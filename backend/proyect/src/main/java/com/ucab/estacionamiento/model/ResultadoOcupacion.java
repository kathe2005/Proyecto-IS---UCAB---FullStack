package com.ucab.estacionamiento.model;

public class ResultadoOcupacion {
    private boolean exito;
    private String mensaje;
    private Puesto puesto;
    private String codigoError;

    public ResultadoOcupacion(boolean exito, String mensaje, Puesto puesto) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.puesto = puesto;
    }

    public ResultadoOcupacion(boolean exito, String mensaje, Puesto puesto, String codigoError) {
        this(exito, mensaje, puesto);
        this.codigoError = codigoError;
    }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Puesto getPuesto() { return puesto; }
    public void setPuesto(Puesto puesto) { this.puesto = puesto; }

    public String getCodigoError() { return codigoError; }
    public void setCodigoError(String codigoError) { this.codigoError = codigoError; }
}
