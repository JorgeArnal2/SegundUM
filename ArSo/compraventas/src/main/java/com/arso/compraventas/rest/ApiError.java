package com.arso.compraventas.rest;

public class ApiError {

    private String mensaje;

    public ApiError(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
