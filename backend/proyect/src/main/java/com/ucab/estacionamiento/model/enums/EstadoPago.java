package com.ucab.estacionamiento.model.enums;

public enum EstadoPago {
    PENDIENTE("Pendiente"),
    COMPLETADO("Completado"),
    RECHAZADO("Rechazado"),
    CANCELADO("Cancelado");

    private String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
