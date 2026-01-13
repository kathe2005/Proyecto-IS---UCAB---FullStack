package com.ucab.estacionamiento.model.enums;

public enum EstadoIncidencia {
    NO_ATENDIDA("No atendida"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada");

    private String descripcion;

    EstadoIncidencia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}