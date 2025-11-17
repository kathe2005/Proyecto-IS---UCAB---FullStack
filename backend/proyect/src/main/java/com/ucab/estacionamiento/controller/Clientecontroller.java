package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente;
import com.ucab.estacionamiento.service.ClienteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class Clientecontroller {

    private final ClienteService clienteService;

    public Clientecontroller(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Endpoint para registrar cliente
    @PostMapping("/registrar")
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente nuevoCliente) {
        Cliente clienteRegistrado = clienteService.registrarCliente(nuevoCliente);
        return new ResponseEntity<>(clienteRegistrado, HttpStatus.CREATED);
    }

    // Endpoint para consultar todos los clientes
    @GetMapping("/consultar")
    public ResponseEntity<List<Cliente>> consultarClientes() {
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    // NUEVO: Endpoint para obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable UUID id) {
        Cliente cliente = clienteService.obtenerClientePorId(id);
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    // NUEVO: Endpoint para modificar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> modificarCliente(@PathVariable UUID id, @RequestBody Cliente clienteActualizado) {
        Cliente clienteModificado = clienteService.modificarCliente(id, clienteActualizado);
        return new ResponseEntity<>(clienteModificado, HttpStatus.OK);
    }
}