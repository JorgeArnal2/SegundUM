package com.arso.productos.rest;

import com.arso.productos.domain.Categoria;
import com.arso.productos.repository.CategoriaRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoriasControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepositoryJPA categoriaRepository;

    private Categoria deportes;
    private Categoria ciclismo;

    @BeforeEach
    void setUp() {
        deportes = new Categoria();
        deportes.setId("cat-deportes");
        deportes.setNombre("Deportes");
        deportes.setDescripcion("Articulos deportivos");
        deportes.setRuta("/deportes");

        ciclismo = new Categoria();
        ciclismo.setId("cat-ciclismo");
        ciclismo.setNombre("Ciclismo");
        ciclismo.setDescripcion("Material de ciclismo");
        ciclismo.setRuta("/deportes/ciclismo");
        deportes.addSubcategoria(ciclismo);
        categoriaRepository.save(deportes);
    }

    @Test
    void getCategoriasRaiz_sinToken_devuelve200() throws Exception {
        mockMvc.perform(get("/categorias"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(deportes.getId()))
            .andExpect(jsonPath("$[0].nombre").value("Deportes"))
            .andExpect(jsonPath("$[0].ruta").value("/deportes"));
    }

    @Test
    void getCategoria_sinToken_devuelveSubcategorias() throws Exception {
        mockMvc.perform(get("/categorias/{id}", deportes.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(deportes.getId()))
            .andExpect(jsonPath("$.subcategorias[0].id").value(ciclismo.getId()))
            .andExpect(jsonPath("$.subcategorias[0].nombre").value("Ciclismo"));
    }

    @Test
    void getCategoria_inexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/categorias/{id}", "no-existe"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }
}
