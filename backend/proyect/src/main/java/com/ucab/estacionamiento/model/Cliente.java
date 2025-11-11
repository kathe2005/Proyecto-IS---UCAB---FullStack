//Contenedor del JSON 
package com.ucab.estacionamiento.model;

import java.util.UUID; 

public class Cliente {
    
    //Atributo del C4 (Clientes)
    private UUID id; 
    private String usuario; 
    private String contrasena; 
    private String nombre; 
    private String apellido; 
    private String cedula; 
    private String direccion; 
    private String telefono; 
    private String email; 
    private String tipoPersona; 

    //Contructor vacio 
    public Cliente(){
        
    }

    //Constructor con parametros (usuario, contraseña, confirmar contraseña, nombre, apellido, cedula, email, tipo de persona)
    public Cliente(String usuario, String contrasena, String confirmcontrasena, String nombre, String apellido, String cedula, String email, String tipoPersona, String direccion, String telefono )
    {

        this(); 
        this.id = UUID.randomUUID(); //Se asigna un id automaticamente
        this.usuario = usuario; 
        this.contrasena = contrasena; 
        this.nombre = nombre; 
        this.apellido = apellido; 
        this.cedula = cedula; 
        this.email = email; 
        this.tipoPersona = tipoPersona; 
        this.direccion = direccion; 
        this.telefono = telefono; 
    }

    //Getters y setters de los atributos 
    //id 
    public UUID getId(){ return id; }
    public void setId(UUID id){ this.id = id; }


    //Usuario 
    public String getUsuario(){   return usuario;    }
    public void setUsuario(String usuario){   this.usuario = usuario;   }

    //Contraseña 
    public String getContrasena(){   return contrasena;    }
    public void setContrasena(String contrasena){   this.contrasena = contrasena;   }

    //Nombre
    public String getNombre(){   return nombre;    }
    public void setNombre(String nombre){   this.nombre = nombre;   }

    //Apellido
    public String getApellido(){   return apellido;    }
    public void setApellido(String apellido){   this.apellido = apellido;   }

    //Cedula
    public String getCedula(){   return cedula;    }
    public void setCedula(String cedula){   this.cedula = cedula;   }
    
    //Email 
    public String getEmail(){   return email;    }
    public void setEmail(String email){   this.email = email;   }

    //Tipo de Persona
    public String getTipoPersona(){   return tipoPersona;    }
    public void setTipoPersona(String tipoPersona){   this.tipoPersona = tipoPersona;   }

    //Telefono
    public String getTelefono(){   return telefono;    }
    public void setTelefono(String telefono){   this.telefono = telefono;   }

    //Direccion
    public String getDireccion(){   return direccion;    }
    public void setDireccion(String direccion){   this.direccion = direccion;  }
}
