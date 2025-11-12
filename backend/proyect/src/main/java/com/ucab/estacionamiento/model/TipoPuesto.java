package com.ucab.estacionamiento.model;

public enum TipoPuesto {
    REGULAR("Regular", "blue"),
    DISCAPACITADO("Discapacitado", "purple"),
    DOCENTE("Docente", "green"),
    VISITANTE("Visitante", "yellow"),
    MOTOCICLETA("Motocicleta", "orange");

    private String descripcion;
    private String color;

    TipoPuesto(String descripcion, String color) {
        this.descripcion = descripcion;
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String descripcionTipoPuesto() {
        return descripcion;
    }

    public String getDescripcionTipoPuesto() {
        return descripcion;
    }

    public String getColor() {
        return color;
    }
}
