package com.ucab.estacionamiento.model.clases;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;

public class Puesto {
    // Atributos de Puesto
    private String id;
    private String numero;
    private String ubicacion;
    private String usuarioOcupante;
    private TipoPuesto tipoPuesto;
    private EstadoPuesto estadoPuesto;
    private LocalDateTime fechaOcupacion;
    private LocalDateTime fechaCreacion;
    private List<String> historialOcupacion;
    
    // Atributos de ResultadoOcupacion
    private boolean exito;
    private String mensaje;
    private String codigoError;
    
    // Atributos de OcuparPuestoRequest
    private String puestoIdSolicitud;
    private String usuarioSolicitud;
    private String clienteId;
    private String tipoCliente; // UCAB o VISITANTE
    
    // Atributos de PuestosDisponiblesResponse
    private LocalDate fechaConsulta;
    private String turnoConsulta;
    private int totalPuestos;
    private int puestosDisponibles;
    private List<Puesto> puestosLista;
    private String mensajeDisponibilidad;

    // Constructores
    public Puesto() {
        this.fechaCreacion = LocalDateTime.now();
        this.historialOcupacion = new ArrayList<>();
        this.estadoPuesto = EstadoPuesto.DISPONIBLE;
        this.exito = true;
    }

    public Puesto(String id, String numero, TipoPuesto tipoPuesto, EstadoPuesto estadoPuesto, String ubicacion) {
        this();
        this.id = id;
        this.numero = numero;
        this.tipoPuesto = tipoPuesto;
        this.estadoPuesto = estadoPuesto;
        this.ubicacion = ubicacion;
        this.puestoIdSolicitud = id;
    }

    // Constructor para solicitud de ocupación
    public Puesto(String puestoId, String usuario, String clienteId, String tipoCliente) {
        this();
        this.puestoIdSolicitud = puestoId;
        this.usuarioSolicitud = usuario;
        this.clienteId = clienteId;
        this.tipoCliente = tipoCliente;
    }

    // Constructor para respuesta de disponibilidad
    public Puesto(LocalDate fecha, String turno, int totalPuestos, int puestosDisponibles, List<Puesto> puestos) {
        this();
        this.fechaConsulta = fecha;
        this.turnoConsulta = turno;
        this.totalPuestos = totalPuestos;
        this.puestosDisponibles = puestosDisponibles;
        this.puestosLista = puestos;
        this.mensajeDisponibilidad = String.format("Se encontraron %d puestos disponibles de %d totales para el %s", 
                                   puestosDisponibles, totalPuestos, turno);
    }

    // Constructor para resultado de operación
    public Puesto(boolean exito, String mensaje, Puesto puesto, String codigoError) {
        this();
        this.exito = exito;
        this.mensaje = mensaje;
        if (puesto != null) {
            this.id = puesto.id;
            this.numero = puesto.numero;
            this.ubicacion = puesto.ubicacion;
            this.estadoPuesto = puesto.estadoPuesto;
            this.tipoPuesto = puesto.tipoPuesto;
        }
        this.codigoError = codigoError;
    }

    // Métodos de Puesto
    public void agregarRegistroHistorial(String entrada) {
        getHistorialOcupacion().add(entrada);
    }
    
    // Métodos de negocio combinados
    public Puesto ocuparPuesto(String usuario, String clienteId, String tipoCliente) {
        this.usuarioOcupante = usuario;
        this.clienteId = clienteId;
        this.tipoCliente = tipoCliente;
        this.estadoPuesto = EstadoPuesto.OCUPADO;
        this.fechaOcupacion = LocalDateTime.now();
        this.agregarRegistroHistorial("Ocupado por " + usuario + " - " + LocalDateTime.now());
        this.exito = true;
        this.mensaje = "Puesto ocupado exitosamente";
        return this;
    }
    
    public Puesto liberarPuesto() {
        this.usuarioOcupante = null;
        this.estadoPuesto = EstadoPuesto.DISPONIBLE;
        this.fechaOcupacion = null;
        this.agregarRegistroHistorial("Liberado - " + LocalDateTime.now());
        this.exito = true;
        this.mensaje = "Puesto liberado exitosamente";
        return this;
    }
    
    public boolean estaDisponible() {
        return this.estadoPuesto == EstadoPuesto.DISPONIBLE;
    }
    
    // Getters y Setters para todos los atributos
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
    
    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getCodigoError() { return codigoError; }
    public void setCodigoError(String codigoError) { this.codigoError = codigoError; }
    
    public String getPuestoIdSolicitud() { return puestoIdSolicitud; }
    public void setPuestoIdSolicitud(String puestoIdSolicitud) { this.puestoIdSolicitud = puestoIdSolicitud; }
    
    public String getUsuarioSolicitud() { return usuarioSolicitud; }
    public void setUsuarioSolicitud(String usuarioSolicitud) { this.usuarioSolicitud = usuarioSolicitud; }
    
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    
    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
    
    public LocalDate getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDate fechaConsulta) { this.fechaConsulta = fechaConsulta; }
    
    public String getTurnoConsulta() { return turnoConsulta; }
    public void setTurnoConsulta(String turnoConsulta) { this.turnoConsulta = turnoConsulta; }
    
    public int getTotalPuestos() { return totalPuestos; }
    public void setTotalPuestos(int totalPuestos) { this.totalPuestos = totalPuestos; }
    
    public int getPuestosDisponibles() { return puestosDisponibles; }
    public void setPuestosDisponibles(int puestosDisponibles) { this.puestosDisponibles = puestosDisponibles; }
    
    public List<Puesto> getPuestosLista() { return puestosLista; }
    public void setPuestosLista(List<Puesto> puestosLista) { this.puestosLista = puestosLista; }
    
    public String getMensajeDisponibilidad() { 
        if (mensajeDisponibilidad == null && turnoConsulta != null) {
            return String.format("Se encontraron %d puestos disponibles de %d totales para el %s", 
                               puestosDisponibles, totalPuestos, turnoConsulta);
        }
        return mensajeDisponibilidad; 
    }
    public void setMensajeDisponibilidad(String mensajeDisponibilidad) { this.mensajeDisponibilidad = mensajeDisponibilidad; }
    
    @Override
    public String toString() {
        return "PuestoCompleto{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", estado=" + estadoPuesto +
                ", tipo=" + tipoPuesto +
                ", usuario='" + usuarioOcupante + '\'' +
                ", exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}