package com.ucab.estacionamiento.repository;

import org.springframework.stereotype.Repository;
import com.ucab.estacionamiento.model.Cliente;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    // Simulación de la Base de Datos JSON
    private final List<Cliente> clientes = new ArrayList<>();
    
    //Metodo de guardar
    @Override
    public Cliente save(Cliente cliente) {

        
        this.clientes.add(cliente); 
        
        // Muestra un mensaje para confirmar que se guardó en la simulación
        System.out.println("DEBUG: Cliente " + cliente.getcedula() + " guardado en la lista simulada.");

        return cliente; // Retornamos el objeto guardado
    }

    // Metodo de existe cedula 
    @Override
    public boolean existsByCedula(String cedula) {
        // Usamos programación funcional (Streams) para buscar en la lista:
        // Verifica si AL MENOS UN objeto (anyMatch) en la lista tiene la cédula buscada.
        return clientes.stream()
            .anyMatch(c -> c.getcedula().equals(cedula));
    }
}
