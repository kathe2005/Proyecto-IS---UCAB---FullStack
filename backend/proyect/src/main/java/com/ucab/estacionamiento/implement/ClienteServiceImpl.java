package com.ucab.estacionamiento.implement;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.model.clases.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteServiceImpl {

    private final JsonManagerCliente jsonManagerCliente;

    public ClienteServiceImpl() {
        this.jsonManagerCliente = new JsonManagerCliente();
        System.out.println("✅ ClienteServiceImpl inicializado con JsonManagerCliente");
        System.out.println("👥 Clientes cargados: " + jsonManagerCliente.obtenerTodosClientes().size());
    }

    public Cliente registrarCliente(Cliente nuevoCliente) {
        System.out.println("👤 Iniciando registro de cliente: " + nuevoCliente.getUsuario());
        
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

        // Normalizar y validar formato de la cédula
        String cedulaNormalizada = normalizeCedula(nuevoCliente.getCedula());
        nuevoCliente.setCedula(cedulaNormalizada);
        validarFormatoCedula(nuevoCliente.getCedula()); 

        //Validar formato del telefono 
        validarFormatoTelefono(nuevoCliente.getTelefono());

        //Validar formato de la contraseña 
        validarFormatoContrasena(nuevoCliente.getContrasena());

        //Validacion de unicidad
        //Email
        if(jsonManagerCliente.buscarPorEmail(nuevoCliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo ingresado se encuentra registrado debe ingresar otro para continuar");
        }

        //Usuario
        if(jsonManagerCliente.buscarPorUsuario(nuevoCliente.getUsuario()).isPresent()) {
            throw new IllegalArgumentException("El usuario ingresado se encuentra registrado ingresa otro para continuar");
        }

        //Cedula
        if(jsonManagerCliente.buscarPorCedula(nuevoCliente.getCedula()).isPresent()) {
            throw new IllegalArgumentException("La cedula ingresada se encuentra registrada");
        }

        //Telefono
        if(jsonManagerCliente.buscarPorTelefono(nuevoCliente.getTelefono()).isPresent()) {
            throw new IllegalArgumentException("El telefono se encuentra registrado ingrese otro para continuar");
        }

        // Asignar ID si no existe
        if (nuevoCliente.getId() == null) {
            nuevoCliente.setId(UUID.randomUUID());
        }

        //Simulacion del proceso lento (3 Segundos)
        try {
            System.out.println("⏳ Procesando el registro...");
            Thread.sleep(3000); 
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error durante el procesamiento del registro");
        }

        //Guardar en el repositorio
        System.out.println("💾 Guardando cliente en JSON...");
        Cliente clienteGuardado = jsonManagerCliente.guardarCliente(nuevoCliente); 
        obtenerTodos();
        System.out.println("✅ Registrado Exitosamente: " + nuevoCliente.getUsuario());
        return clienteGuardado;
    }

    public Cliente actualizarCliente(Cliente clienteActualizado) {
        System.out.println("🔄 Iniciando actualización de cliente: " + clienteActualizado.getUsuario());
        
        Cliente clienteExistente = jsonManagerCliente.buscarPorUsuario(clienteActualizado.getUsuario())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado para la actualización.")); 

        if (clienteActualizado.getContrasena() != null && !clienteActualizado.getContrasena().isEmpty()) {
            validarFormatoContrasena(clienteActualizado.getContrasena());
            clienteExistente.setContrasena(clienteActualizado.getContrasena());
        }

        validarSinEspacios(clienteActualizado.getEmail(), "email");
        String cedulaNormalizada = normalizeCedula(clienteActualizado.getCedula());
        clienteActualizado.setCedula(cedulaNormalizada);
        validarFormatoCedula(clienteActualizado.getCedula());
        validarFormatoTelefono(clienteActualizado.getTelefono());

        clasificarDominio(clienteActualizado.getEmail());
        validarEmailPorTipoPersona(clienteActualizado.getTipoPersona(), clienteActualizado.getEmail());

        // Validar duplicados en actualización
        jsonManagerCliente.buscarPorEmail(clienteActualizado.getEmail()).ifPresent(duplicado -> {
            if (!clienteExistente.getId().equals(duplicado.getId())) {
                System.err.println("!!! CONFLICTO EMAIL !!!: El email '" + clienteActualizado.getEmail() + "' ya pertenece al usuario: " + duplicado.getUsuario());
                throw new IllegalArgumentException("El nuevo correo ingresado ya está registrado por otro usuario.");
            }
        });

        jsonManagerCliente.buscarPorCedula(clienteActualizado.getCedula()).ifPresent(duplicado -> {
            if (!clienteExistente.getId().equals(duplicado.getId())) {
                System.err.println("!!! CONFLICTO CÉDULA !!!: La cédula '" + clienteActualizado.getCedula() + "' ya pertenece al usuario: " + duplicado.getUsuario());
                throw new IllegalArgumentException("La nueva cédula ingresada ya está registrada por otro usuario.");
            }
        });

        jsonManagerCliente.buscarPorTelefono(clienteActualizado.getTelefono()).ifPresent(duplicado -> {
            if (!clienteExistente.getId().equals(duplicado.getId())) {
                System.err.println("!!! CONFLICTO TELÉFONO !!!: El teléfono '" + clienteActualizado.getTelefono() + "' ya pertenece al usuario: " + duplicado.getUsuario());
                throw new IllegalArgumentException("El nuevo teléfono ya está registrado por otro usuario.");
            }
        });
        
        // Transfiere los datos actualizados
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setCedula(clienteActualizado.getCedula());
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setTipoPersona(clienteActualizado.getTipoPersona());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());

        Cliente clienteGuardado = jsonManagerCliente.guardarCliente(clienteExistente);
        obtenerTodos(); 
        System.out.println("✅ Cliente actualizado exitosamente: " + clienteActualizado.getUsuario());
        return clienteGuardado;
    }

    public boolean eliminarCliente(String id) {
        return jsonManagerCliente.eliminarCliente(id);
    }

    // ------------------------- MÉTODOS DE VALIDACIÓN -------------------------

    private String normalizeCedula(String cedula) {
        if (cedula == null) {
            throw new IllegalArgumentException("La cédula no puede ser nula");
        }
        String cleaned = cedula.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("La cédula debe contener números");
        }
        return cleaned;
    }

    public void validarSinEspacios(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede estar vacio");
        }

        if (valor.contains(" ")) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede contener espacios en blanco");
        }

        System.out.println("✅ " + nombreCampo + " se encuentra sin espacios en blanco");
    }

    // Dominios permitidos
    private static final List<String> DOMINIOS_ACADEMICOS = List.of(
        "ucab.edu.ve",
        "est.ucab.edu.ve"
    ); 

    private static final List<String> DOMINIOS_GMAIL = List.of(
        "gmail.com",
        "googlemail.com"
    ); 

    private static final List<String> DOMINIOS_CORPORATIVOS = List.of(
        "outlook.com",
        "yahoo.com",
        "hotmail.com"
    ); 

    public String clasificarDominio(String email) {
        if(!email.contains("@")) {
            throw new IllegalArgumentException("El formato del email es invalido: falta el '@'");
        }

        String dominio = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        if (DOMINIOS_ACADEMICOS.contains(dominio)) {
            System.out.println("🎓 Dominio ACADÉMICO: " + dominio);
            return "ACADEMICO";
        } else if (DOMINIOS_GMAIL.contains(dominio)) {
            System.out.println("📧 Dominio GMAIL: " + dominio);
            return "GMAIL";
        } else if (DOMINIOS_CORPORATIVOS.contains(dominio)) {
            System.out.println("🏢 Dominio CORPORATIVO: " + dominio);
            return "CORPORATIVO";
        } else {
            System.err.println("❌ Dominio no permitido: " + dominio);
            throw new IllegalArgumentException("El dominio '" + dominio + "' no está permitido para el registro.");
        }
    }

    public void validarEmailPorTipoPersona(String tipoPersona, String email) {
        String tipo = tipoPersona.toUpperCase().trim();
        String tipoDominio = clasificarDominio(email);

        if ("UCAB".equals(tipo)) {
            if (!"ACADEMICO".equals(tipoDominio)) {
                System.err.println("❌ Usuario UCAB con dominio no académico");
                throw new IllegalArgumentException("Para el tipo 'UCAB', el email debe ser @ucab.edu.ve u @est.ucab.edu.ve");
            }
            System.out.println("✅ Email válido para tipo UCAB");
        } else if ("VISITANTE".equals(tipo)) {
            if ("ACADEMICO".equals(tipoDominio)) {
                System.err.println("❌ Visitante con dominio académico");
                throw new IllegalArgumentException("Para el tipo 'VISITANTE', el email debe ser @gmail.com, @outlook.com, yahoo.com u hotmail.com");
            }
            System.out.println("✅ Email válido para tipo VISITANTE");
        } else {
            throw new IllegalArgumentException("El tipo de persona especificado ('" + tipoPersona + "') no es válido.");
        }
    }

    private static final String CEDULA_REGEX = "^\\d{6,20}$";

    public void validarFormatoCedula(String cedula) {
        String cedulaLimpia = cedula.trim(); 
        if(!cedulaLimpia.matches(CEDULA_REGEX)) {
            System.err.println("❌ Formato de cédula inválido: " + cedulaLimpia);
            throw new IllegalArgumentException("El formato de la cédula debe contener solo números después de normalización (ej. 12345678). Formatos aceptados: 12345678, V-12345678, E12345678");
        }
        System.out.println("✅ Cédula válida: " + cedulaLimpia);
    }

    private static final String TELEFONO_REGEX = "^(0212|0424|0416|0426|0414)-\\d{7}$";

    public void validarFormatoTelefono(String telefono) {
        String telefonoLimpio = telefono.trim(); 
        if(!telefonoLimpio.matches(TELEFONO_REGEX)) {
            System.err.println("❌ Formato de teléfono inválido: " + telefonoLimpio);
            throw new IllegalArgumentException("El formato del teléfono debe ser 0426-6112225");
        }
        System.out.println("✅ Teléfono válido: " + telefonoLimpio);
    }

    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";

    public void validarFormatoContrasena(String contrasena) {
        String contrasenaLimpia = contrasena.trim(); 
        if(!contrasenaLimpia.matches(CONTRASENA_REGEX)) {
            System.err.println("❌ Formato de contraseña inválido");
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad. Debe tener un mínimo de 8 caracteres, incluir al menos una mayúscula y un número");
        }
        System.out.println("✅ Contraseña válida");
    }

    // ------------------------- MÉTODOS DE CONSULTA -------------------------

    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = jsonManagerCliente.obtenerTodosClientes(); 
        System.out.println("\n📋 Lista de Clientes Registrados (" + clientes.size() + " clientes)");
        System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ USUARIO     │ EMAIL                    │ CEDULA     │ NOMBRE     │ APELLIDO  │ TIPO       │ TELÉFONO     │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────────────────────────────────────┤");
        
        for (Cliente cliente: clientes) {
            System.out.printf("│ %-11s │ %-23s │ %-10s │ %-10s │ %-9s │ %-9s │ %-12s │\n", 
                truncar(cliente.getUsuario(), 11),
                truncar(cliente.getEmail(), 23),
                truncar(cliente.getCedula(), 10),
                truncar(cliente.getNombre(), 10),
                truncar(cliente.getApellido(), 9),
                truncar(cliente.getTipoPersona(), 9),
                truncar(cliente.getTelefono(), 12)
            );
        }
        System.out.println("└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘");
        return clientes; 
    }

    private String truncar(String texto, int longitud) {
        if (texto == null) return "";
        return texto.length() > longitud ? texto.substring(0, longitud - 3) + "..." : texto;
    }

    public Optional<Cliente> obtenerPorUsuario(String usuario) {
        return jsonManagerCliente.buscarPorUsuario(usuario);
    }

    public Optional<Cliente> obtenerPorCedula(String cedula) {
        return jsonManagerCliente.buscarPorCedula(cedula);
    }

    public Optional<Cliente> obtenerPorEmail(String email) {
        return jsonManagerCliente.buscarPorEmail(email);
    }

    public Optional<Cliente> obtenerPorTelefono(String telefono) {
        return jsonManagerCliente.buscarPorTelefono(telefono);
    }

    public Optional<Cliente> obtenerPorId(String id) {
        return jsonManagerCliente.buscarPorId(id);
    }

    // ------------------------- MÉTODOS ADICIONALES -------------------------

    public boolean existeClientePorUsuario(String usuario) {
        return jsonManagerCliente.buscarPorUsuario(usuario).isPresent();
    }

    public boolean existeClientePorEmail(String email) {
        return jsonManagerCliente.buscarPorEmail(email).isPresent();
    }

    public boolean existeClientePorCedula(String cedula) {
        return jsonManagerCliente.buscarPorCedula(cedula).isPresent();
    }

    public void diagnostico() {
        System.out.println("🩺 DIAGNÓSTICO DEL SERVICIO CLIENTE");
        jsonManagerCliente.diagnostico();
        List<Cliente> clientes = jsonManagerCliente.obtenerTodosClientes();
        System.out.println("📊 Total clientes registrados: " + clientes.size());
        
        long ucabCount = clientes.stream().filter(c -> "UCAB".equalsIgnoreCase(c.getTipoPersona())).count();
        long visitanteCount = clientes.stream().filter(c -> "VISITANTE".equalsIgnoreCase(c.getTipoPersona())).count();
        
        System.out.println("🎓 Clientes UCAB: " + ucabCount);
        System.out.println("👤 Clientes Visitantes: " + visitanteCount);
    }
}