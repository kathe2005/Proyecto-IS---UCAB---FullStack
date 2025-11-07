package com.proyectIS.proyect.cliente.cliente; 

public class Cliente
{
    private Long id; 

    //Todos los atributos del cliente 
    private String username; 
    private String password;
    private String nombre;
    private String apellido;
    private String cedula;
    private String email;
    private String tipos_de_cliente;

    // --- Constructor sin argumentos (necesario para JPA) ---
    public Cliente(){
    }

    // --- Constructor completo (incluye username y password) ---
    public Cliente(String username, String password, String nombre, String apellido, String cedula, String email, String tipos_de_cliente) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.email = email;
        this.tipos_de_cliente = tipos_de_cliente;
    }

    // --- Getters y Setters de username y password ---
    public String getUsername() {
        return username; 
    }

    public void setUsername(String username) {
        this.username = username; 
    }
    
    public String getPassword() {
        return password; 
    }

    public void setPassword(String password) {
        this.password = password; 
    }

    // --- Getters y Setters de id (Correctos) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- Getters y Setters de nombre (Correctos) ---
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // --- Getters y Setters de apellido (CORREGIDO: recibe el parámetro String) ---
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { // ¡Corrección aquí!
        this.apellido = apellido; 
    }

    // --- Getters y Setters de cedula (CORREGIDO: usa String y recibe el parámetro String) ---
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { // ¡Corrección aquí!
        this.cedula = cedula; 
    }

    // --- Getters y Setters de email (CORREGIDO: recibe el parámetro String) ---
    public String getEmail() { return email; }
    public void setEmail(String email) { // ¡Corrección aquí!
        this.email = email; 
    }

    // --- Getters y Setters de tipos de cliente (CORREGIDO: recibe el parámetro String) ---
    public String getTipos_de_cliente() { return tipos_de_cliente; }
    public void setTipos_de_cliente(String tipos_de_cliente) { // ¡Corrección aquí!
        this.tipos_de_cliente = tipos_de_cliente; 
    }

    @Override
    public String toString() {
        return "Cliente [id=" + id + ", username=" + username + ", nombre=" + nombre + ", apellido=" + apellido + ", cedula=" + cedula 
                + ", email=" + email + ", tipos_de_cliente=" + tipos_de_cliente + "]";
    }
}