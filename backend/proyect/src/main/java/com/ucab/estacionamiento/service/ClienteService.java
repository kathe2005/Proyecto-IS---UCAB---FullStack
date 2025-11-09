//Procesar y Validar 

package com.ucab.estacionamiento.service;

import org.springframework.stereotype.Service;
import com.ucab.estacionamiento.model.Cliente;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import com.ucab.estacionamiento.repository.ClienteRepository;

@Service //Esta clase continene la lógica del negocio
public class ClienteService {

    //Crea o inserta la instancia del repository
    private final ClienteRepository clienteRepository; 


    //Constructor 
    public ClienteService(ClienteRepository clienteRepository)
    {
        this.clienteRepository = clienteRepository; 
    }

    @Autowired //Necesario para los atributos del Cliente y su conexion a la Base de Datos 

    //Metodos 
    //Registrar un usuario 
    public Cliente registrarCliente(Cliente nuevoCliente)
    {

        //Validar dominio 
        clasificarDominio(nuevoCliente.getemail());

        validarEmailPorTipoPersona(nuevoCliente.gettipoPersona(), nuevoCliente.getemail());

        //Validar formato de la cedula 
        validarFormatoCedula(nuevoCliente.getcedula()); 

        //Validar formato del telefono 
        validarFormatoTelefono(nuevoCliente.gettelefono());

        //Validar formato de la contraseña 
        validarFormatoContrasena(nuevoCliente.getcontrasena());


        //Validacion de unicidad 
        //Usuario 
        if(clienteRepository.findByUsuario(nuevoCliente.getusuario()).isPresent())
        {
            throw new IllegalArgumentException("Su usuario se encuentra registrado debe ingresar otro para continuar"); 
        }

        //Contrasena 
        if(clienteRepository.findByContrasena(nuevoCliente.getcontrasena()).isPresent())
        {
            throw new IllegalArgumentException("Su contraseña se encuentra registrada debe ingresar otro para continuar"); 
        }

        //Nombre 
        if(clienteRepository.findByNombre(nuevoCliente.getnombre()).isPresent())
        {
            throw new IllegalArgumentException("Los nombres se encuentra registrada debe ingresar otro para continuar"); 
        }


        //Apellido 
        if(clienteRepository.findByApellido(nuevoCliente.getapellido()).isPresent())
        {
            throw new IllegalArgumentException("Los apellidos se encuentra registrada debe ingresar otro para continuar"); 
        }

        //Cedula 
        if(clienteRepository.findByCedula(nuevoCliente.getcedula()).isPresent())
        {
            throw new IllegalArgumentException("Su cedula se encuentra registrada"); 
        }

        //Email
        if(clienteRepository.findByEmail(nuevoCliente.getemail()).isPresent())
        {
            throw new IllegalArgumentException("Su correo se encuentra registrado debe ingresar otro para continuar"); 
        }

        //Direccion 
        if(clienteRepository.findByDireccion(nuevoCliente.getdireccion()).isPresent())
        {
            throw new IllegalArgumentException("Su direccion se encuentra registrada"); 
        }

        //Telefono 
        if(clienteRepository.findByTelefono(nuevoCliente.gettelefono()).isPresent())
        {
            throw new IllegalArgumentException("Su telefono se encuentra registrado"); 
        }


        //Validar espacios en blanco 
        //Usuario 
        validarSinEspacios(nuevoCliente.getusuario(), "usuario");

        //Contraseña 
        validarSinEspacios(nuevoCliente.getcontrasena(), "contrasena");

        //Confirmar Contraseña 
        validarSinEspacios(nuevoCliente.getconfirmcontrasena(), "contrasena");

        //Email
        validarSinEspacios(nuevoCliente.getemail(), "email");
        


        //Validaciones de campos obligatorios y no puede estar vacío.
        if(nuevoCliente.getusuario() == null || nuevoCliente.getusuario().isEmpty())
        {
            throw new IllegalArgumentException("El usuario no puede estar vacio"); 
        }

        if(nuevoCliente.getcontrasena() == null || nuevoCliente.getcontrasena().isEmpty())
        {
            throw new IllegalArgumentException("La contraseña no puede estar vacio"); 
        }

        if(nuevoCliente.getconfirmcontrasena() == null || nuevoCliente.getconfirmcontrasena().isEmpty())
        {
            throw new IllegalArgumentException("La confirmacion de la contraseña no puede estar vacio"); 
        }

        if (nuevoCliente.getcontrasena() != nuevoCliente.getconfirmcontrasena())
        {
            throw new IllegalArgumentException("La confirmacion de la contraseña debe ser igual a la contraseña"); 
        }


        if(nuevoCliente.getnombre() == null || nuevoCliente.getnombre().isEmpty())
        {
            throw new IllegalArgumentException("El nombre no puede estar vacio"); 
        }

        if(nuevoCliente.getapellido() == null || nuevoCliente.getapellido().isEmpty())
        {
            throw new IllegalArgumentException("El apellido no puede estar vacio"); 
        }

        if(nuevoCliente.getcedula() == null || nuevoCliente.getcedula().isEmpty())
        {
            throw new IllegalArgumentException("La cedula no puede estar vacio"); 
        }

        if(nuevoCliente.getemail() == null || nuevoCliente.getemail().isEmpty())
        {
            throw new IllegalArgumentException("El email no puede estar vacio"); 
        }

        if(nuevoCliente.gettipoPersona() == null || nuevoCliente.gettipoPersona().isEmpty())
        {
            throw new IllegalArgumentException("El tipo de persona no puede estar vacio"); 
        }

        //Simulacion del proceso lento (3 Segundos)
        try {
            System.out.println("Procesando el registro");
            Thread.sleep(3000);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //Guardar en el repositorio es decir en la conexion a  la base de datos 
        System.out.println("Registrado Exitosamente");
        return clienteRepository.guardar(nuevoCliente); 

    }

    /**
     * Verifica que el valor no esté vacío ni contenga espacios intermedios.
     * @param valor La cadena a validar (email o contraseña).
     * @param nombreCampo El nombre del campo para el mensaje de error.
     */

    private void validarSinEspacios(String valor, String nombreCampo)
    {
        //Si el valor esta vacio
        if (valor.trim().contains(""))
        {
            throw new IllegalArgumentException(nombreCampo + " no puede estar vacio"); 
        }

        //Verificar espacios intermedios 
        // 1. trim() elimina los espacios iniciales/finales.
        // 2. contains(" ") verifica si AÚN queda algún espacio dentro de la cadena.
        if (valor.trim().contains(""))
        {
            throw new IllegalArgumentException(nombreCampo + " no puede contener espacios en blanco"); 

        }

        System.out.println(nombreCampo + "  se encuentra sin espacios en blanco");
        
    }

    //Lista de dominios clasificados que estan permitidos 

    //Dominios Academicos 
    private static final List<String> DOMINIOS_ACADEMICOS = List.of(
        "ucab.edu.ve",
        "est.ucab.edu.ve"
    ); 

    //Dominios de Gmail/Google
    private static final List<String> DOMINIOS_GMAIL = List.of(
        "gmail.com",
        "googlemail.com"
    ); 

    //Dominios de Corpotativos Empresariales 
    private static final List<String> DOMINIOS_CORPORATIVOS = List.of(
        "outlook.com",
        "yahoo.com",
        "hotmail.com"
    ); 


    //Clasificar el dominio de email 
    private String clasificarDominio (String email)
    {
        //
        if(!email.contains("@"))
        {
            throw new IllegalArgumentException("El formato del email es invalido: falta el '@'"); 
        }

        //Extraer el dominio 
        String dominio = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        //Clasificación 
        if (DOMINIOS_ACADEMICOS.contains(dominio)) {
            System.out.println("Dominio ACADÉMICO: " + dominio);
            return "ACADEMICO";
            
        } else if (DOMINIOS_GMAIL.contains(dominio)) {
            System.out.println("Dominio GMAIL: " + dominio);
            return "GMAIL";
            
        } else if (DOMINIOS_CORPORATIVOS.contains(dominio)) {
            System.out.println(" Dominio CORPORATIVO: " + dominio);
            return "CORPORATIVO";
            
        } else {
            System.err.println("El dominio no esta permitido: " + dominio);
            throw new IllegalArgumentException("El dominio '" + dominio + "' no está permitido para el registro.");
        }
    }

    private void validarEmailPorTipoPersona(String tipoPersona, String email) 
    {
        
        // El tipo de persona lo colocamos en mayúsculas para la comparación segura
        String tipo = tipoPersona.toUpperCase().trim();
        String tipoDominio = clasificarDominio(email);

        if ("UCAB".equals(tipo)) 
        {
            //UCAB: DEBE ser ACADÉMICO.
            if (!"ACADEMICO".equals(tipoDominio)) 
            {
                System.err.println("El usuario UCAB registrado no esta permitido.");
                System.out.println("correo ingresado no pertenece a un dominio válido para este tipo de cliente");
                throw new IllegalArgumentException("Para el tipo 'UCAB', el email debe ser @ucab.edu.ve u @est.ucab.edu.ve");
            }
            
        } 
        else if ("VISITANTE".equals(tipo)) 
        {
            // VISITANTE: NO DEBE ser ACADÉMICO.
            if ("ACADEMICO".equals(tipoDominio)) 
            {
                System.err.println("El visitante registrado no esta permitido su dominio");
                System.out.println("correo ingresado no pertenece a un dominio válido para este tipo de cliente");
                throw new IllegalArgumentException("Para el tipo 'VISITANTE', el email debe ser @gmail.com, @outlook.com, yahoo.com u hotmail.com");
            }
            
        } 
        else 
        {
            throw new IllegalArgumentException("El tipo de persona especificado ('" + tipoPersona + "') no es válido.");
        }
        
        System.out.println(" El email (" + tipoDominio + ") es válido para el TipoPersona (" + tipo + ").");
    }

    private static final String CEDULA_REGEX = "^[VE]-\\d{1,9}$";
    //[VE] que inicia con V o E
    // - Seguido de un guion
    // \d{1,8} Seguido de 1 a 9 dígitos
    // $ Termina ahí 

    private void validarFormatoCedula(String cedula)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String cedulaLimpia = cedula.trim().toUpperCase(); 

        //Verificacion del formato REGEX
        if(!cedulaLimpia.matches(CEDULA_REGEX))
        {
            System.err.println("Formato de cédula inválido: " + cedulaLimpia);
            throw new IllegalArgumentException("El formato de la cédula debe ser V - 12345678");
        }

        System.out.println("La cedula se ha registrado");
    }


