package com.ucab.estacionamiento.model.enums;

public enum TipoProblema {
    PAGO("Problemas con el pago"),
    RESERVA("Problemas con la reserva"),
    PUESTO("Problemas con el puesto"),
    OTROS("Otros");

    private String descripcion;

    TipoProblema(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}