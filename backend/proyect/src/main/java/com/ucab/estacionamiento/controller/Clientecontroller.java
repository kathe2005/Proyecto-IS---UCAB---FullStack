//Recibir el JSON 
package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente; 
import com.ucab.estacionamiento.service.ClienteService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.PathVariable;
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


    @PutMapping(value = "/actualizar/{usuario}", consumes = "application/json")
    public ResponseEntity<Cliente> actualizarCliente(
        @PathVariable String usuario,  @RequestBody Cliente clienteActualizado) 
        {
                clienteActualizado.setUsuario(usuario); 
                
                Cliente clienteGuardado = clienteService.actualizarCliente(clienteActualizado);
                return new ResponseEntity<>(clienteGuardado, HttpStatus.OK);
        }
}




