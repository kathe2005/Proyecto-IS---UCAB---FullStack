package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente;
import com.ucab.estacionamiento.service.ClienteService;
import com.ucab.estacionamiento.exepciones.RegistroClienteException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Endpoint para registrar cliente
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCliente(@RequestBody Cliente nuevoCliente) {
        try {
            Cliente clienteRegistrado = clienteService.registrarCliente(nuevoCliente);
            return new ResponseEntity<>(clienteRegistrado, HttpStatus.CREATED);
        } catch (RegistroClienteException e) {
            // Para excepciones específicas de negocio, devolvemos el mensaje estructurado
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("status", e.getStatus());
            errorResponse.put("error", HttpStatus.valueOf(e.getStatus()).getReasonPhrase());
            return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getStatus()));
        } catch (Exception e) {
            // Para otras excepciones inesperadas
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("error", "Internal Server Error");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para consultar todos los clientes
    @GetMapping("/consultar")
    public ResponseEntity<List<Cliente>> consultarClientes() {
        try {
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Nuevo endpoint para buscar cliente por cédula
    @GetMapping("/buscar-por-cedula")
    public ResponseEntity<?> buscarClientePorCedula(@RequestParam String cedula) {
        try {
            List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
            List<Cliente> clientesFiltrados = clientes.stream()
                    .filter(cliente -> cliente.getCedula().equals(cedula))
                    .toList();
            
            if (clientesFiltrados.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "No se encontró cliente con la cédula: " + cedula);
                response.put("encontrado", false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(clientesFiltrados, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error al buscar cliente: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}