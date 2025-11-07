package com.proyectIS.proyect.cliente.servicio;

import com.proyectIS.proyect.cliente.cliente.Cliente;
import com.proyectIS.proyect.repositorio.ClienteRepositorio; 
// import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {


    private final ClienteRepositorio clienteRepositorio;
    // private final PasswordEncoder passwordEncoder;     

    // 🧱 Constructor 
    public ClienteService(ClienteRepositorio clienteRepositorio) { 
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * Guarda un cliente. TEMPORALMENTE sin hashing.
     */
    public Cliente guardarCliente(Cliente cliente) {
        
        // Guarda el cliente (con la contraseña en texto plano, temporalmente)
        return clienteRepositorio.save(cliente); 
    }
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepositorio.findAll();
    }
    
    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepositorio.findById(id);
    }
}