//Contenedor del JSON 
package com.ucab.estacionamiento.model;

public class Cliente {
    
    //Atributo del C4 (Clientes)
    private String usuario; 
    private String contrasena; 
    private String confirmarcontrasena; 
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        // La igualdad se basa solo en el campo único: 'usuario'
        return java.util.Objects.equals(usuario, cliente.usuario); 
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(usuario);
    }

    //Constructor con parametros (usuario, contraseña, confirmar contraseña, nombre, apellido, cedula, email, tipo de persona)
    public Cliente(String usuario, String contrasena, String confirmarcontrasena, String nombre, String apellido, String cedula, String email, String tipoPersona, String direccion, String telefono )
    {

        this.usuario = usuario; 
        this.contrasena = contrasena; 
        this.confirmarcontrasena = confirmarcontrasena; 
        this.nombre = nombre; 
        this.apellido = apellido; 
        this.cedula = cedula; 
        this.email = email; 
        this.tipoPersona = tipoPersona; 
        this.direccion = direccion; 
        this.telefono = telefono; 
    }

    //Getters y setters de los atributos 
    
    //Usuario 
    public String getUsuario(){   return usuario;    }
    public void setUsuario(String usuario){   this.usuario = usuario;   }

    //Contraseña 
    public String getContrasena(){   return contrasena;    }
    public void setContrasena(String contrasena){   this.contrasena = contrasena;   }

    //Contraseña 
    public String getconfirmarContrasena(){   return confirmarcontrasena;    }
    public void setconfirmarContrasena(String confirmarcontrasena){   this.confirmarcontrasena = confirmarcontrasena;  }

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
