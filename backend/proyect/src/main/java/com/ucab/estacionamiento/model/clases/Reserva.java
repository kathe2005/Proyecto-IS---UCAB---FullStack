package com.ucab.estacionamiento.model.clases;

import com.ucab.estacionamiento.model.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    // Atributos de Reserva
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
    
    // Atributos de ReservaRequest
    private String puestoIdSolicitud;
    private String clienteIdSolicitud;
    private String usuarioSolicitud;
    private LocalDate fechaSolicitud;
    private String turnoSolicitud;

    // Constructores
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
        
        // También inicializar los atributos de solicitud
        this.puestoIdSolicitud = puestoId;
        this.clienteIdSolicitud = clienteId;
        this.usuarioSolicitud = usuario;
        this.fechaSolicitud = fecha;
        this.turnoSolicitud = turno;
    }

    // Constructor para solicitud
    public Reserva(String puestoId, String clienteId, String usuario, 
                          LocalDate fecha, String turno) {
        this();
        this.puestoIdSolicitud = puestoId;
        this.clienteIdSolicitud = clienteId;
        this.usuarioSolicitud = usuario;
        this.fechaSolicitud = fecha;
        this.turnoSolicitud = turno;
        
        // Inicializar también los atributos principales
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

    // Métodos de negocio combinados
    public Reserva confirmarReserva() {
        this.estado = EstadoReserva.CONFIRMADA;
        return this;
    }
    
    public Reserva activarReserva() {
        this.estado = EstadoReserva.ACTIVA;
        return this;
    }
    
    public Reserva completarReserva() {
        this.estado = EstadoReserva.COMPLETADA;
        return this;
    }
    
    public Reserva cancelarReserva() {
        this.estado = EstadoReserva.CANCELADA;
        return this;
    }
    
    public boolean estaActiva() {
        return this.estado == EstadoReserva.ACTIVA;
    }
    
    public boolean estaDisponibleParaUso() {
        return this.estado == EstadoReserva.CONFIRMADA || this.estado == EstadoReserva.ACTIVA;
    }
    
    public boolean esParaHoy() {
        return this.fecha != null && this.fecha.equals(LocalDate.now());
    }
    
    // Método para crear reserva desde solicitud
    public Reserva crearDesdeSolicitud(String idReserva) {
        this.id = idReserva;
        this.puestoId = this.puestoIdSolicitud;
        this.clienteId = this.clienteIdSolicitud;
        this.usuario = this.usuarioSolicitud;
        this.fecha = this.fechaSolicitud;
        this.turno = this.turnoSolicitud;
        setHorariosPorTurno(this.turnoSolicitud);
        this.estado = EstadoReserva.PENDIENTE;
        return this;
    }

    // Getters y Setters para todos los atributos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPuestoId() { return puestoId; }
    public void setPuestoId(String puestoId) { 
        this.puestoId = puestoId; 
        this.puestoIdSolicitud = puestoId;
    }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { 
        this.clienteId = clienteId; 
        this.clienteIdSolicitud = clienteId;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { 
        this.usuario = usuario; 
        this.usuarioSolicitud = usuario;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { 
        this.fecha = fecha; 
        this.fechaSolicitud = fecha;
    }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { 
        this.turno = turno; 
        this.turnoSolicitud = turno;
        setHorariosPorTurno(turno);
    }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Getters y Setters para atributos de solicitud
    public String getPuestoIdSolicitud() { return puestoIdSolicitud; }
    public void setPuestoIdSolicitud(String puestoIdSolicitud) { 
        this.puestoIdSolicitud = puestoIdSolicitud; 
        this.puestoId = puestoIdSolicitud;
    }

    public String getClienteIdSolicitud() { return clienteIdSolicitud; }
    public void setClienteIdSolicitud(String clienteIdSolicitud) { 
        this.clienteIdSolicitud = clienteIdSolicitud; 
        this.clienteId = clienteIdSolicitud;
    }

    public String getUsuarioSolicitud() { return usuarioSolicitud; }
    public void setUsuarioSolicitud(String usuarioSolicitud) { 
        this.usuarioSolicitud = usuarioSolicitud; 
        this.usuario = usuarioSolicitud;
    }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { 
        this.fechaSolicitud = fechaSolicitud; 
        this.fecha = fechaSolicitud;
    }

    public String getTurnoSolicitud() { return turnoSolicitud; }
    public void setTurnoSolicitud(String turnoSolicitud) { 
        this.turnoSolicitud = turnoSolicitud; 
        this.turno = turnoSolicitud;
        setHorariosPorTurno(turnoSolicitud);
    }

    @Override
    public String toString() {
        return "ReservaCompleta{" +
                "id='" + id + '\'' +
                ", puestoId='" + puestoId + '\'' +
                ", clienteId='" + clienteId + '\'' +
                ", usuario='" + usuario + '\'' +
                ", fecha=" + fecha +
                ", turno='" + turno + '\'' +
                ", estado=" + estado +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}