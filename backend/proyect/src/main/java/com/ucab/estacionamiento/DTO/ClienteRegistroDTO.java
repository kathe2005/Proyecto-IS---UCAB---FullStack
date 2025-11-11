package com.ucab.estacionamiento.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ClienteRegistroDTO {

    // Campos de Credenciales
    private String usuario;
    private String contrasena;
    
    // Campo clave para la validación: Debe coincidir con el nombre usado en Angular
    // Angular usa 'confirmcontrasena', no 'confirmarContrasena'
    @JsonProperty("confirmcontrasena")
    private String confirmcontrasena;
    
    // Campos de Datos Personales
    private String nombre;
    private String apellido;
    private String cedula;
    private String email; 
    private String tipoPersona;

    // Campos de Contacto/Ubicación
    private String direccion;
    private String telefono;

    // Constructor vacío (necesario para Spring)
    public ClienteRegistroDTO() {}

    // Getters y Setters
    // (Debes generarlos todos para que Spring/Jackson pueda mapear los datos)

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getConfirmcontrasena() { return confirmcontrasena; }
    public void setConfirmcontrasena(String confirmcontrasena) { this.confirmcontrasena = confirmcontrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
}
