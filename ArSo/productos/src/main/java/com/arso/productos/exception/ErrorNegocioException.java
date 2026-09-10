package com.arso.productos.exception;

public class ErrorNegocioException extends RuntimeException {

    public ErrorNegocioException(String message) {
        super(message);
    }

    public ErrorNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}

