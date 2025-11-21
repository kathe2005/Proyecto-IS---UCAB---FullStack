package com.ucab.estacionamiento.model.enums;

public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    ACTIVA("Activa"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada"),
    NO_SHOW("No Show");

    private String descripcion;

    EstadoReserva(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}