package com.proyectIS.proyect.repositorio;

import com.proyectIS.proyect.cliente.cliente.Cliente;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

//Implementa la interfaz ClienteRepositorio 
@Repository // Marca esta clase como el Repositorio que Spring debe usar
public class ClienteRepositorioJSONImpl implements ClienteRepositorio {

    // 1. Simulación de la Base de Datos: Guardamos los objetos en una lista en memoria
    private final List<Cliente> clientes = new ArrayList<>();
    
    // 2. Simulación del ID autoincremental
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Guarda o actualiza un cliente.
     */
    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            // Asigna un ID al nuevo cliente antes de guardarlo en la lista
            cliente.setId(nextId.getAndIncrement());
            clientes.add(cliente);
        } else {
            // Lógica simple de actualización: reemplaza el cliente existente
            clientes.removeIf(c -> c.getId().equals(cliente.getId()));
            clientes.add(cliente);
        }
        
        return cliente;
    }

    /**
     * Obtiene todos los clientes de la lista en memoria.
     */
    @Override
    public List<Cliente> findAll() {
        return clientes;
    }

    /**
     * Busca un cliente en la lista por su ID.
     */
    @Override
    public Optional<Cliente> findById(Long id) {
        // Usa streams para buscar el primer cliente cuyo ID coincida con el ID dado
        return clientes.stream()
        .filter(cliente -> cliente.getId().equals(id))
        .findFirst(); // Devuelve un Optional<Cliente>
    }
}