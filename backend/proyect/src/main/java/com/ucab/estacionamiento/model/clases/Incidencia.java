package com.ucab.estacionamiento.model.clases;

import com.ucab.estacionamiento.model.enums.EstadoIncidencia;
import com.ucab.estacionamiento.model.enums.TipoProblema;
import java.time.LocalDateTime;

public class Incidencia {
    private int id;
    private String clienteAfectado;
    private EstadoIncidencia estado;
    private TipoProblema tipoProblema;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    public Incidencia() {
    }
    public Incidencia(int id, String clienteAfectado, EstadoIncidencia estado, TipoProblema tipoProblema, String descripcion) {
        this.id = id;
        this.clienteAfectado = clienteAfectado;
        this.estado = estado;
        this.tipoProblema = tipoProblema;
        this.descripcion = descripcion;
        this.fechaRegistro = LocalDateTime.now();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getClienteAfectado() {
        return clienteAfectado;
    }
    public void setClienteAfectado(String clienteAfectado) {
        this.clienteAfectado = clienteAfectado;
    }
    public EstadoIncidencia getEstado() {
        return estado;
    }
    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }
    public TipoProblema getTipoProblema() {
        return tipoProblema;
    }
    public void setTipoProblema(TipoProblema tipoProblema) {
        this.tipoProblema = tipoProblema;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    @Override
    public String toString() {
        return "Incidencia{" +
                "id=" + id +
                ", cliente='" + clienteAfectado + '\'' +
                ", estado=" + estado +
                ", tipo=" + tipoProblema +
                ", fecha=" + fechaRegistro +
                '}';
    }
}