package com.arso.usuarios.rest.error;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String id) {
        super("Usuario no encontrado: " + id);
    }
}
