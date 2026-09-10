package com.arso.usuarios.service;

import com.arso.repository.RepositoryFactory;
import com.arso.usuarios.domain.Usuario;
import com.arso.usuarios.event.UsuarioCreadoEvent;
import com.arso.usuarios.event.UsuarioModificadoEvent;
import com.arso.usuarios.port.EventPublisherPort;
import com.arso.usuarios.repository.UsuarioRepositoryJPA;
import com.arso.usuarios.rest.error.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioUsuariosImplIntegrationTest {

    private final EventPublisherPort noOpPublisher = new EventPublisherPort() {
        @Override
        public void publicarUsuarioCreado(UsuarioCreadoEvent event) {}

        @Override
        public void publicarUsuarioModificado(UsuarioModificadoEvent event) {}
    };

    private UsuarioRepositoryJPA usuarioRepo;
    private ServicioUsuariosImpl servicio;

    @BeforeEach
    void setUp() {
        usuarioRepo = RepositoryFactory.getRepositorio(Usuario.class);
        List<Usuario> usuarios = usuarioRepo.findAll();
        for (Usuario usuario : usuarios) {
            usuarioRepo.delete(usuario);
        }
        servicio = new ServicioUsuariosImpl(noOpPublisher);
    }

    @Test
    void altaUsuario_guardaUsuarioConRolPorDefecto() {
        String id = servicio.altaUsuario(
            "Ana",
            "Lopez",
            "ana@example.com",
            "secreta",
            new Date(),
            "600123123"
        );

        Usuario saved = servicio.getUsuario(id);
        assertEquals("Ana", saved.getNombre());
        assertEquals("ana@example.com", saved.getEmail());
        assertEquals("USER", saved.getRol());
        assertFalse(saved.isAdministrador());
        assertEquals(0, saved.getContadorCompras());
        assertEquals(0, saved.getContadorVentas());
    }

    @Test
    void altaUsuario_fallaSiEmailDuplicado() {
        servicio.altaUsuario(
            "Ana",
            "Lopez",
            "ana.dup@example.com",
            "secreta",
            new Date(),
            "600123123"
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servicio.altaUsuario(
            "Otro",
            "Usuario",
            "ana.dup@example.com",
            "clave2",
            new Date(),
            "600000000"
        ));
        assertTrue(ex.getMessage().contains("Ya existe un usuario con ese email"));
    }

    @Test
    void modificarUsuario_actualizaCampos() {
        String id = servicio.altaUsuario(
            "Pepe",
            "Perez",
            "pepe@example.com",
            "clave",
            new Date(),
            "611111111"
        );

        servicio.modificarUsuario(
            id,
            "Jose",
            "Perez",
            "nuevaClave",
            new Date(),
            "622222222"
        );

        Usuario updated = servicio.getUsuario(id);
        assertEquals("Jose", updated.getNombre());
        assertEquals("nuevaClave", updated.getClave());
        assertEquals("622222222", updated.getTelefono());
    }

    @Test
    void registrarCompraventa_incrementaContadoresCompradorYVendedor() {
        String idComprador = servicio.altaUsuario(
            "Comprador",
            "Uno",
            "comprador@example.com",
            "c1",
            new Date(),
            "633333333"
        );
        String idVendedor = servicio.altaUsuario(
            "Vendedor",
            "Dos",
            "vendedor@example.com",
            "c2",
            new Date(),
            "644444444"
        );

        servicio.registrarCompraventa(idComprador, idVendedor);

        Usuario comprador = servicio.getUsuario(idComprador);
        Usuario vendedor = servicio.getUsuario(idVendedor);
        assertEquals(1, comprador.getContadorCompras());
        assertEquals(0, comprador.getContadorVentas());
        assertEquals(0, vendedor.getContadorCompras());
        assertEquals(1, vendedor.getContadorVentas());
    }

    @Test
    void getUsuario_lanzaExcepcionSiNoExiste() {
        assertThrows(UsuarioNoEncontradoException.class, () -> servicio.getUsuario("no-existe"));
    }
}
