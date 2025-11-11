package com.ucab.estacionamiento.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class CustomErrorController implements ErrorController{

    @RequestMapping
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        
        // Obtenemos el código de estado HTTP (ej. 400)
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : HttpStatus.INTERNAL_SERVER_ERROR.value();
        
        // Si el estado es 400 o 500 y no tiene mensaje, creamos uno genérico
        String mensaje = "Error de validación o del servidor (Código: " + statusCode + ").";

        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", mensaje);
        body.put("codigo_error", statusCode);

        // Devolvemos el JSON con el estado correcto
        return new ResponseEntity<>(body, HttpStatus.valueOf(statusCode));
    }
    
}
