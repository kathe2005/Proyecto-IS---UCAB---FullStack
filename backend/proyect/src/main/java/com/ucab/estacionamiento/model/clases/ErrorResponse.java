package com.ucab.estacionamiento.model.clases;

public class ErrorResponse {
    private String mensajeError; 
    private int codigoError;

    public ErrorResponse(String mensajeError, int codigoError) {
        this.mensajeError = mensajeError;
        this.codigoError = codigoError;
    }

    // Getters y Setters necesarios para que Spring lo serialice a JSON
    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }

    public int getCodigoError() { return codigoError; }
    public void setCodigoError(int codigoError) { this.codigoError = codigoError; }
}
