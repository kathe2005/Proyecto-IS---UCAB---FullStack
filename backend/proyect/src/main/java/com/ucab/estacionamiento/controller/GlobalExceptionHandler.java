package com.ucab.estacionamiento.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.model.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseBody;

// indica que esta clase maneja excepciones de todos los controladores

@ControllerAdvice
public class GlobalExceptionHandler 
{
    @ExceptionHandler(RegistroClienteException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRegistroClienteException(RegistroClienteException ex) { 

        // 1. Obtiene el código HTTP (400 o 409) de tu excepción personalizada
        HttpStatus status = HttpStatus.valueOf(ex.getCodigoError()); 

        // 2. Crea el objeto de respuesta estructurado
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getCodigoError());
        
        // 3. Devuelve la respuesta, asegurando que el cuerpo sea JSON y el estado HTTP sea el correcto
        return new ResponseEntity<>(errorResponse, status);
    }
}