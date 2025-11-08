//Contenedor del JSON 
package com.ucab.estacionamiento.model;

public class Cliente {
    
    //Atributo del C4 (Clientes)
    private String usuario; 
    private String contrasena; 
    private String confirmcontrasena; 
    private String nombre; 
    private String apellido; 
    private String cedula; 
    private String email; 
    private String tipoPersona; 

    //Contructor vacio 
    public Cliente(){
    }

    //Constructor con parametros (usuario, contraseña, confirmar contraseña, nombre, apellido, cedula, email, tipo de persona)
    public Cliente(String usuario, String contrasena, String confirmcontrasena, String nombre, String apellido, String cedula, String email, String tipoPersona)
    {
        this.usuario = usuario; 
        this.contrasena = contrasena; 
        this.confirmcontrasena = confirmcontrasena; 
        this.nombre = nombre; 
        this.apellido = apellido; 
        this.cedula = cedula; 
        this.email = email; 
        this.tipoPersona = tipoPersona; 
    }

    //Getters y setters de los atributos 
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



}
