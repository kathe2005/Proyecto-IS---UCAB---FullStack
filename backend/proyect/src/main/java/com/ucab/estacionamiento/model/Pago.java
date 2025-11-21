package com.ucab.estacionamiento.model;

import com.ucab.estacionamiento.model.enums.MetodoPago;
import com.ucab.estacionamiento.model.enums.EstadoPago;
import java.time.LocalDateTime;

public class Pago {
    private String id;
    private String reservaId;
    private String clienteId;
    private double monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private String referencia;
    private String descripcion;

    public Pago() {
        this.fechaPago = LocalDateTime.now();
        this.estado = EstadoPago.COMPLETADO;
    }

    public Pago(String id, String reservaId, String clienteId, double monto, 
                MetodoPago metodoPago, String referencia) {
        this();
        this.id = id;
        this.reservaId = reservaId;
        this.clienteId = clienteId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReservaId() { return reservaId; }
    public void setReservaId(String reservaId) { this.reservaId = reservaId; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}