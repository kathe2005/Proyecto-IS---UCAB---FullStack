package com.ucab.estacionamiento.model.implement;

import com.ucab.estacionamiento.model.archivosJson.ClienteRepository;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.model.interfaces.ClienteService;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ClienteServiceImpl implements ClienteService {


    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // ------------------------- Registrar Cliente ---------------------------------------
    @Override
    public Cliente registrarCliente(Cliente nuevoCliente)
    {
        //Validaciones de campos obligatorios, no vacios y sin espacios en blanco 
        //Usuario 
        validarSinEspacios(nuevoCliente.getUsuario(), "usuario");

        //Email
        validarSinEspacios(nuevoCliente.getEmail(), "email");
        

        //Validaciones de Formato
        //Validar dominio
        clasificarDominio(nuevoCliente.getEmail());

        //Validar tipo de persona y el correo 
        validarEmailPorTipoPersona(nuevoCliente.getTipoPersona(), nuevoCliente.getEmail());

        // Normalizar y validar formato de la cédula (acepta con o sin prefijo V/E)
        String cedulaNormalizada = normalizeCedula(nuevoCliente.getCedula());
        nuevoCliente.setCedula(cedulaNormalizada);
        validarFormatoCedula(nuevoCliente.getCedula()); 

        //Validar formato del telefono 
        validarFormatoTelefono(nuevoCliente.getTelefono());

        //Validar formato de la contraseña 
        validarFormatoContrasena(nuevoCliente.getContrasena());


        //Validacion de unicidad: comprobar email primero para detectar duplicados por correo
        //Email
        if(clienteRepository.findByEmail(nuevoCliente.getEmail()).isPresent()) {
            throw new RegistroClienteException("El correo ingresado se encuentra registrado debe ingresar otro para continuar", 409);
        }

        //Usuario
        if(clienteRepository.findByUsuario(nuevoCliente.getUsuario()).isPresent()) {
            throw new RegistroClienteException("El usuario ingresado se encuentra registrado ingresa otro para continuar", 409);
        }

        //Cedula
        if(clienteRepository.findByCedula(nuevoCliente.getCedula()).isPresent()) {
            throw new RegistroClienteException("La cedula ingresada se encuentra registrada",409);
        }

        //Telefono
        if(clienteRepository.findByTelefono(nuevoCliente.getTelefono()).isPresent()) {
            throw new RegistroClienteException("El telefono se encuentra registrado ingrese otro para continuar",409);
        }


        //Simulacion del proceso lento (3 Segundos)
        try {
            System.out.println("Procesando el registro");
            Thread.sleep(3000); 
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RegistroClienteException("Error durante el procesamiento del registro", 400);
            
        }

        //Guardar en el repositorio es decir en la conexion a  la base de datos
        System.out.println("Registrado Exitosamente");
        Cliente clienteGuardado = clienteRepository.save(nuevoCliente); 
        obtenerTodos(); // <--- LLAMADA CLAVE: Imprime la lista actualizada
        return clienteGuardado;
    }

    
    /* --------------------------------------------- Actualizar Cliente -------------------------------------------------------------------------- */
    @Override
    public Cliente actualizarCliente(Cliente clienteActualizado) 
    {
    Cliente clienteExistente = clienteRepository.findByUsuario(clienteActualizado.getUsuario())
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado para la actualización.")); 

    if (clienteActualizado.getContrasena() != null && !clienteActualizado.getContrasena().isEmpty()) 
    {
        validarFormatoContrasena(clienteActualizado.getContrasena());
        clienteExistente.setContrasena(clienteActualizado.getContrasena());
    }

    validarSinEspacios(clienteActualizado.getEmail(), "email");
    // Normalizar cédula en actualizaciones también
    String cedulaNormalizada = normalizeCedula(clienteActualizado.getCedula());
    clienteActualizado.setCedula(cedulaNormalizada);
    validarFormatoCedula(clienteActualizado.getCedula());
    validarFormatoTelefono(clienteActualizado.getTelefono());

    clasificarDominio(clienteActualizado.getEmail());
    validarEmailPorTipoPersona(clienteActualizado.getTipoPersona(), clienteActualizado.getEmail());


    clienteRepository.findByEmail(clienteActualizado.getEmail()).ifPresent(duplicado -> {
        if (!clienteExistente.getUsuario().equals(duplicado.getUsuario())) {
            // Se encontró un duplicado, y no es el cliente que estamos actualizando.
            System.err.println("!!! CONFLICTO EMAIL !!!: El email '" + clienteActualizado.getEmail() + "' ya pertenece al usuario: " + duplicado.getUsuario());
            throw new RegistroClienteException("El nuevo correo ingresado ya está registrado por otro usuario.", 409);
        }
    });

    clienteRepository.findByCedula(clienteActualizado.getCedula()).ifPresent(duplicado -> {
        if (!clienteExistente.getUsuario().equals(duplicado.getUsuario())) {
            System.err.println("!!! CONFLICTO CÉDULA !!!: La cédula '" + clienteActualizado.getCedula() + "' ya pertenece al usuario: " + duplicado.getUsuario());
            throw new RegistroClienteException("La nueva cédula ingresada ya está registrada por otro usuario.", 409);
        }
    });


    clienteRepository.findByTelefono(clienteActualizado.getTelefono()).ifPresent(duplicado -> {
        if (!clienteExistente.getUsuario().equals(duplicado.getUsuario())) {
            System.err.println("!!! CONFLICTO TELÉFONO !!!: El teléfono '" + clienteActualizado.getTelefono() + "' ya pertenece al usuario: " + duplicado.getUsuario());
            throw new RegistroClienteException("El nuevo teléfono ya está registrado por otro usuario.", 409);
        }
    });
    
    // Transfiere los datos actualizados a la entidad existente (Mismo código que tenías)
    clienteExistente.setEmail(clienteActualizado.getEmail());
    clienteExistente.setCedula(clienteActualizado.getCedula());
    clienteExistente.setNombre(clienteActualizado.getNombre());
    clienteExistente.setApellido(clienteActualizado.getApellido());
    clienteExistente.setTipoPersona(clienteActualizado.getTipoPersona());
    clienteExistente.setDireccion(clienteActualizado.getDireccion());
    clienteExistente.setTelefono(clienteActualizado.getTelefono());

    Cliente clienteGuardado = clienteRepository.save(clienteExistente);
    obtenerTodos(); 
    return clienteGuardado;
    }

    //----------------------------- Normalizar Cédula ----------------------------------
    /**
     * Normaliza la cédula aceptando formatos como "V-12345678", "E12345678", "v 12345678" o "12345678".
     * Devuelve sólo los dígitos. Si la entrada es null lanzará RegistroClienteException.
     */
    private String normalizeCedula(String cedula) {
        if (cedula == null) {
            throw new RegistroClienteException("La cédula no puede ser nula", 400);
        }
        // Eliminar espacios y guiones, y caracteres no numéricos
        String cleaned = cedula.replaceAll("[^0-9]", "");
        return cleaned;
    }


    //------------------------------- Validar espacios ----------------------------------
    @Override
    public void validarSinEspacios( String valor, String nombreCampo)
    {
        //Si el valor esta vacio
        if (valor == null || valor.trim().isEmpty())
        {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede estar vacio", 400); 
        }

        //Verificar espacios intermedios 
        if (valor.contains(" "))
        {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede contener espacios en blanco", 400); 

        }

        System.out.println(nombreCampo + "  se encuentra sin espacios en blanco");
        
    }




    //-------------------------- Lista de dominios clasificados que estan permitidos -------------------------------------
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



    // ------------------------------------------------- Clasificar el dominio de email ----------------------------------------
    @Override
    public String clasificarDominio (String email)
    {
        //Si contiene el @
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



    //----------------------------------------- Validar Email Por Tipo de Persona ---------------------------------------------------------------------------------
    @Override
    public void validarEmailPorTipoPersona(String tipoPersona, String email) 
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
                throw new RegistroClienteException("Para el tipo 'UCAB', el email debe ser @ucab.edu.ve u @est.ucab.edu.ve", 400);
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
            throw new RegistroClienteException("El tipo de persona especificado ('" + tipoPersona + "') no es válido.", 400);
        }
        
        System.out.println(" El email (" + tipoDominio + ") es válido para el TipoPersona (" + tipo + ").");
    }


    // Ahora aceptamos solo números para la cédula (sin prefijos), entre 6 y 20 dígitos
    private static final String CEDULA_REGEX = "^\\d{6,20}$";


    //------------------------------------ Validar Formato de Cedula ----------------------------------------------------------
    @Override
    public void validarFormatoCedula(String cedula)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String cedulaLimpia = cedula.trim(); 

        //Verificacion del formato REGEX (solo dígitos)
        if(!cedulaLimpia.matches(CEDULA_REGEX)) {
            System.err.println("Formato de cédula inválido: " + cedulaLimpia);
            // CORREGIDO: Mensaje más descriptivo
            throw new RegistroClienteException("El formato de la cédula debe contener solo números después de normalización (ej. 12345678). Formatos aceptados: 12345678, V-12345678, E12345678", 400);
        }

        System.out.println("La cedula se ha registrado");
    }

    private static final String TELEFONO_REGEX = "^(0212|0424|0416|0426|0414)-\\d{7}$";

   //------------------------------------------- Validar Formato de Telefono --------------------------------------------------------
    @Override
    public void validarFormatoTelefono(String telefono)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String telefonoLimpio = telefono.trim(); 

        //Verificacion del formato REGEX
        if(!telefonoLimpio.matches(TELEFONO_REGEX))
        {
            System.err.println("Formato de telefono inválido: " + telefonoLimpio);
            throw new RegistroClienteException("El formato del telefono debe ser 0426-6112225", 400);
        }

        System.out.println("El telefono se ha registrado");
    }

    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";

    //------------------------------------------------- Validar Formato de la Contraseña ---------------------------------------------------
    @Override
    public void validarFormatoContrasena(String contrasena)
    {
        //Eliminamos espacios iniciales / finales para la validacion
        String ContrasenaLimpio = contrasena.trim(); 

        //Verificacion del formato REGEX
        if(!ContrasenaLimpio.matches(CONTRASENA_REGEX))
        {
            System.err.println("Formato de la contraseña es inválido: " + ContrasenaLimpio);
            throw new RegistroClienteException("La contraseña no cumple con los requisitos de seguridad. Debe tener un minimo de 8 caractere, incluir al menos una mayuscula y un número", 400);
        }

        System.out.println("La contrasena se ha registrado");
    }

    @Override
    public List<Cliente> obtenerTodos() 
    {
        //Obtener Lista del Repositorio
        List<Cliente>clientes = clienteRepository.findAll(); 

        //Imprimir el encabezado
        System.out.println("\n-- Lista de Clientes Registrados ");
        System.out.println(" USUARIO | EMAIL | CEDULA | NOMBRE | APELLIDO | TIPO DE PERSONA | DIRECCIÓN | TÉLEFONO ");
        System.out.println(" ------------------------------------------------------------------------------------- ");

        //Iterar sobre la lista e imprimir cada objeto 
        for (Cliente cliente: clientes)
        {
            System.out.printf("%s | %s | %s | %s | %s | %s | %s | %s \n", 
                cliente.getUsuario(),
                cliente.getEmail(),
                cliente.getCedula(), 
                cliente.getNombre(), 
                cliente.getApellido(),
                cliente.getTipoPersona(),
                cliente.getDireccion(), 
                cliente.getTelefono()
            );
        }

        System.out.println(" ------------------------------------------------------------------------------------- ");

        //Devolver la lista al controlador 
        return clientes; 
    }
}