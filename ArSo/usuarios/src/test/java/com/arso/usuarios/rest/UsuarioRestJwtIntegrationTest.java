package com.arso.usuarios.rest;

import com.arso.auth.JwtUtils;
import com.arso.repository.RepositoryFactory;
import com.arso.service.FactoriaServicios;
import com.arso.usuarios.domain.Usuario;
import com.arso.usuarios.repository.UsuarioRepositoryJPA;
import com.arso.usuarios.service.ServicioUsuarios;
import com.arso.usuarios.service.ServicioUsuariosTestImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class UsuarioRestJwtIntegrationTest {

    private static HttpServer server;
    private static String baseUrl;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final UsuarioRepositoryJPA usuarioRepo = RepositoryFactory.getRepositorio(Usuario.class);

    @BeforeAll
    static void startServer() throws Exception {
        useNoOpServicioUsuarios();
        assertInstanceOf(ServicioUsuariosTestImpl.class, FactoriaServicios.getServicio(ServicioUsuarios.class));
        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        URI baseUri = URI.create("http://localhost:" + port + "/api/");
        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, new UsuariosApplication(), false);
        server.start();
        baseUrl = baseUri.toString();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        List<Usuario> usuarios = usuarioRepo.findAll();
        for (Usuario usuario : usuarios) {
            usuarioRepo.delete(usuario);
        }
    }

    @Test
    void credenciales_devuelveUsuarioO401() throws Exception {
        seedUser("Ana", "Lopez", "ana@login.test", "clave-ok", "1995-02-10", "600123123", false);

        HttpResponse<String> loginOk = verificarCredenciales("ana@login.test", "clave-ok");
        assertEquals(200, loginOk.statusCode(), loginOk.body());
        Map<String, Object> payload = OBJECT_MAPPER.readValue(loginOk.body(), new TypeReference<Map<String, Object>>() {});
        assertEquals("ana@login.test", payload.get("email"));

        HttpResponse<String> loginFail = verificarCredenciales("ana@login.test", "clave-mal");
        assertEquals(401, loginFail.statusCode(), loginFail.body());
    }

    @Test
    void endpointsProtegidosRequierenJwt() throws Exception {
        String userId = seedUser("Bob", "Diaz", "bob@jwt.test", "clave", "1990-03-11", "611111111", false);

        HttpResponse<String> listSinJwt = get("/usuarios", null);
        assertEquals(401, listSinJwt.statusCode(), listSinJwt.body());

        HttpResponse<String> getSinJwt = get("/usuarios/" + userId, null);
        assertEquals(401, getSinJwt.statusCode(), getSinJwt.body());
    }

    @Test
    void modificarUsuarioSoloPermitePropietarioOAdmin() throws Exception {
        String ownerId = seedUser("Owner", "Uno", "owner@rule.test", "owner123", "1993-01-01", "622222222", false);
        seedUser("Other", "Dos", "other@rule.test", "other123", "1992-01-01", "633333333", false);
        String adminId = seedUser("Admin", "Tres", "admin@rule.test", "admin123", "1991-01-01", "644444444", true);

        String ownerToken = obtainToken("owner@rule.test", "owner123");
        String otherToken = obtainToken("other@rule.test", "other123");
        String adminToken = obtainToken("admin@rule.test", "admin123");

        String body = userJson("Owner", "Actualizado", "owner@rule.test", "owner123", "1993-01-01", "655555555");
        HttpResponse<String> forbidden = put("/usuarios/" + ownerId, body, otherToken);
        assertEquals(403, forbidden.statusCode(), forbidden.body());

        HttpResponse<String> ownerAllowed = put("/usuarios/" + ownerId, body, ownerToken);
        assertEquals(200, ownerAllowed.statusCode(), ownerAllowed.body());

        String adminBody = userJson("Owner", "AdminEdit", "owner@rule.test", "owner123", "1993-01-01", "666666666");
        HttpResponse<String> adminAllowed = put("/usuarios/" + ownerId, adminBody, adminToken);
        assertEquals(200, adminAllowed.statusCode(), adminAllowed.body());
    }

    @Test
    void obtenerNombreEsPublico() throws Exception {
        String userId = seedUser("Publico", "Nombre", "publico@name.test", "clave", "1998-04-20", "677777777", false);

        HttpResponse<String> response = get("/usuarios/" + userId + "/nombre", null);
        assertEquals(200, response.statusCode(), response.body());

        Map<String, String> payload = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Map<String, String>>() {
        });
        assertEquals(userId, payload.get("id"));
        assertEquals("Publico", payload.get("nombre"));
    }

    @Test
    void erroresComunesMapeados_jsonMalformadoFechaInvalidaYValidacion() throws Exception {
        HttpResponse<String> malformedJson = postRawJson("/usuarios", "{\"nombre\":\"X\"", null);
        assertEquals(400, malformedJson.statusCode(), malformedJson.body());

        String fechaInvalida = userJson("Fecha", "Invalida", "fecha@error.test", "clave", "20-04-1998", "688888888");
        HttpResponse<String> badDate = postRawJson("/usuarios", fechaInvalida, null);
        assertEquals(400, badDate.statusCode(), badDate.body());
        assertTrue(badDate.body().contains("Formato de fecha no válido"));

        String sinNombre = userJson("", "SinNombre", "validacion@error.test", "clave", "1998-04-20", "699999999");
        HttpResponse<String> validationError = postRawJson("/usuarios", sinNombre, null);
        assertEquals(400, validationError.statusCode(), validationError.body());
        assertTrue(validationError.body().contains("El nombre es obligatorio"));
    }

    private static void useNoOpServicioUsuarios() throws Exception {
        Field serviciosField = FactoriaServicios.class.getDeclaredField("servicios");
        serviciosField.setAccessible(true);
        Map<Class<?>, Object> servicios = (Map<Class<?>, Object>) serviciosField.get(null);
        servicios.clear();
        servicios.put(ServicioUsuarios.class, new ServicioUsuariosTestImpl());
    }

    private String seedUser(String nombre, String apellidos, String email, String clave, String fechaNacimiento, String telefono, boolean administrador) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setClave(clave);
        usuario.setFechaNacimiento(DATE_FORMAT.parse(fechaNacimiento));
        usuario.setTelefono(telefono);
        usuario.setAdministrador(administrador);
        usuario.setRol(administrador ? "ADMIN" : "USER");
        usuario.setContadorCompras(0);
        usuario.setContadorVentas(0);
        return usuarioRepo.save(usuario).getId();
    }

    private HttpResponse<String> verificarCredenciales(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "usuarios/credenciales"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String obtainToken(String email, String password) throws Exception {
        HttpResponse<String> response = verificarCredenciales(email, password);
        assertEquals(200, response.statusCode(), response.body());
        Map<String, Object> user = OBJECT_MAPPER.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", user.get("id"));
        claims.put("email", user.get("email"));
        claims.put("nombre", user.get("nombre"));
        Object administrador = user.get("administrador");
        claims.put("roles", Boolean.TRUE.equals(administrador) ? "ADMIN" : "USER");
        return JwtUtils.generateToken(claims);
    }

    private HttpResponse<String> get(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + trimInitialSlash(path))).GET();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> put(String path, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + trimInitialSlash(path)))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postRawJson(String path, String body, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + trimInitialSlash(path)))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String userJson(String nombre, String apellidos, String email, String clave, String fechaNacimiento, String telefono) {
        return "{"
            + "\"nombre\":\"" + nombre + "\","
            + "\"apellidos\":\"" + apellidos + "\","
            + "\"email\":\"" + email + "\","
            + "\"clave\":\"" + clave + "\","
            + "\"fechaNacimiento\":\"" + fechaNacimiento + "\","
            + "\"telefono\":\"" + telefono + "\""
            + "}";
    }

    private static String trimInitialSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
