package com.arso.usuarios.event;

import com.arso.usuarios.domain.Usuario;

import java.time.Instant;

public class UsuarioCreadoEvent extends DomainEvent {

    private final String idUsuario;
    private final String email;
    private final String nombre;
    private final String apellidos;

    private UsuarioCreadoEvent(String idUsuario, String email, String nombre, String apellidos, String occurredOn) {
        super("usuario-creado", occurredOn);
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public static UsuarioCreadoEvent from(Usuario usuario) {
        return new UsuarioCreadoEvent(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNombre(),
            usuario.getApellidos(),
            Instant.now().toString()
        );
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
}
