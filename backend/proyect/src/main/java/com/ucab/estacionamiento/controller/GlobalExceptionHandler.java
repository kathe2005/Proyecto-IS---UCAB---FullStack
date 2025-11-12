package com.ucab.estacionamiento.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import com.ucab.estacionamiento.model.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

// indica que esta clase maneja excepciones de todos los controladores

@ControllerAdvice
public class GlobalExceptionHandler 
{
    // Maneja tu excepción de negocio (400, 409)
    @ExceptionHandler(RegistroClienteException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRegistroClienteException(RegistroClienteException ex) { 
        HttpStatus status = HttpStatus.valueOf(ex.getCodigoError()); 
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getCodigoError());
        System.err.println("!!! EXCEPCIÓN DE NEGOCIO ATRAPADA (" + ex.getCodigoError() + "): " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    // Maneja errores 404 para URLs no mapeadas
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "Recurso no encontrado: La ruta " + ex.getRequestURL() + " no está mapeada.", 
            HttpStatus.NOT_FOUND.value() // 404
        );
        System.err.println("!!! ERROR 404 REST atrapado: " + ex.getRequestURL());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    // Maneja errores genéricos (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "Error interno del servidor. Mensaje: " + ex.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value() // 500
        );
        System.err.println("!!! ERROR 500 GENÉRICO atrapado: " + ex.getMessage());
        ex.printStackTrace(); 
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}