//Contenedor del JSON 
package com.ucab.estacionamiento.model;

import java.util.UUID; 

public class Cliente {
    
    //Atributo del C4 (Clientes)
    private UUID id; 
    private String usuario; 
    private String contrasena; 
    private String confirmcontrasena; 
    private String nombre; 
    private String apellido; 
    private String cedula; 
    private String direccion; 
    private String telefono; 
    private String email; 
    private String tipoPersona; 

    //Contructor vacio 
    public Cliente(){
        this.id = UUID.randomUUID(); //Se asigna un id automaticamente
    }

    //Constructor con parametros (usuario, contraseña, confirmar contraseña, nombre, apellido, cedula, email, tipo de persona)
    public Cliente(String usuario, String contrasena, String confirmcontrasena, String nombre, String apellido, String cedula, String email, String tipoPersona, String direccion, String telefono )
    {
        this(); 
        this.usuario = usuario; 
        this.contrasena = contrasena; 
        this.confirmcontrasena = confirmcontrasena; 
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
    public String getusuario(){   return usuario;    }
    public void setusuario(String usuario){   this.usuario = usuario;   }

    //Contraseña 
    public String getcontrasena(){   return contrasena;    }
    public void setcontrasena(String contrasena){   this.contrasena = contrasena;   }

    //Confirma Contraseña 
    public String getconfirmcontrasena(){   return confirmcontrasena;    }
    public void setconfirmcontrasena(String confirmcontrasena){   this.confirmcontrasena = confirmcontrasena;   }

    //Nombre
    public String getnombre(){   return nombre;    }
    public void setnombre(String nombre){   this.nombre = nombre;   }

    //Apellido
    public String getapellido(){   return apellido;    }
    public void setapellido(String apellido){   this.apellido = apellido;   }

    //Cedula
    public String getcedula(){   return cedula;    }
    public void setcedula(String cedula){   this.cedula = cedula;   }
    
    //Email 
    public String getemail(){   return email;    }
    public void setemail(String email){   this.email = email;   }

    //Tipo de Persona
    public String gettipoPersona(){   return tipoPersona;    }
    public void settipoPersona(String tipoPersona){   this.tipoPersona = tipoPersona;   }

    //Telefono
    public String gettelefono(){   return telefono;    }
    public void settelefono(String telefono){   this.telefono = telefono;   }

    //Direccion
    public String getdireccion(){   return direccion;    }
    public void setdireccion(String direccion){   this.direccion = direccion;  }

    //Impresion en consola
    @Override
    public String toString() {
            return "Usuario{" +
                    "id=" + id +
                    ", nombre='" + nombre + '\'' +
                    ", email='" + email + '\'' +
                    '}';
    }

}
