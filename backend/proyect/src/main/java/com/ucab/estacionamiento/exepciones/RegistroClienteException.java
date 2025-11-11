package com.ucab.estacionamiento.exepciones;

import org.springframework.http.HttpStatus;

// 1. Debe heredar de RuntimeException
public class RegistroClienteException extends RuntimeException {

private final HttpStatus httpStatus; 

    // Constructor...
    public RegistroClienteException(String message, int httpStatusCode) {
        super(message);
        this.httpStatus = HttpStatus.valueOf(httpStatusCode); 
    }

    // Este es el método que SÍ existe y devuelve el código HTTP
    public HttpStatus getHttpStatus() { 
        return httpStatus;
    }
}