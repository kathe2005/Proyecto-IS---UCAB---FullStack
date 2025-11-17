package com.ucab.estacionamiento.model;

import java.util.UUID;

public class Cliente {
    
    private UUID id;
    private String usuario;
    private String contrasena;
    private String confirmarContrasena;
    private String nombre;
    private String apellido;
    private String cedula;
    private String direccion;
    private String telefono;
    private String email;
    private String tipoPersona;

    // Constructor vacío
    public Cliente() {
        this.id = UUID.randomUUID();
    }

    // Constructor con parámetros
    public Cliente(String usuario, String contrasena, String confirmarContrasena, 
                   String nombre, String apellido, String cedula, String email, 
                   String tipoPersona, String direccion, String telefono) {
        this();
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.confirmarContrasena = confirmarContrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.email = email;
        this.tipoPersona = tipoPersona;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getConfirmarContrasena() { return confirmarContrasena; }
    public void setConfirmarContrasena(String confirmarContrasena) { this.confirmarContrasena = confirmarContrasena; }

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

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}