    private static final String TELEFONO_REGEX = "^(0212|0424|0416|0426|0414)-\\d{7}$";
    //(0212|0424|0416|0426|0414) que inicia con estos numeros
    // - Seguido de un guion
    // \d{7} Seguido de 1 a 7 dígitos
    // $ Termina ahí 

    private void validarFormatoTelefono(String telefono)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String telefonoLimpio = telefono.trim().toUpperCase(); 

        //Verificacion del formato REGEX
        if(!telefonoLimpio.matches(TELEFONO_REGEX))
        {
            System.err.println("Formato de telefono inválido: " + telefonoLimpio);
            throw new IllegalArgumentException("El formato del telefono debe ser 0426 - 6112225");
        }

        System.out.println("El telefono se ha registrado");
    }

    
    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$";
    // ^ Inicio 
    // (?=.*[A-Z]) Contiene al menos una letra mayúscula (A-Z)
    // (?=.*[0-9]) Contiene al menos un dígito (0-9)
    // .{8,} Tiene una longitud mínima de 8 caracteres
    // $ Termina ahí

    private void validarFormatoContrasena(String contrasena)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String ContrasenaLimpio = contrasena.trim().toUpperCase(); 

        //Verificacion del formato REGEX
        if(!ContrasenaLimpio.matches(CONTRASENA_REGEX))
        {
            System.err.println("Formato de la contraseña es inválido: " + ContrasenaLimpio);
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad. Debe tener un minimo de 8 caractere, incluir al menos una mayuscula y un número");
        }

        System.out.println("La contrasena se ha registrado");
    }

}
