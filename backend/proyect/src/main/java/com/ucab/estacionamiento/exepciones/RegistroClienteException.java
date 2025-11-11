package com.ucab.estacionamiento.exepciones;

// 1. Debe heredar de RuntimeException
public class RegistroClienteException extends RuntimeException {

    // 2. Campo para almacenar el código de estado HTTP (ej. 409, 400)
    private final int status; 

    public RegistroClienteException(String mensaje, int status) {
        super(mensaje); // El mensaje se pasa al constructor base
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}