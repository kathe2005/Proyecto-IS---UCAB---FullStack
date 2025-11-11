//Procesar y Validar 

package com.ucab.estacionamiento.service;

import org.springframework.stereotype.Service;
import com.ucab.estacionamiento.DTO.ClienteRegistroDTO;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.model.Cliente;

import java.util.List;


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

    //Metodos 
    //Registrar un usuario 
    public Cliente registrarCliente(ClienteRegistroDTO nuevoCliente)
    {
        //Validaciones de campos obligatorios, no vacios y sin espacios en blanco 
        //Usuario 
        validarSinEspacios(nuevoCliente.getUsuario(), "usuario");

        //Contraseña 
        validarSinEspacios(nuevoCliente.getContrasena(), "contrasena");

        //Email
        validarSinEspacios(nuevoCliente.getEmail(), "email");
        


        //Validaciones de Formato
        //Validar dominio
        clasificarDominio(nuevoCliente.getEmail());

        //Validar tipo de persona y el correo 
        validarEmailPorTipoPersona(nuevoCliente.getTipoPersona(), nuevoCliente.getEmail());

        //Validar formato de la cedula 
        validarFormatoCedula(nuevoCliente.getCedula()); 

        //Validar formato del telefono 
        validarFormatoTelefono(nuevoCliente.getTelefono());

        //Validar formato de la contraseña 
        validarFormatoContrasena(nuevoCliente.getContrasena());


        //Validacion de unicidad 
        //Usuario 
        if(clienteRepository.findByUsuario(nuevoCliente.getUsuario()).isPresent())
        {
            throw new RegistroClienteException("El usuario ingresado se encuentra esta registrado  ingresa otro para continuar", 409 ); 
        }
        
        //Cedula 
        if(clienteRepository.findByCedula(nuevoCliente.getCedula()).isPresent())
        {
            throw new RegistroClienteException("La cedula ingresada se encuentra registrada",409); 
        }

        //Email
        if(clienteRepository.findByEmail(nuevoCliente.getEmail()).isPresent())
        {
            throw new RegistroClienteException("El correo ingresado se encuentra registrado debe ingresar otro para continuar", 409); 
        }

        //Telefono 
        if(clienteRepository.findByTelefono(nuevoCliente.getTelefono()).isPresent())
        {
            throw new RegistroClienteException("El telefono se encuentra registrado ingrese otro para continuar",409); 
        }


        Cliente nuevoCliente2 = new Cliente();

        nuevoCliente2.setUsuario(nuevoCliente.getUsuario());
        nuevoCliente2.setContrasena(nuevoCliente.getContrasena()); 
        nuevoCliente2.setNombre(nuevoCliente.getNombre());
        nuevoCliente2.setApellido(nuevoCliente.getApellido());
        nuevoCliente2.setCedula(nuevoCliente.getCedula());
        nuevoCliente2.setEmail(nuevoCliente.getEmail());
        nuevoCliente2.setTipoPersona(nuevoCliente.getTipoPersona());
        nuevoCliente2.setDireccion(nuevoCliente.getDireccion());
        nuevoCliente2.setTelefono(nuevoCliente.getTelefono());

        //Simulacion del proceso lento (3 Segundos)
        try {
            System.out.println("Procesando el registro");
            Thread.sleep(3000); 
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RegistroClienteException("Error durante el procesamiento del registro", 500);
            
        }

        //Guardar en el repositorio es decir en la conexion a  la base de datos 
        System.out.println("Registrado Exitosamente");
        return clienteRepository.guardar(nuevoCliente2); 

    }

    /**
     * Verifica que el valor no esté vacío ni contenga espacios intermedios.
     * @param valor La cadena a validar (email o contraseña).
     * @param nombreCampo El nombre del campo para el mensaje de error.
     */

    //Validar espacios 
    private void validarSinEspacios(String valor, String nombreCampo)
    {
        //Si el valor esta vacio
        if (valor == null || valor.trim().isEmpty())
        {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede estar vacio",400); 
        }

        //Verificar espacios intermedios 
        if (valor.contains(" "))
        {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede contener espacios en blanco",400); 

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
            throw new RegistroClienteException("El formato del email es invalido: falta el '@'",400); 
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
            throw new RegistroClienteException("El dominio '" + dominio + "' no está permitido para el registro.",400);
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
                throw new RegistroClienteException("Para el tipo 'UCAB', el email debe ser @ucab.edu.ve u @est.ucab.edu.ve",400);
            }
            
        } 
        else if ("VISITANTE".equals(tipo)) 
        {
            // VISITANTE: NO DEBE ser ACADÉMICO.
            if ("ACADEMICO".equals(tipoDominio)) 
            {
                System.err.println("El visitante registrado no esta permitido su dominio");
                System.out.println("correo ingresado no pertenece a un dominio válido para este tipo de cliente");
                throw new RegistroClienteException("Para el tipo 'VISITANTE', el email debe ser @gmail.com, @outlook.com, yahoo.com u hotmail.com",400);
            }
            
        } 
        else 
        {
            throw new RegistroClienteException("El tipo de persona especificado ('" + tipoPersona + "') no es válido.",400);
        }
        
        System.out.println(" El email (" + tipoDominio + ") es válido para el TipoPersona (" + tipo + ").");
    }

    private static final String CEDULA_REGEX = "^[VE]-\\d{6,8}$";

    private void validarFormatoCedula(String cedula)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String cedulaLimpia = cedula.trim(); 

        //Verificacion del formato REGEX
        if(!cedulaLimpia.matches(CEDULA_REGEX))
        {
            System.err.println("Formato de cédula inválido: " + cedulaLimpia);
            throw new RegistroClienteException("El formato de la cédula debe ser V - 12345678",400);
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
        String telefonoLimpio = telefono.trim(); 

        //Verificacion del formato REGEX
        if(!telefonoLimpio.matches(TELEFONO_REGEX))
        {
            System.err.println("Formato de telefono inválido: " + telefonoLimpio);
            throw new RegistroClienteException("El formato del telefono debe ser 0426 - 6112225",400);
        }

        System.out.println("El telefono se ha registrado");
    }

    
    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";
    // ^ Inicio 
    // (?=.*[A-Z]) Contiene al menos una letra mayúscula (A-Z)
    // (?=.*[0-9]) Contiene al menos un dígito (0-9)
    // (?=.*[a-z]) Contiene al menos una letra en minuscula (a-z)
    // .{8,} Tiene una longitud mínima de 8 caracteres
    // $ Termina ahí

    private void validarFormatoContrasena(String contrasena)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String ContrasenaLimpio = contrasena.trim(); 

        //Verificacion del formato REGEX
        if(!ContrasenaLimpio.matches(CONTRASENA_REGEX))
        {
            System.err.println("Formato de la contraseña es inválido: " + ContrasenaLimpio);
            throw new RegistroClienteException("La contraseña no cumple con los requisitos de seguridad. Debe tener un minimo de 8 caractere, incluir al menos una mayuscula y un número",400);
        }

        System.out.println("La contrasena se ha registrado");
    }

}
