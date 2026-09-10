package com.arso.compraventas.exception;

public class ErrorNegocioException extends RuntimeException {
    public ErrorNegocioException(String mensaje) {
        super(mensaje);
    }
}
