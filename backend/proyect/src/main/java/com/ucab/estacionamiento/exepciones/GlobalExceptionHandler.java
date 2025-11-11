package com.ucab.estacionamiento.exepciones;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Necesaria para forzar el Content-Type
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice indica que esta clase maneja excepciones de todos los controladores
@ControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler indica que este método maneja solo la excepción RegistroClienteException

@ExceptionHandler(RegistroClienteException.class)
public ResponseEntity<Map<String,Object>> handleRegistroClienteException(RegistroClienteException ex) { 
    // 1. Creamos la estructura JSON que Angular espera
    Map<String, Object> body = new HashMap<>();
    body.put("mensajeError", ex.getMessage()); 
    
    // LÍNEA 27 CORREGIDA: Usamos .value() para obtener el INT (400, 409) del objeto HttpStatus
    body.put("codigo_error", ex.getHttpStatus().value()); 
    
    // 2. Creamos y forzamos las cabeceras a ser JSON
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON); 

    // LÍNEA 37 (CORRECTA): Pasamos el OBJETO HttpStatus directamente
    return new ResponseEntity<>(body, headers, ex.getHttpStatus());
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    
    String mensajeError = ex.getBindingResult().getAllErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining(" | ")); // Añadí un separador para que se vea mejor

    Map<String, Object> body = new HashMap<>();
    body.put("mensajeError", mensajeError); 
    body.put("codigo_error", HttpStatus.BAD_REQUEST.value()); 

    // 🔑 LÍNEAS CLAVE AÑADIDAS: Forzar la respuesta a ser JSON
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 3. Devolvemos la respuesta usando las CABECERAS, el CUERPO y el STATUS
    return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
}

    
}