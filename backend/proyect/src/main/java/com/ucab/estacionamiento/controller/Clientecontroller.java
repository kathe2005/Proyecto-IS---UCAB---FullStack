package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.service.ClienteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteServiceImpl clienteService;

    // ========== VISTAS THYMELEAF ==========
    
    @GetMapping
    public String mostrarClientes(Model model) {
        try {
            List<Cliente> clientes = clienteService.obtenerTodos();
            model.addAttribute("clientes", clientes);
            return "clientes/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar clientes: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registrar";
    }

    @PostMapping("/registrar")
    public String registrarCliente(@ModelAttribute Cliente cliente, Model model) {
        try {
            Cliente creado = clienteService.registrarCliente(cliente);
            model.addAttribute("mensaje", "Cliente registrado exitosamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/registrar";
        }
    }

    // ========== API REST ==========

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> registrarClienteApi(@RequestBody Cliente cliente) {
        try {
            Cliente creado = clienteService.registrarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor al registrar cliente");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> obtenerTodosLosClientesApi() {
        try {
            List<Cliente> lista = clienteService.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener clientes: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/usuario/{usuario}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorUsuarioApi(@PathVariable String usuario) {
        try {
            return clienteService.obtenerPorUsuario(usuario)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/email/{email}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorEmailApi(@PathVariable String email) {
        try {
            return clienteService.obtenerPorEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Método alternativo para buscar por ID usando los métodos disponibles
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorIdApi(@PathVariable String id) {
        try {
            // Buscar en todos los clientes por ID
            List<Cliente> clientes = clienteService.obtenerTodos();
            Cliente clienteEncontrado = clientes.stream()
                    .filter(c -> c.getId() != null && c.getId().toString().equals(id))
                    .findFirst()
                    .orElse(null);
            
            if (clienteEncontrado != null) {
                return ResponseEntity.ok(clienteEncontrado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Método para buscar por cédula
    @GetMapping("/api/cedula/{cedula}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorCedulaApi(@PathVariable String cedula) {
        try {
            // Buscar en todos los clientes por cédula
            List<Cliente> clientes = clienteService.obtenerTodos();
            Cliente clienteEncontrado = clientes.stream()
                    .filter(c -> c.getCedula() != null && c.getCedula().equalsIgnoreCase(cedula))
                    .findFirst()
                    .orElse(null);
            
            if (clienteEncontrado != null) {
                return ResponseEntity.ok(clienteEncontrado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Método para actualizar cliente
    @PutMapping("/api/{usuario}")
    @ResponseBody
    public ResponseEntity<?> actualizarClienteApi(@PathVariable String usuario, @RequestBody Cliente cliente) {
        try {
            // Verificar que el cliente existe
            Cliente clienteExistente = clienteService.obtenerPorUsuario(usuario)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + usuario));
            
            // Actualizar datos
            clienteExistente.setNombre(cliente.getNombre());
            clienteExistente.setApellido(cliente.getApellido());
            clienteExistente.setEmail(cliente.getEmail());
            clienteExistente.setTelefono(cliente.getTelefono());
            
            Cliente actualizado = clienteService.registrarCliente(clienteExistente);
            return ResponseEntity.ok(actualizado);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor al actualizar cliente");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Método para eliminar cliente
    @DeleteMapping("/api/{usuario}")
    @ResponseBody
    public ResponseEntity<?> eliminarClienteApi(@PathVariable String usuario) {
        try {
            // En una implementación real, aquí llamarías a un método de eliminación del servicio
            // Por ahora, retornamos un mensaje indicando que no está implementado
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Funcionalidad de eliminación no implementada para: " + usuario);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}