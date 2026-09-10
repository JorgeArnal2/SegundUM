package com.arso.productos.rest;

import com.arso.productos.domain.Categoria;
import com.arso.productos.domain.EstadoProducto;
import com.arso.productos.domain.Producto;
import com.arso.productos.domain.UsuarioResumen;
import com.arso.productos.repository.CategoriaRepositoryJPA;
import com.arso.productos.repository.ProductoRepositoryJPA;
import com.arso.productos.repository.UsuarioResumenRepositoryJPA;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductosControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepositoryJPA categoriaRepository;

    @Autowired
    private UsuarioResumenRepositoryJPA usuarioRepository;

    @Autowired
    private ProductoRepositoryJPA productoRepository;

    private Categoria categoria;
    private UsuarioResumen vendedor;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId("cat-1");
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Categoria de prueba");
        categoria.setRuta("/electronica");
        categoriaRepository.save(categoria);

        vendedor = new UsuarioResumen();
        vendedor.setId("usr-1");
        vendedor.setEmail("seller@example.com");
        vendedor.setNombre("Seller");
        vendedor.setApellidos("Test");
        usuarioRepository.save(vendedor);
    }

    @Test
    void createProducto_devuelve201YLocation() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("titulo", "Portatil");
        payload.put("descripcion", "Portatil de pruebas");
        payload.put("precio", 899.99);
        payload.put("estado", "NUEVO");
        payload.put("idCategoria", categoria.getId());
        payload.put("envioDisponible", true);
        payload.put("idVendedor", vendedor.getId());

        mockMvc.perform(post("/productos")
                .header("Authorization", bearerFor(vendedor.getId(), "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/productos/")));
    }

    @Test
    void getProducto_devuelveDTOCompleto() throws Exception {
        Producto producto = crearProducto("Camara", "Camara compacta", 120.0);

        mockMvc.perform(get("/productos/{id}", producto.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(producto.getId()))
            .andExpect(jsonPath("$.titulo").value("Camara"))
            .andExpect(jsonPath("$.categoria.id").value(categoria.getId()))
            .andExpect(jsonPath("$.vendedor.id").value(vendedor.getId()));
    }

    @Test
    void createProducto_sinToken_devuelve401() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("titulo", "Portatil");
        payload.put("descripcion", "Portatil de pruebas");
        payload.put("precio", 899.99);
        payload.put("estado", "NUEVO");
        payload.put("idCategoria", categoria.getId());
        payload.put("envioDisponible", true);
        payload.put("idVendedor", vendedor.getId());

        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createProducto_vendedorDistintoDelToken_devuelve403() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("titulo", "Portatil");
        payload.put("descripcion", "Portatil de pruebas");
        payload.put("precio", 899.99);
        payload.put("estado", "NUEVO");
        payload.put("idCategoria", categoria.getId());
        payload.put("envioDisponible", true);
        payload.put("idVendedor", vendedor.getId());

        mockMvc.perform(post("/productos")
                .header("Authorization", bearerFor("otro-usuario", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isForbidden());
    }

    @Test
    void createProducto_payloadInvalidoDevuelve400ConError() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("descripcion", "Falta titulo");
        payload.put("precio", 50.0);
        payload.put("estado", "NUEVO");
        payload.put("idCategoria", categoria.getId());
        payload.put("envioDisponible", true);
        payload.put("idVendedor", vendedor.getId());

        mockMvc.perform(post("/productos")
                .header("Authorization", bearerFor(vendedor.getId(), "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("titulo")));
    }

    private Producto crearProducto(String titulo, String descripcion, Double precio) {
        Producto producto = new Producto();
        producto.setTitulo(titulo);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setEstado(EstadoProducto.NUEVO);
        producto.setFechaPublicacion(new Date());
        producto.setVisualizaciones(0);
        producto.setEnvioDisponible(true);
        producto.setVendido(false);
        producto.setCategoria(categoria);
        producto.setVendedor(vendedor);
        productoRepository.save(producto);
        return producto;
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
