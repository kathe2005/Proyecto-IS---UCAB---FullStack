//Recibir el JSON 
package com.ucab.estacionamiento.controller;

//import com.ucab.estacionamiento.model.Cliente; 
import com.ucab.estacionamiento.service.ClienteService;
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.ucab.estacionamiento.DTO.ClienteRegistroDTO;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;


//import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

//import org.springframework.http.HttpStatus;
//import java.util.Map;
//import java.util.HashMap;
//import org.springframework.beans.factory.annotation.Autowired; 
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity; 


@RestController //Indica que esta clase maneja peticiones REST
@RequestMapping("/api") //URL base para todos los metodos 
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:54733" })
public class Clientecontroller {
    

    @Autowired
    private final ClienteService clienteService; 

    
    public Clientecontroller(ClienteService clienteService)
    {
        this.clienteService = clienteService; 
    }

    
    //Endpoint para el registro con JSON 
    @PostMapping("/cliente/registrar")
    
    public ResponseEntity<?> registrarCliente(@Validated @RequestBody ClienteRegistroDTO nuevoCliente) {


    if (!nuevoCliente.getContrasena().equals(nuevoCliente.getConfirmcontrasena())) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("mensaje", "Las contraseñas enviadas no coinciden."));
    }

    try {

        // Usamos la entidad Cliente en el import 
        Cliente clienteGuardado = clienteService.registrarCliente(nuevoCliente);
        
        // 3. RESPUESTA EXITOSA 
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);

    } catch (RegistroClienteException e) {
        // Manejo de errores específicos (400 Bad Request, 409 Conflict)
        return ResponseEntity
            .status(e.getHttpStatus()) 
            .body(Map.of("mensaje", e.getMessage()));
    } catch (Exception e) {
        // Manejo de errores genéricos (500 Internal Server Error)
        return ResponseEntity
            .internalServerError()
            .body(Map.of("mensaje", "Ocurrió un error inesperado en el servidor: " + e.getMessage()));
    }
}

}




