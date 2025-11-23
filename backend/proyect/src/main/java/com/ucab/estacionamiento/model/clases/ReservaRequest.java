package com.ucab.estacionamiento.model.clases;

import java.time.LocalDate;

public class ReservaRequest {
    private String puestoId;
    private String clienteId;
    private String usuario;
    private LocalDate fecha;
    private String turno; // MAÑANA, TARDE, NOCHE

    public ReservaRequest() {}

    // Getters y Setters
    public String getPuestoId() { return puestoId; }
    public void setPuestoId(String puestoId) { this.puestoId = puestoId; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}
