//Procesar y Validar 

package com.ucab.estacionamiento.service;

import org.springframework.stereotype.Service;
import com.ucab.estacionamiento.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import com.ucab.estacionamiento.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired //Necesario para los atributos del Cliente y su conexion a la Base de Datos 
    private ClienteRepository clienteRepository;

    //Metodos 
    public Cliente guardarCliente(Cliente cliente)
    {
        //Aplicar validaciones 
        if (!validarDominioCorreo(cliente.getemail(), cliente.gettipoPersona()))
        {
            // Lanza una excepción si la validación falla
            throw new IllegalArgumentException("El dominio del correo no coincide.");
        }

        //Guardar en BD
        return clienteRepository.save(cliente); 

    }

    //Metodo de validaciones
    private boolean validarDominioCorreo(String email, String tipo) {
        // ... (Tu lógica de UCAB y Visitante)
        return true; 
    }
}
