package com.ucab.estacionamiento.model;

import com.ucab.estacionamiento.model.enums.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private String id;
    private String puestoId;
    private String clienteId;
    private String usuario;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String turno; // MAÑANA, TARDE, NOCHE
    private EstadoReserva estado;
    private LocalDate fechaCreacion;

    public Reserva() {
        this.fechaCreacion = LocalDate.now();
        this.estado = EstadoReserva.PENDIENTE;
    }

    public Reserva(String id, String puestoId, String clienteId, String usuario, 
                  LocalDate fecha, String turno) {
        this();
        this.id = id;
        this.puestoId = puestoId;
        this.clienteId = clienteId;
        this.usuario = usuario;
        this.fecha = fecha;
        this.turno = turno;
        setHorariosPorTurno(turno);
    }

    private void setHorariosPorTurno(String turno) {
        switch (turno.toUpperCase()) {
            case "MAÑANA":
                this.horaInicio = LocalTime.of(6, 0);
                this.horaFin = LocalTime.of(14, 0);
                break;
            case "TARDE":
                this.horaInicio = LocalTime.of(14, 0);
                this.horaFin = LocalTime.of(22, 0);
                break;
            case "NOCHE":
                this.horaInicio = LocalTime.of(22, 0);
                this.horaFin = LocalTime.of(6, 0);
                break;
            default:
                throw new IllegalArgumentException("Turno no válido: " + turno);
        }
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPuestoId() { return puestoId; }
    public void setPuestoId(String puestoId) { this.puestoId = puestoId; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { 
        this.turno = turno; 
        setHorariosPorTurno(turno);
    }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}