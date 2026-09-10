package com.arso.compraventas.rest;

import com.arso.compraventas.domain.Compraventa;
import com.arso.compraventas.port.EventPublisherPort;
import com.arso.compraventas.port.ProductosPort;
import com.arso.compraventas.port.UsuariosPort;
import com.arso.compraventas.port.dto.ProductoRemotoDto;
import com.arso.compraventas.port.dto.UsuarioRemotoDto;
import com.arso.compraventas.repository.CompraventaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CompraventasControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompraventaRepository repository;

    @MockBean
    private ProductosPort productosPort;

    @MockBean
    private UsuariosPort usuariosPort;

    @MockBean
    private EventPublisherPort eventPublisherPort;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        doNothing().when(eventPublisherPort).publicarCompraventaCreada(ArgumentMatchers.any());
    }

    @Test
    void crearCompraventa_devuelve201YPersistencia() throws Exception {
        when(productosPort.getProducto("prod-1")).thenReturn(producto("prod-1", "vend-1", false));
        when(usuariosPort.getNombreUsuario("comp-1")).thenReturn(usuario("comp-1", "Ana", "Buyer"));
        when(usuariosPort.getNombreUsuario("vend-1")).thenReturn(usuario("vend-1", "Luis", "Seller"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("idProducto", "prod-1");
        payload.put("idComprador", "comp-1");

        mockMvc.perform(post("/compraventas")
                .header("Authorization", bearerFor("comp-1", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/compraventas/")));

        assertEquals(1, repository.count());
        Compraventa saved = repository.findAll().get(0);
        assertEquals("prod-1", saved.getIdProducto());
        assertEquals("Ana Buyer", saved.getNombreComprador());
        assertEquals("Luis Seller", saved.getNombreVendedor());
    }

    @Test
    void crearCompraventa_productoVendidoDevuelve400() throws Exception {
        when(productosPort.getProducto("prod-vendido")).thenReturn(producto("prod-vendido", "vend-1", true));

        Map<String, Object> payload = new HashMap<>();
        payload.put("idProducto", "prod-vendido");
        payload.put("idComprador", "comp-1");

        mockMvc.perform(post("/compraventas")
                .header("Authorization", bearerFor("comp-1", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensaje", containsString("ya ha sido vendido")));
    }

    @Test
    void crearCompraventa_sinToken_devuelve401() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idProducto", "prod-1");
        payload.put("idComprador", "comp-1");

        mockMvc.perform(post("/compraventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void crearCompraventa_compradorDistintoDelToken_devuelve403() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idProducto", "prod-1");
        payload.put("idComprador", "comp-1");

        mockMvc.perform(post("/compraventas")
                .header("Authorization", bearerFor("otro-usuario", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden());
    }

    @Test
    void consultarPorComprador_devuelvePaginado() throws Exception {
        repository.save(compraventa("c1", "comp-9", "vend-2"));
        repository.save(compraventa("c2", "comp-9", "vend-3"));
        repository.save(compraventa("c3", "comp-x", "vend-3"));

        mockMvc.perform(get("/compraventas/comprador/{idComprador}", "comp-9")
                .header("Authorization", bearerFor("comp-9", "USER"))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(2))
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void consultarPorVendedor_devuelvePaginado() throws Exception {
        repository.save(compraventa("v1", "comp-1", "vend-9"));
        repository.save(compraventa("v2", "comp-2", "vend-9"));
        repository.save(compraventa("v3", "comp-3", "vend-x"));

        mockMvc.perform(get("/compraventas/vendedor/{idVendedor}", "vend-9")
                .header("Authorization", bearerFor("vend-9", "USER"))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void consultarPorCompradorYVendedor_requiereAdministrador() throws Exception {
        repository.save(compraventa("a1", "comp-1", "vend-1"));

        mockMvc.perform(get("/compraventas")
                .header("Authorization", bearerFor("comp-1", "USER"))
                .param("idComprador", "comp-1")
                .param("idVendedor", "vend-1"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/compraventas")
                .header("Authorization", bearerFor("admin-1", "ADMIN"))
                .param("idComprador", "comp-1")
                .param("idVendedor", "vend-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    private ProductoRemotoDto producto(String idProducto, String idVendedor, boolean vendido) {
        ProductoRemotoDto dto = new ProductoRemotoDto();
        dto.setId(idProducto);
        dto.setTitulo("Producto " + idProducto);
        dto.setPrecio(100.0);
        dto.setVendido(vendido);

        ProductoRemotoDto.VendedorDto vendedor = new ProductoRemotoDto.VendedorDto();
        vendedor.setId(idVendedor);
        vendedor.setNombre("Vendedor");
        vendedor.setApellidos("Demo");
        dto.setVendedor(vendedor);

        ProductoRemotoDto.RecogidaDto recogida = new ProductoRemotoDto.RecogidaDto();
        recogida.setDescripcion("Punto de recogida");
        dto.setRecogida(recogida);
        return dto;
    }

    private UsuarioRemotoDto usuario(String id, String nombre, String apellidos) {
        UsuarioRemotoDto dto = new UsuarioRemotoDto();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setApellidos(apellidos);
        return dto;
    }

    private Compraventa compraventa(String id, String idComprador, String idVendedor) {
        Compraventa c = new Compraventa();
        c.setId(id);
        c.setIdProducto("prod-" + id);
        c.setTitulo("Producto " + id);
        c.setPrecio(20.0);
        c.setRecogida("Punto");
        c.setIdComprador(idComprador);
        c.setNombreComprador("Comprador");
        c.setIdVendedor(idVendedor);
        c.setNombreVendedor("Vendedor");
        c.setFecha(Instant.now());
        return c;
    }

    private String bearerFor(String sub, String role) throws Exception {
        return "Bearer " + buildToken(sub, role);
    }

    private String buildToken(String sub, String role) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", sub);
        payload.put("roles", role);

        String headerJson = "{\"alg\":\"none\",\"typ\":\"JWT\"}";
        String payloadJson = objectMapper.writeValueAsString(payload);
        String header = base64Url(headerJson);
        String body = base64Url(payloadJson);

        return header + "." + body + ".";
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
