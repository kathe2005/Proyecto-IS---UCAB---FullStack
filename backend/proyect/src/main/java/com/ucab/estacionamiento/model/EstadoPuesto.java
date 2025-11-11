package com.ucab.estacionamiento.model;

public enum EstadoPuesto {
    DISPONIBLE("Disponible", "green"),
    OCUPADO("Ocupado", "red"),
    RESERVADO("Reservado", "yellow"),
    BLOQUEADO("Bloqueado", "gray"),
    MANTENIMIENTO("En Mantenimiento", "orange");

    private String descripcion;
    private String color;

    EstadoPuesto(String descripcion, String color) {
        this.descripcion = descripcion;
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionEstadoPuesto() {
        return descripcion;
    }

    public String descripcionEstadoPuesto() {
        return descripcion;
    }

    public String getColor() {
        return color;
    }
}
