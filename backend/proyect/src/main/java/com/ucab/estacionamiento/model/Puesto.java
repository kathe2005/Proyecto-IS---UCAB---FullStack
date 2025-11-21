package com.ucab.estacionamiento.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;

public class Puesto {
    private String id;
    private String numero;
    private String ubicacion;
    private String usuarioOcupante;
    private TipoPuesto tipoPuesto;
    private EstadoPuesto estadoPuesto;
    private LocalDateTime fechaOcupacion;
    private LocalDateTime fechaCreacion;
    private List<String> historialOcupacion;

    public Puesto() {
        this.fechaCreacion = LocalDateTime.now();
        this.historialOcupacion = new ArrayList<>();
    }

    public Puesto(String id, String numero, TipoPuesto tipoPuesto, EstadoPuesto estadoPuesto, String ubicacion) {
        this();
        this.id = id;
        this.numero = numero;
        this.tipoPuesto = tipoPuesto;
        this.estadoPuesto = estadoPuesto;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public String getUsuarioOcupante() { return usuarioOcupante; }
    public void setUsuarioOcupante(String usuarioOcupante) { this.usuarioOcupante = usuarioOcupante; }
    
    public TipoPuesto getTipoPuesto() { return tipoPuesto; }
    public void setTipoPuesto(TipoPuesto tipoPuesto) { this.tipoPuesto = tipoPuesto; }
    
    public EstadoPuesto getEstadoPuesto() { return estadoPuesto; }
    public void setEstadoPuesto(EstadoPuesto estadoPuesto) { this.estadoPuesto = estadoPuesto; }
    
    public LocalDateTime getFechaOcupacion() { return fechaOcupacion; }
    public void setFechaOcupacion(LocalDateTime fechaOcupacion) { this.fechaOcupacion = fechaOcupacion; }
    
    public LocalDateTime getFechaCreacion() { 
        if (fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        return fechaCreacion; 
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public List<String> getHistorialOcupacion() { 
        if (historialOcupacion == null) {
            this.historialOcupacion = new ArrayList<>();
        }
        return historialOcupacion; 
    }
    public void setHistorialOcupacion(List<String> historialOcupacion) { 
        this.historialOcupacion = historialOcupacion; 
        if (this.historialOcupacion == null) {
            this.historialOcupacion = new ArrayList<>();
        }
    }

    public void agregarRegistroHistorial(String entrada) {
        getHistorialOcupacion().add(entrada);
    }
    
    @Override
    public String toString() {
        return "Puesto{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", tipoPuesto=" + tipoPuesto +
                ", estadoPuesto=" + estadoPuesto +
                '}';
    }
}