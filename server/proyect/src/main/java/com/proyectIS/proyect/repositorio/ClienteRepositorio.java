package com.proyectIS.proyect.repositorio; 

import com.proyectIS.proyect.cliente.cliente.Cliente; 
import java.util.List;
import java.util.Optional;

public interface ClienteRepositorio 
{
    //Declamos los metodos que el ClienteService necesita
    
    /** Guarda un cliente (simularemos la escritura al JSON) */
    Cliente save(Cliente cliente); 

    /** Obtener todos los clientes (simularemos la lectura del JSON) */
    List<Cliente>findAll(); 

    /** Busca un cliente por su ID */
    Optional<Cliente> findById(Long id);

}

