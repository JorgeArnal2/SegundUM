package com.arso.usuarios.event;

import com.arso.usuarios.domain.Usuario;

import java.time.Instant;

public class UsuarioModificadoEvent extends DomainEvent {

    private final String idUsuario;
    private final String email;
    private final String nombre;
    private final String apellidos;

    private UsuarioModificadoEvent(String idUsuario, String email, String nombre, String apellidos, String occurredOn) {
        super("usuario-modificado", occurredOn);
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public static UsuarioModificadoEvent from(Usuario usuario) {
        return new UsuarioModificadoEvent(
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
