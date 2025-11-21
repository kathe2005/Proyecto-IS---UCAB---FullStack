package com.ucab.estacionamiento.service;

import org.springframework.stereotype.Service;
import com.ucab.estacionamiento.model.Cliente;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.repository.ClienteRepository;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente registrarCliente(Cliente nuevoCliente) {
        // Validaciones básicas de campos obligatorios
        validarCamposObligatorios(nuevoCliente);
        
        // Validar que las contraseñas coincidan
        if (!nuevoCliente.getContrasena().equals(nuevoCliente.getConfirmarContrasena())) {
            throw new RegistroClienteException("La confirmación de la contraseña debe ser igual a la contraseña", 400);
        }

        // Validar formato de la contraseña
        validarFormatoContrasena(nuevoCliente.getContrasena());

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

        // Simulación del proceso lento (3 Segundos)
        try {
            System.out.println("Procesando el registro...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RegistroClienteException("Error en el procesamiento del registro", 500);
        }

        // Establecer fecha de registro y estado
        nuevoCliente.setFechaRegistro(java.time.LocalDate.now().toString());
        nuevoCliente.setEstado("ACTIVO");

        // Guardar en el repositorio
        System.out.println("Registrado Exitosamente");
        return clienteRepository.guardar(nuevoCliente);
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
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
    
    private static final String CONTRASENA_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    private void validarFormatoContrasena(String contrasena) {
        if (contrasena == null) {
            throw new RegistroClienteException("La contraseña no puede ser nula", 400);
        }
        
        String contrasenaLimpia = contrasena.trim();

        if (!contrasenaLimpia.matches(CONTRASENA_REGEX)) {
            throw new RegistroClienteException("La contraseña debe tener mínimo 8 caracteres, incluir al menos una mayúscula, una minúscula y un número", 400);
        }

        System.out.println("La contraseña cumple con los requisitos de seguridad");
    }
}