package com.ucab.estacionamiento.model;

public class OcuparPuestoRequest {
    private String puestoId;
    private String usuario;
    private String clienteId; // Nuevo campo para integrar con clientes
    private String tipoCliente; // UCAB o VISITANTE

    public OcuparPuestoRequest() {}

    public OcuparPuestoRequest(String puestoId, String usuario, String clienteId, String tipoCliente) {
        this.puestoId = puestoId;
        this.usuario = usuario;
        this.clienteId = clienteId;
        this.tipoCliente = tipoCliente;
    }

    // Getters y Setters
    public String getPuestoId() { return puestoId; }
    public void setPuestoId(String puestoId) { this.puestoId = puestoId; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }
}
