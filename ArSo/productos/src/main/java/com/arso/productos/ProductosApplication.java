package com.arso.productos;

import com.arso.productos.domain.Categoria;
import com.arso.productos.domain.UsuarioResumen;
import com.arso.productos.repository.CategoriaRepositoryJPA;
import com.arso.productos.repository.UsuarioResumenRepositoryJPA;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = "com.arso")
@EntityScan(basePackages = "com.arso.productos.domain")
public class ProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductosApplication.class, args);
    }

    @Bean
    @Profile("docker")
    public CommandLineRunner inicializarCategorias(CategoriaRepositoryJPA categoriaRepo) {
        return args -> {
            if (!categoriaRepo.findRootCategories().isEmpty()) {
                return;
            }
            Categoria categoria = new Categoria();
            categoria.setId("cat-1");
            categoria.setNombre("General");
            categoria.setDescripcion("Categoria general de productos");
            categoria.setRuta("/general");
            categoriaRepo.save(categoria);
            System.out.println("Categoria inicial creada para despliegue Docker.");
        };
    }

    @Bean
    @Profile("demo")
    public CommandLineRunner inicializarDatos(UsuarioResumenRepositoryJPA usuarioRepo, com.arso.productos.repository.CategoriaRepositoryJPA categoriaRepo) {
        return args -> {
            if (categoriaRepo.findAll().isEmpty()) {
                System.out.println("Inicializando catálogo de categorías...");
                
                com.arso.productos.domain.Categoria deportes = crearCategoria("cat-deportes", "Deportes", "Artículos deportivos", "/deportes", null);
                crearCategoria("cat-ciclismo", "Ciclismo", "Bicicletas y accesorios", "/deportes/ciclismo", deportes);
                crearCategoria("cat-fitness", "Fitness", "Máquinas y pesas", "/deportes/fitness", deportes);
                
                com.arso.productos.domain.Categoria electronica = crearCategoria("cat-electronica", "Electrónica", "Dispositivos electrónicos", "/electronica", null);
                crearCategoria("cat-moviles", "Móviles", "Smartphones y accesorios", "/electronica/moviles", electronica);
                crearCategoria("cat-informatica", "Informática", "Ordenadores y periféricos", "/electronica/informatica", electronica);
                
                com.arso.productos.domain.Categoria hogar = crearCategoria("cat-hogar", "Hogar", "Muebles y decoración", "/hogar", null);
                
                categoriaRepo.save(deportes);
                categoriaRepo.save(electronica);
                categoriaRepo.save(hogar);
                
                // Añadimos también el cat-001 por retrocompatibilidad con las pruebas que ya estemos haciendo
                com.arso.productos.domain.Categoria cat001 = crearCategoria("cat-001", "Miscelánea", "Otros productos", "/otros", null);
                categoriaRepo.save(cat001);
                
                System.out.println("Categorías inicializadas correctamente.");
            }
            if (usuarioRepo.count() > 0) {
                System.out.println("Los usuarios ya están inicializados.");
                return;
            }

            UsuarioResumen[] usuarios = {
                crearUsuario("u1", "ana.garcia@email.com", "Ana", "García López"),
                crearUsuario("u2", "carlos.martin@email.com", "Carlos", "Martín Pérez"),
                crearUsuario("u3", "lucia.fernandez@email.com", "Lucía", "Fernández Torres"),
                crearUsuario("u4", "pedro.sanchez@email.com", "Pedro", "Sánchez Ruiz"),
                crearUsuario("u5", "maria.lopez@email.com", "María", "López González")
            };

            for (UsuarioResumen u : usuarios) {
                usuarioRepo.save(u);
                System.out.println("Usuario insertado: " + u.getEmail());
            }
        };
    }

    private UsuarioResumen crearUsuario(String id, String email, String nombre, String apellidos) {
        UsuarioResumen u = new UsuarioResumen();
        u.setId(id);
        u.setEmail(email);
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        return u;
    }

    private com.arso.productos.domain.Categoria crearCategoria(String id, String nombre, String descripcion, String ruta, com.arso.productos.domain.Categoria parent) {
        com.arso.productos.domain.Categoria c = new com.arso.productos.domain.Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setRuta(ruta);
        if (parent != null) {
            parent.addSubcategoria(c);
        }
        return c;
    }
}
