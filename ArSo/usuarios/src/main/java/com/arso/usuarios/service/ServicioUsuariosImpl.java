package com.arso.usuarios.service;

import com.arso.usuarios.adapter.rabbitmq.RabbitMqEventPublisherAdapter;
import com.arso.usuarios.domain.Usuario;
import com.arso.usuarios.event.UsuarioCreadoEvent;
import com.arso.usuarios.event.UsuarioModificadoEvent;
import com.arso.usuarios.port.EventPublisherPort;
import com.arso.usuarios.repository.UsuarioRepositoryJPA;
import com.arso.repository.EntityNotFound;
import com.arso.repository.RepositoryException;
import com.arso.repository.RepositoryFactory;
import com.arso.usuarios.rest.error.UsuarioNoEncontradoException;

import java.util.Date;
import java.util.List;

public class ServicioUsuariosImpl implements ServicioUsuarios {

    private final UsuarioRepositoryJPA usuarioRepo = RepositoryFactory.getRepositorio(Usuario.class);
    private final EventPublisherPort eventPublisher;

    public ServicioUsuariosImpl() {
        this(new RabbitMqEventPublisherAdapter());
    }

    ServicioUsuariosImpl(EventPublisherPort eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String altaUsuario(String nombre, String apellidos, String email, String clave, Date fechaNacimiento, String telefono) {
        // Validar campos obligatorios
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (clave == null || clave.trim().isEmpty()) {
            throw new IllegalArgumentException("La clave es obligatoria");
        }

        // Comprobar email duplicado
        if (usuarioRepo.existeEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setEmail(email);
            usuario.setClave(clave);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setTelefono(telefono);
            usuario.setAdministrador(false);
            usuario.setRol("USER");
            usuario.setContadorCompras(0);
            usuario.setContadorVentas(0);

            String idUsuario = usuarioRepo.add(usuario);
            usuario.setId(idUsuario);
            eventPublisher.publicarUsuarioCreado(UsuarioCreadoEvent.from(usuario));
            return idUsuario;
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al dar de alta el usuario", e);
        }
    }

    @Override
    public void modificarUsuario(String idUsuario, String nombre, String apellidos, String clave, Date fechaNacimiento, String telefono) {
        Usuario usuario = usuarioRepo.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException(idUsuario);
        }
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setClave(clave);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setTelefono(telefono);
        try {
            usuarioRepo.update(usuario);
            eventPublisher.publicarUsuarioModificado(UsuarioModificadoEvent.from(usuario));
        } catch (EntityNotFound e) {
            throw new UsuarioNoEncontradoException(idUsuario);
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al modificar el usuario", e);
        }
    }

    @Override
    public void registrarCompraventa(String idComprador, String idVendedor) {
        if (idComprador == null || idComprador.trim().isEmpty() || idVendedor == null || idVendedor.trim().isEmpty()) {
            return;
        }

        Usuario comprador = usuarioRepo.findById(idComprador);
        Usuario vendedor = usuarioRepo.findById(idVendedor);
        if (comprador == null || vendedor == null) {
            return;
        }

        try {
            if (idComprador.equals(idVendedor)) {
                comprador.setContadorCompras(comprador.getContadorCompras() + 1);
                comprador.setContadorVentas(comprador.getContadorVentas() + 1);
                usuarioRepo.update(comprador);
                return;
            }

            comprador.setContadorCompras(comprador.getContadorCompras() + 1);
            vendedor.setContadorVentas(vendedor.getContadorVentas() + 1);
            usuarioRepo.update(comprador);
            usuarioRepo.update(vendedor);
        } catch (EntityNotFound e) {
            throw new UsuarioNoEncontradoException(e.getMessage());
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al actualizar contadores de usuario", e);
        }
    }

    @Override
    public void registrarValoracion(String idUsuarioValorado, String rolUsuarioValorado, int puntuacion) {
        if (idUsuarioValorado == null || idUsuarioValorado.trim().isEmpty() || rolUsuarioValorado == null) {
            return;
        }

        Usuario usuario = usuarioRepo.findById(idUsuarioValorado);
        if (usuario == null) {
            return;
        }

        try {
            if ("COMPRADOR".equalsIgnoreCase(rolUsuarioValorado)) {
                int oldCount = usuario.getNumeroValoracionesComoComprador();
                double oldAvg = usuario.getValoracionMediaComoComprador();
                double newAvg = ((oldAvg * oldCount) + puntuacion) / (oldCount + 1);
                usuario.setNumeroValoracionesComoComprador(oldCount + 1);
                usuario.setValoracionMediaComoComprador(newAvg);
            } else if ("VENDEDOR".equalsIgnoreCase(rolUsuarioValorado)) {
                int oldCount = usuario.getNumeroValoracionesComoVendedor();
                double oldAvg = usuario.getValoracionMediaComoVendedor();
                double newAvg = ((oldAvg * oldCount) + puntuacion) / (oldCount + 1);
                usuario.setNumeroValoracionesComoVendedor(oldCount + 1);
                usuario.setValoracionMediaComoVendedor(newAvg);
            }
            usuarioRepo.update(usuario);
        } catch (EntityNotFound e) {
            throw new UsuarioNoEncontradoException(idUsuarioValorado);
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al actualizar valoraciones de usuario", e);
        }
    }

    @Override
    public Usuario getUsuario(String idUsuario) {
        Usuario usuario = usuarioRepo.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException(idUsuario);
        }
        return usuario;
    }

    @Override
    public List<Usuario> getUsuarios() {
        return usuarioRepo.findAll();
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepo.obtenerPorEmail(email);
    }

    @Override
    public Usuario obtenerPorGithubId(String githubId) {
        return usuarioRepo.obtenerPorGithubId(githubId);
    }

    @Override
    public void actualizarGithubId(String idUsuario, String githubId) {
        if (githubId == null || githubId.trim().isEmpty()) {
            throw new IllegalArgumentException("El githubId es obligatorio");
        }

        Usuario usuario = usuarioRepo.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException(idUsuario);
        }

        usuario.setGithubId(githubId);
        try {
            usuarioRepo.update(usuario);
            eventPublisher.publicarUsuarioModificado(UsuarioModificadoEvent.from(usuario));
        } catch (EntityNotFound e) {
            throw new UsuarioNoEncontradoException(idUsuario);
        } catch (RepositoryException e) {
            throw new RuntimeException("Error al actualizar githubId del usuario", e);
        }
    }
}
