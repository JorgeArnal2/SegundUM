package com.arso.compraventas.e2e;

import com.arso.compraventas.CompraventasApplication;
import com.arso.compraventas.domain.Compraventa;
import com.arso.compraventas.repository.CompraventaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = CompraventasApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CompraventasCrossServiceE2ETest {

    private static final Object START_LOCK = new Object();
    private static final Path ROOT = Path.of(System.getProperty("user.dir")).getParent();
    private static final Path PRODUCTOS_LOG = ROOT.resolve("compraventas").resolve("target").resolve("productos-e2e.log");
    private static final Path USUARIOS_LOG = ROOT.resolve("compraventas").resolve("target").resolve("usuarios-e2e.log");
    private static final Path PRODUCTOS_DB = ROOT.resolve("compraventas").resolve("target").resolve("productos-e2e-db");

    private static boolean started = false;
    private static Process productosProcess;
    private static Process usuariosProcess;
    private static int productosPort;
    private static int usuariosPort;
    private static String productosJdbcUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompraventaRepository compraventaRepository;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        startExternalServices();
        registry.add("microservicios.productos.base-url", () -> "http://localhost:" + productosPort + "/");
        registry.add("microservicios.usuarios.base-url", () -> "http://localhost:" + usuariosPort + "/api/");
    }

    @BeforeEach
    void resetState() throws Exception {
        compraventaRepository.deleteAll();
        resetProductosData();
    }

    @AfterAll
    static void stopExternalServices() {
        stopProcess(productosProcess);
        stopProcess(usuariosProcess);
        started = false;
    }

    @Test
    void happyPath_creacionYConsultaDeCompraventaEntreServicios() throws Exception {
        String vendedorId = crearUsuario("Laura", "Vendedora", "laura-" + UUID.randomUUID() + "@example.com");
        String compradorId = crearUsuario("Marcos", "Comprador", "marcos-" + UUID.randomUUID() + "@example.com");

        insertarVendedorEnProductos(vendedorId, "laura-vendedora@example.com", "Laura", "Vendedora");
        String productoId = crearProducto(vendedorId);

        Map<String, Object> payloadCompraventa = new LinkedHashMap<>();
        payloadCompraventa.put("idProducto", productoId);
        payloadCompraventa.put("idComprador", compradorId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(buildToken(compradorId, "USER"));

        ResponseEntity<Void> crearCompraventaResponse = restTemplate.exchange(
            "/compraventas",
            HttpMethod.POST,
            new HttpEntity<>(payloadCompraventa, headers),
            Void.class
        );

        assertEquals(HttpStatus.CREATED, crearCompraventaResponse.getStatusCode());
        assertEquals(1, compraventaRepository.count());

        Compraventa saved = compraventaRepository.findAll().get(0);
        assertEquals(productoId, saved.getIdProducto());
        assertEquals(compradorId, saved.getIdComprador());
        assertEquals(vendedorId, saved.getIdVendedor());
        assertEquals("Marcos Comprador", saved.getNombreComprador());
        assertEquals("Laura Vendedora", saved.getNombreVendedor());

        ResponseEntity<JsonNode> comprasResponse = restTemplate.exchange(
            "/compraventas/comprador/" + compradorId + "?page=0&size=10",
            HttpMethod.GET,
            new HttpEntity<>(comprasHeaders(compradorId)),
            JsonNode.class
        );

        assertEquals(HttpStatus.OK, comprasResponse.getStatusCode());
        assertNotNull(comprasResponse.getBody());
        assertEquals(1, comprasResponse.getBody().path("page").path("totalElements").asInt());
        assertTrue(comprasResponse.getBody().toString().contains(productoId));
    }

    private static void startExternalServices() {
        synchronized (START_LOCK) {
            if (started) {
                return;
            }

            try {
                Files.createDirectories(PRODUCTOS_LOG.getParent());
            } catch (Exception e) {
                throw new IllegalStateException("No se pudo preparar carpeta target para logs E2E", e);
            }

            productosPort = findFreePort();
            usuariosPort = 8080;
            productosJdbcUrl = "jdbc:h2:file:" + PRODUCTOS_DB.toAbsolutePath().toString().replace("\\", "/")
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";

            productosProcess = startProductosProcess();
            waitForHttpReady("http://localhost:" + productosPort + "/productos", Duration.ofMinutes(2));

            usuariosProcess = startUsuariosProcess();
            waitForHttpReady("http://localhost:" + usuariosPort + "/api/usuarios", Duration.ofMinutes(2));

            started = true;
        }
    }

    private static Process startProductosProcess() {
        String cmd = String.join(" ",
            "mvn -f \"" + ROOT.resolve("productos").resolve("pom.xml") + "\"",
            "spring-boot:run",
            "-Dspring-boot.run.useTestClasspath=true",
            "-Dspring-boot.run.jvmArguments=\"-Dfile.encoding=UTF-8\"",
            "-Dspring-boot.run.arguments=\"--server.port=" + productosPort
                + " --spring.datasource.url=" + productosJdbcUrl
                + " --spring.datasource.username=sa"
                + " --spring.datasource.password="
                + " --spring.datasource.driver-class-name=org.h2.Driver"
                + " --spring.jpa.hibernate.ddl-auto=create-drop"
                + " --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
                + " --spring.rabbitmq.listener.simple.auto-startup=false\""
        );
        return startProcess(cmd, PRODUCTOS_LOG.toFile());
    }

    private static Process startUsuariosProcess() {
        String cmd = String.join(" ",
            "mvn -f \"" + ROOT.resolve("usuarios").resolve("pom.xml") + "\"",
            "-Dexec.classpathScope=test",
            "exec:java",
            "-Dexec.mainClass=com.arso.Main"
        );
        return startProcess(cmd, USUARIOS_LOG.toFile());
    }

    private static Process startProcess(String cmd, File logFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
            pb.directory(ROOT.toFile());
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            Process process = pb.start();
            Thread.sleep(500);
            if (!process.isAlive()) {
                throw new IllegalStateException("El proceso arrancó y finalizó inmediatamente: " + cmd);
            }
            return process;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo arrancar proceso E2E: " + cmd, e);
        }
    }

    private static void waitForHttpReady(String url, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        Exception lastError = null;

        while (Instant.now().isBefore(deadline)) {
            try {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() < 500) {
                    return;
                }
            } catch (Exception e) {
                lastError = e;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrumpido esperando disponibilidad HTTP de " + url, interruptedException);
            }
        }

        throw new IllegalStateException("El servicio no estuvo disponible a tiempo: " + url, lastError);
    }

    private String crearUsuario(String nombre, String apellidos, String email) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nombre", nombre);
        payload.put("apellidos", apellidos);
        payload.put("email", email);
        payload.put("clave", "clave123");
        payload.put("fechaNacimiento", "1994-03-02");
        payload.put("telefono", "600123123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.exchange(
            "http://localhost:" + usuariosPort + "/api/usuarios",
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Void.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        return extraerUltimoSegmento(response.getHeaders().getLocation());
    }

    private String crearProducto(String vendedorId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("titulo", "Producto E2E");
        payload.put("descripcion", "Producto creado durante test E2E");
        payload.put("precio", 250.0);
        payload.put("estado", "NUEVO");
        payload.put("idCategoria", "cat-e2e");
        payload.put("envioDisponible", true);
        payload.put("idVendedor", vendedorId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(buildToken(vendedorId, "USER"));

        ResponseEntity<Void> response = restTemplate.exchange(
            "http://localhost:" + productosPort + "/productos",
            HttpMethod.POST,
            new HttpEntity<>(payload, headers),
            Void.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        return extraerUltimoSegmento(response.getHeaders().getLocation());
    }

    private static void resetProductosData() throws Exception {
        try (Connection connection = DriverManager.getConnection(productosJdbcUrl, "sa", "");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM productos");
            statement.executeUpdate("DELETE FROM categorias");
            statement.executeUpdate("DELETE FROM usuarios");
            statement.executeUpdate("INSERT INTO categorias(id_categoria, nombre, descripcion, ruta, parent_id) VALUES ('cat-e2e', 'Categoria E2E', 'Categoria para pruebas E2E', '/categoria-e2e', NULL)");
        }
    }

    private static void insertarVendedorEnProductos(String id, String email, String nombre, String apellidos) throws Exception {
        try (Connection connection = DriverManager.getConnection(productosJdbcUrl, "sa", "");
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO usuarios(id_usuario, email, nombre, apellidos) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, id);
            statement.setString(2, email);
            statement.setString(3, nombre);
            statement.setString(4, apellidos);
            statement.executeUpdate();
        }
    }

    private static String extraerUltimoSegmento(URI uri) {
        String path = uri.getPath();
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    private static HttpHeaders comprasHeaders(String compradorId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(buildToken(compradorId, "USER"));
        return headers;
    }

    private static String buildToken(String sub, String role) {
        String headerJson = "{\"alg\":\"none\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + sub + "\",\"roles\":\"" + role + "\"}";
        String header = base64Url(headerJson);
        String payload = base64Url(payloadJson);
        return header + "." + payload + ".";
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo reservar un puerto libre para el test E2E", e);
        }
    }

    private static void stopProcess(Process process) {
        if (process == null || !process.isAlive()) {
            return;
        }
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
