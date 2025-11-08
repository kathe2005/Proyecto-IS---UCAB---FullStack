//Recibir el JSON 
package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente; 
import com.ucab.estacionamiento.service.ClienteService;
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 

@RestController //Indica que esta clase maneja peticiones REST
@RequestMapping("/api/clientes")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:57145", "http://localhost:59035","http://localhost:59110"}, methods = {RequestMethod.GET, RequestMethod.POST})

public class Clientecontroller {
    
    @Autowired
    private ClienteService clienteService; 

    @PostMapping("/registrar")
    public ResponseEntity<Cliente> registrarCliente ( @RequestBody Cliente clienteRecibido )
    {
        Cliente nuevoCliente = clienteService.guardarCliente(clienteRecibido); 
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED); 
    }
}


