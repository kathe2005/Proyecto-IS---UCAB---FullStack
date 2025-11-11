package com.ucab.estacionamiento.exepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice indica que esta clase maneja excepciones de todos los controladores
@ControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler indica que este método maneja solo la excepción RegistroClienteException
    @ExceptionHandler(RegistroClienteException.class)
    public ResponseEntity<Object> handleRegistroClienteException(RegistroClienteException ex) {
        
        // Creamos la estructura JSON que Angular espera (la que tiene el campo 'mensaje')
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatus());
        body.put("error", HttpStatus.resolve(ex.getStatus()).getReasonPhrase());
        body.put("mensaje", ex.getMessage()); // ¡Aquí inyectamos el mensaje del Service!
        
        // Devolvemos la respuesta con el status HTTP que definimos en la excepción (ej. 409)
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getStatus()));
    }
}
