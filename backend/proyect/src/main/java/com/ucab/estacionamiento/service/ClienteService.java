package com.ucab.estacionamiento.service;

import org.springframework.stereotype.Service;
import com.ucab.estacionamiento.model.Cliente;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.repository.ClienteRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente registrarCliente(Cliente nuevoCliente) {
        // Validaciones básicas de campos obligatorios
        validarCamposObligatorios(nuevoCliente);
        
        // Validar espacios en blanco
        validarSinEspacios(nuevoCliente.getUsuario(), "usuario");
        validarSinEspacios(nuevoCliente.getContrasena(), "contrasena");
        validarSinEspacios(nuevoCliente.getConfirmarContrasena(), "confirmarContrasena");
        validarSinEspacios(nuevoCliente.getEmail(), "email");

        // Validar que las contraseñas coincidan
        if (!nuevoCliente.getContrasena().equals(nuevoCliente.getConfirmarContrasena())) {
            throw new RegistroClienteException("La confirmación de la contraseña debe ser igual a la contraseña", 400);
        }

        // Validar formato de la contraseña
        validarFormatoContrasena(nuevoCliente.getContrasena());

        // Validar formato de la cédula (V-/E-)
        validarFormatoCedula(nuevoCliente.getCedula());

        // Validar formato del teléfono
        validarFormatoTelefono(nuevoCliente.getTelefono());

        // Validar dominio y tipo de persona
        validarEmailPorTipoPersona(nuevoCliente.getTipoPersona(), nuevoCliente.getEmail());

        // Validación de unicidad
        if (clienteRepository.findByUsuario(nuevoCliente.getUsuario()).isPresent()) {
            throw new RegistroClienteException("El usuario ingresado se encuentra registrado. Ingresa otro para continuar", 409);
        }

        if (clienteRepository.findByCedula(nuevoCliente.getCedula()).isPresent()) {
            throw new RegistroClienteException("La cédula ingresada se encuentra registrada", 409);
        }

        if (clienteRepository.findByEmail(nuevoCliente.getEmail()).isPresent()) {
            throw new RegistroClienteException("El correo ingresado se encuentra registrado. Debe ingresar otro para continuar", 409);
        }

        if (clienteRepository.findByTelefono(nuevoCliente.getTelefono()).isPresent()) {
            throw new RegistroClienteException("El teléfono se encuentra registrado. Ingrese otro para continuar", 409);
        }

        // Simulación del proceso lento (3 Segundos)
        try {
            System.out.println("Procesando el registro...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RegistroClienteException("Error en el procesamiento del registro", 500);
        }

        // Guardar en el repositorio
        System.out.println("Registrado Exitosamente");
        return clienteRepository.guardar(nuevoCliente);
    }

    // Método para consultar todos los clientes
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    // NUEVO: Método para obtener cliente por ID
    public Cliente obtenerClientePorId(UUID id) {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RegistroClienteException("Cliente no encontrado con ID: " + id, 404));
    }

    // NUEVO: Método para modificar cliente
    public Cliente modificarCliente(UUID id, Cliente clienteActualizado) {
        // Validar que el cliente existe
        Cliente clienteExistente = obtenerClientePorId(id);
        
        // Validaciones básicas de campos obligatorios
        validarCamposObligatorios(clienteActualizado);
        
        // Validar espacios en blanco
        validarSinEspacios(clienteActualizado.getUsuario(), "usuario");
        validarSinEspacios(clienteActualizado.getEmail(), "email");

        // Validar formato de la cédula
        validarFormatoCedula(clienteActualizado.getCedula());

        // Validar formato del teléfono
        validarFormatoTelefono(clienteActualizado.getTelefono());

        // Validar dominio y tipo de persona
        validarEmailPorTipoPersona(clienteActualizado.getTipoPersona(), clienteActualizado.getEmail());

        // Validación de unicidad (excluyendo el cliente actual)
        validarUnicidadEnModificacion(id, clienteActualizado);

        // Actualizar los campos del cliente existente
        clienteExistente.setUsuario(clienteActualizado.getUsuario());
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setCedula(clienteActualizado.getCedula());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setTipoPersona(clienteActualizado.getTipoPersona());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());

        // Guardar en el repositorio
        return clienteRepository.guardar(clienteExistente);
    }

    // NUEVO: Método para validar unicidad en modificación
    private void validarUnicidadEnModificacion(UUID idClienteActual, Cliente clienteActualizado) {
        // Validar usuario único (excluyendo el cliente actual)
        clienteRepository.findByUsuario(clienteActualizado.getUsuario())
                .ifPresent(cliente -> {
                    if (!cliente.getId().equals(idClienteActual)) {
                        throw new RegistroClienteException("El usuario ingresado ya se encuentra registrado", 409);
                    }
                });

        // Validar cédula única (excluyendo el cliente actual)
        clienteRepository.findByCedula(clienteActualizado.getCedula())
                .ifPresent(cliente -> {
                    if (!cliente.getId().equals(idClienteActual)) {
                        throw new RegistroClienteException("La cédula ingresada ya se encuentra registrada", 409);
                    }
                });

        // Validar email único (excluyendo el cliente actual)
        clienteRepository.findByEmail(clienteActualizado.getEmail())
                .ifPresent(cliente -> {
                    if (!cliente.getId().equals(idClienteActual)) {
                        throw new RegistroClienteException("El correo ingresado ya se encuentra registrado", 409);
                    }
                });

        // Validar teléfono único (excluyendo el cliente actual)
        clienteRepository.findByTelefono(clienteActualizado.getTelefono())
                .ifPresent(cliente -> {
                    if (!cliente.getId().equals(idClienteActual)) {
                        throw new RegistroClienteException("El teléfono ingresado ya se encuentra registrado", 409);
                    }
                });
    }

    private void validarSinEspacios(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede estar vacío", 400);
        }

        if (valor.contains(" ")) {
            throw new RegistroClienteException("El campo " + nombreCampo + " no puede contener espacios en blanco", 400);
        }

        System.out.println(nombreCampo + " se encuentra sin espacios en blanco");
    }

    private void validarCamposObligatorios(Cliente cliente) {
        if (cliente.getUsuario() == null || cliente.getUsuario().trim().isEmpty()) {
            throw new RegistroClienteException("El usuario no puede estar vacío", 400);
        }

        if (cliente.getContrasena() == null || cliente.getContrasena().trim().isEmpty()) {
            throw new RegistroClienteException("La contraseña no puede estar vacía", 400);
        }

        if (cliente.getConfirmarContrasena() == null || cliente.getConfirmarContrasena().trim().isEmpty()) {
            throw new RegistroClienteException("La confirmación de la contraseña no puede estar vacía", 400);
        }

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new RegistroClienteException("El nombre no puede estar vacío", 400);
        }

        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new RegistroClienteException("El apellido no puede estar vacío", 400);
        }

        if (cliente.getCedula() == null || cliente.getCedula().trim().isEmpty()) {
            throw new RegistroClienteException("La cédula no puede estar vacía", 400);
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new RegistroClienteException("El email no puede estar vacío", 400);
        }

        if (cliente.getTipoPersona() == null || cliente.getTipoPersona().trim().isEmpty()) {
            throw new RegistroClienteException("El tipo de persona no puede estar vacío", 400);
        }

        if (cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) {
            throw new RegistroClienteException("La dirección no puede estar vacía", 400);
        }

        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new RegistroClienteException("El teléfono no puede estar vacío", 400);
        }
    }

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

    private String clasificarDominio(String email) {
        if (!email.contains("@")) {
            throw new RegistroClienteException("El formato del email es inválido: falta el '@'", 400);
        }

        String dominio = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

        if (DOMINIOS_ACADEMICOS.contains(dominio)) {
            System.out.println("Dominio ACADÉMICO: " + dominio);
            return "ACADEMICO";
        } else if (DOMINIOS_GMAIL.contains(dominio)) {
            System.out.println("Dominio GMAIL: " + dominio);
            return "GMAIL";
        } else if (DOMINIOS_CORPORATIVOS.contains(dominio)) {
            System.out.println("Dominio CORPORATIVO: " + dominio);
            return "CORPORATIVO";
        } else {
            System.err.println("El dominio no está permitido: " + dominio);
            throw new RegistroClienteException("El dominio '" + dominio + "' no está permitido para el registro.", 400);
        }
    }

    private void validarEmailPorTipoPersona(String tipoPersona, String email) {
        if (tipoPersona == null) {
            throw new RegistroClienteException("El tipo de persona no puede ser nulo", 400);
        }
        
        String tipo = tipoPersona.toUpperCase().trim();
        String tipoDominio = clasificarDominio(email);

        if ("UCAB".equals(tipo)) {
            if (!"ACADEMICO".equals(tipoDominio)) {
                System.err.println("El usuario UCAB registrado no está permitido.");
                throw new RegistroClienteException("Para el tipo 'UCAB', el email debe ser @ucab.edu.ve u @est.ucab.edu.ve", 400);
            }
        } else if ("VISITANTE".equals(tipo)) {
            if ("ACADEMICO".equals(tipoDominio)) {
                System.err.println("El visitante registrado no está permitido su dominio");
                throw new RegistroClienteException("Para el tipo 'VISITANTE', el email debe ser @gmail.com, @outlook.com, @yahoo.com u @hotmail.com", 400);
            }
        } else {
            throw new RegistroClienteException("El tipo de persona especificado ('" + tipoPersona + "') no es válido. Debe ser UCAB o VISITANTE", 400);
        }
        
        System.out.println("El email (" + tipoDominio + ") es válido para el TipoPersona (" + tipo + ").");
    }

    // REGEX CORREGIDO - Formato V-/E- con 6-8 dígitos
    private static final String CEDULA_REGEX = "^[VEve]-\\d{6,8}$";

    private void validarFormatoCedula(String cedula) {
        if (cedula == null) {
            throw new RegistroClienteException("La cédula no puede ser nula", 400);
        }
        
        String cedulaLimpia = cedula.trim().toUpperCase();

        if (!cedulaLimpia.matches(CEDULA_REGEX)) {
            System.err.println("Formato de cédula inválido: " + cedulaLimpia);
            throw new RegistroClienteException("El formato de la cédula debe ser V-12345678 o E-12345678 (V o E seguido de guion y 6-8 dígitos)", 400);
        }

        System.out.println("La cédula se ha validado correctamente: " + cedulaLimpia);
    }

    private static final String TELEFONO_REGEX = "^(0212|0424|0416|0426|0414|0412)-\\d{7}$";

    private void validarFormatoTelefono(String telefono) {
        if (telefono == null) {
            throw new RegistroClienteException("El teléfono no puede ser nulo", 400);
        }
        
        String telefonoLimpio = telefono.trim();

        if (!telefonoLimpio.matches(TELEFONO_REGEX)) {
            System.err.println("Formato de teléfono inválido: " + telefonoLimpio);
            throw new RegistroClienteException("El formato del teléfono debe ser 0412-6112225 (código de área de 4 dígitos, guion y 7 dígitos)", 400);
        }

        System.out.println("El teléfono se ha validado correctamente: " + telefonoLimpio);
    }
    
    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    private void validarFormatoContrasena(String contrasena) {
        if (contrasena == null) {
            throw new RegistroClienteException("La contraseña no puede ser nula", 400);
        }
        
        String contrasenaLimpia = contrasena.trim();

        if (!contrasenaLimpia.matches(CONTRASENA_REGEX)) {
            System.err.println("Formato de la contraseña es inválido: " + contrasenaLimpia);
            throw new RegistroClienteException("La contraseña no cumple con los requisitos de seguridad. Debe tener mínimo 8 caracteres, incluir al menos una mayúscula, una minúscula y un número", 400);
        }

        System.out.println("La contraseña cumple con los requisitos de seguridad");
    }
}