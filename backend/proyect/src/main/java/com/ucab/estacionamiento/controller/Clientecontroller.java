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
//import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
//import java.util.Map;
//import java.util.HashMap;
//import org.springframework.beans.factory.annotation.Autowired; 
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity; 







@RestController //Indica que esta clase maneja peticiones REST
@RequestMapping("/api/clientes") //URL base para todos los metodos 
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:57145", "http://localhost:59035","http://localhost:59110"}, methods = {RequestMethod.GET, RequestMethod.POST})

public class Clientecontroller {
    

    private final ClienteService clienteService; 

    
    public Clientecontroller(ClienteService clienteService)
    {
        this.clienteService = clienteService; 
    }

    
    //Endpoint para el registro con JSON 
    @PostMapping("/registrar")
    
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente nuevoCliente) {
    
    Cliente clienteRegistrado = clienteService.registrarCliente(nuevoCliente);
    
    // Devuelve 201 Created si es exitoso
    return new ResponseEntity<>(clienteRegistrado, HttpStatus.CREATED);
}


    /*
     *     public ResponseEntity<Cliente> registrarCliente ( @RequestBody Cliente cliente)
    {
        System.out.println("Recibida peticion de registro para: " + cliente.getemail());

        try {
            //llama al servicio 
            Cliente clienteRegistrado = clienteService.registrarCliente(cliente); 
            return new ResponseEntity<> (clienteRegistrado, HttpStatus.CREATED); 
        } 
        catch (IllegalArgumentException e) {
            //Errores de validacion 
            System.out.println("Error de validacion: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        }
        catch (Exception e)
        {
            //Otros errores 
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
     */

    /*public Cliente registrarCliente(@RequestBody Cliente cliente) { 
        return clienteService.registrarCliente(cliente);
    }*/

    /*@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) 
    {
    
    // Creamos un mapa (JSON) para la respuesta de error
    Map<String, String> errorResponse = new HashMap<>();
    
    // 'mensaje' contiene el mensaje de error del Service (ej: "Su correo se encuentra registrado...")
    errorResponse.put("mensaje", ex.getMessage()); 
    
    // Retornamos un código HTTP 400 Bad Request
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); 
    
    }  */

}




