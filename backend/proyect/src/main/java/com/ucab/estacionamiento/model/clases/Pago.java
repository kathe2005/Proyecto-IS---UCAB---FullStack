package com.ucab.estacionamiento.model.clases;

import com.ucab.estacionamiento.model.enums.MetodoPago;
import com.ucab.estacionamiento.model.enums.EstadoPago;
import java.time.LocalDateTime;

public class Pago {
    // Atributos de Pago
    private String id;
    private String reservaId;
    private String clienteId;
    private double monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private String referencia;
    private String descripcion;
    
    // Atributos de PagoRequest
    private String reservaIdSolicitud;
    private String clienteIdSolicitud;
    private double montoSolicitud;
    private MetodoPago metodoPagoSolicitud;
    private String referenciaSolicitud;
    private String descripcionSolicitud;

    // Constructores
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
        
        // Sincronizar con atributos de solicitud
        this.reservaIdSolicitud = reservaId;
        this.clienteIdSolicitud = clienteId;
        this.montoSolicitud = monto;
        this.metodoPagoSolicitud = metodoPago;
        this.referenciaSolicitud = referencia;
    }

    // Constructor para solicitud de pago
    public Pago(String reservaId, String clienteId, double monto, 
                       MetodoPago metodoPago, String referencia, String descripcion) {
        this();
        this.reservaIdSolicitud = reservaId;
        this.clienteIdSolicitud = clienteId;
        this.montoSolicitud = monto;
        this.metodoPagoSolicitud = metodoPago;
        this.referenciaSolicitud = referencia;
        this.descripcionSolicitud = descripcion;
        
        // Sincronizar con atributos principales
        this.reservaId = reservaId;
        this.clienteId = clienteId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
        this.descripcion = descripcion;
    }

    // Métodos de negocio combinados
    public Pago procesarPago(String idPago) {
        this.id = idPago;
        this.estado = EstadoPago.COMPLETADO;
        this.fechaPago = LocalDateTime.now();
        return this;
    }
    
    public Pago marcarComoPendiente() {
        this.estado = EstadoPago.PENDIENTE;
        return this;
    }
    
    public Pago marcarComoRechazado() {
        this.estado = EstadoPago.RECHAZADO;
        return this;
    }
    
    public Pago cancelar() {
        this.estado = EstadoPago.CANCELADO;
        return this;
    }
    
    public boolean estaCompletado() {
        return this.estado == EstadoPago.COMPLETADO;
    }
    
    public boolean estaPendiente() {
        return this.estado == EstadoPago.PENDIENTE;
    }
    
    public boolean fueRechazado() {
        return this.estado == EstadoPago.RECHAZADO;
    }
    
    public boolean estaCancelado() {
        return this.estado == EstadoPago.CANCELADO;
    }
    
    public boolean esMontoValido() {
        return this.monto > 0 && this.monto <= 10000; // Límite razonable
    }
    
    public String getDescripcionEstado() {
        return this.estado != null ? this.estado.getDescripcion() : "Desconocido";
    }
    
    // Método para crear pago desde solicitud
    public Pago crearDesdeSolicitud(String idPago) {
        this.id = idPago;
        this.reservaId = this.reservaIdSolicitud;
        this.clienteId = this.clienteIdSolicitud;
        this.monto = this.montoSolicitud;
        this.metodoPago = this.metodoPagoSolicitud;
        this.referencia = this.referenciaSolicitud;
        this.descripcion = this.descripcionSolicitud;
        this.fechaPago = LocalDateTime.now();
        this.estado = EstadoPago.COMPLETADO;
        return this;
    }
    
    // Método para validar datos de pago
    public boolean validarDatosPago() {
        return this.reservaId != null && !this.reservaId.trim().isEmpty() &&
               this.clienteId != null && !this.clienteId.trim().isEmpty() &&
               this.monto > 0 &&
               this.metodoPago != null &&
               this.referencia != null && !this.referencia.trim().isEmpty();
    }
    
    // Método para obtener resumen del pago
    public String obtenerResumenPago() {
        return String.format("Pago %s - Monto: %.2f - Método: %s - Estado: %s",
                id != null ? id : "PENDIENTE",
                monto,
                metodoPago != null ? metodoPago.toString() : "NO DEFINIDO",
                getDescripcionEstado());
    }

    // Getters y Setters para todos los atributos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReservaId() { return reservaId; }
    public void setReservaId(String reservaId) { 
        this.reservaId = reservaId; 
        this.reservaIdSolicitud = reservaId;
    }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { 
        this.clienteId = clienteId; 
        this.clienteIdSolicitud = clienteId;
    }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { 
        this.monto = monto; 
        this.montoSolicitud = monto;
    }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { 
        this.metodoPago = metodoPago; 
        this.metodoPagoSolicitud = metodoPago;
    }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { 
        this.referencia = referencia; 
        this.referenciaSolicitud = referencia;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
        this.descripcionSolicitud = descripcion;
    }

    // Getters y Setters para atributos de solicitud
    public String getReservaIdSolicitud() { return reservaIdSolicitud; }
    public void setReservaIdSolicitud(String reservaIdSolicitud) { 
        this.reservaIdSolicitud = reservaIdSolicitud; 
        this.reservaId = reservaIdSolicitud;
    }

    public String getClienteIdSolicitud() { return clienteIdSolicitud; }
    public void setClienteIdSolicitud(String clienteIdSolicitud) { 
        this.clienteIdSolicitud = clienteIdSolicitud; 
        this.clienteId = clienteIdSolicitud;
    }

    public double getMontoSolicitud() { return montoSolicitud; }
    public void setMontoSolicitud(double montoSolicitud) { 
        this.montoSolicitud = montoSolicitud; 
        this.monto = montoSolicitud;
    }

    public MetodoPago getMetodoPagoSolicitud() { return metodoPagoSolicitud; }
    public void setMetodoPagoSolicitud(MetodoPago metodoPagoSolicitud) { 
        this.metodoPagoSolicitud = metodoPagoSolicitud; 
        this.metodoPago = metodoPagoSolicitud;
    }

    public String getReferenciaSolicitud() { return referenciaSolicitud; }
    public void setReferenciaSolicitud(String referenciaSolicitud) { 
        this.referenciaSolicitud = referenciaSolicitud; 
        this.referencia = referenciaSolicitud;
    }

    public String getDescripcionSolicitud() { return descripcionSolicitud; }
    public void setDescripcionSolicitud(String descripcionSolicitud) { 
        this.descripcionSolicitud = descripcionSolicitud; 
        this.descripcion = descripcionSolicitud;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id='" + id + '\'' +
                ", reservaId='" + reservaId + '\'' +
                ", clienteId='" + clienteId + '\'' +
                ", monto=" + monto +
                ", metodoPago=" + metodoPago +
                ", estado=" + getDescripcionEstado() +
                ", fechaPago=" + fechaPago +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}