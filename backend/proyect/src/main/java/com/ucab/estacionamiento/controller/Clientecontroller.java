//Recibir el JSON 
package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente; 
import com.ucab.estacionamiento.service.ClienteService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import java.util.List;
@RestController //Indica que esta clase maneja peticiones REST
@RequestMapping(value = "api/cliente", produces = "application/json") //URL base para todos los metodos 
@CrossOrigin(origins = "http://localhost:4200")
public class Clientecontroller {
    

    private final ClienteService clienteService; 

    
    public Clientecontroller(ClienteService clienteService)
    {
        this.clienteService = clienteService; 
    }

    @GetMapping("/obtenerTodo")
    public List<Cliente> obtenerTodosLosClientes() 
    {
        return clienteService.obtenerTodos(); 
    }

    //Endpoint para el registro con JSON 
    @PostMapping(value = "/registrar", consumes = "application/json")
    public Cliente registrarCliente( @RequestBody Cliente nuevoCliente)
    {
        return clienteService.registrarCliente(nuevoCliente);
    }


    @PutMapping(value = "/actualizar", consumes = "application/json")
    public Cliente actualizarCliente(@RequestBody Cliente clienteActualizado) 
    {
        return clienteService.actualizarCliente(clienteActualizado); 
    }
}




