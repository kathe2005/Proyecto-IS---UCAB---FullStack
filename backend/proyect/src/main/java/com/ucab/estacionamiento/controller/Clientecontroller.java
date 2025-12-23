package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.service.ClienteServiceImpl;

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
public class Clientecontroller {

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
            clienteService.registrarCliente(cliente);
            model.addAttribute("mensaje", "Cliente registrado exitosamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/registrar";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable String id, Model model) {
        try {
            Cliente cliente = clienteService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
            model.addAttribute("cliente", cliente);
            return "clientes/editar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cliente: " + e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable String id, @ModelAttribute Cliente cliente, Model model) {
        try {
            cliente.setId(java.util.UUID.fromString(id)); // Asegurar que el ID se mantiene
            clienteService.actualizarCliente(cliente);
            model.addAttribute("mensaje", "Cliente actualizado exitosamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar cliente: " + e.getMessage());
            model.addAttribute("cliente", cliente);
            return "clientes/editar";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable String id, Model model) {
        try {
            boolean eliminado = clienteService.eliminarCliente(id);
            if (eliminado) {
                model.addAttribute("mensaje", "Cliente eliminado exitosamente");
            } else {
                model.addAttribute("error", "Cliente no encontrado: " + id);
            }
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("error", "Error al eliminar cliente: " + e.getMessage());
            return "redirect:/clientes";
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
            errorResponse.put("error", "Error interno del servidor al registrar cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> obtenerTodosLosClientesApi() {
        try {
            List<Cliente> lista = clienteService.obtenerTodos(); // CORRECCIÓN: Cambiado de obtenerTodosLosClientesApi() a obtenerTodos()
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener clientes: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorIdApi(@PathVariable String id) {
        try {
            return clienteService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
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

    @GetMapping("/api/cedula/{cedula}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorCedulaApi(@PathVariable String cedula) {
        try {
            return clienteService.obtenerPorCedula(cedula)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/telefono/{telefono}")
    @ResponseBody
    public ResponseEntity<?> obtenerClientePorTelefonoApi(@PathVariable String telefono) {
        try {
            return clienteService.obtenerPorTelefono(telefono)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarClienteApi(@PathVariable String id, @RequestBody Cliente cliente) {
        try {
            // Verificar que el cliente existe
            Cliente clienteExistente = clienteService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
            
            // Mantener el ID original
            cliente.setId(clienteExistente.getId());
            
            // Actualizar datos
            Cliente actualizado = clienteService.actualizarCliente(cliente);
            return ResponseEntity.ok(actualizado);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor al actualizar cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarClienteApi(@PathVariable String id) {
        try {
            boolean eliminado = clienteService.eliminarCliente(id);
            if (eliminado) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Cliente eliminado exitosamente: " + id);
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Cliente no encontrado: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<?> buscarClientesApi(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String telefono) {
        
        try {
            if (usuario != null && !usuario.trim().isEmpty()) {
                return clienteService.obtenerPorUsuario(usuario)
                        .map(cliente -> ResponseEntity.ok(List.of(cliente)))
                        .orElse(ResponseEntity.ok(List.of()));
            } else if (email != null && !email.trim().isEmpty()) {
                return clienteService.obtenerPorEmail(email)
                        .map(cliente -> ResponseEntity.ok(List.of(cliente)))
                        .orElse(ResponseEntity.ok(List.of()));
            } else if (cedula != null && !cedula.trim().isEmpty()) {
                return clienteService.obtenerPorCedula(cedula)
                        .map(cliente -> ResponseEntity.ok(List.of(cliente)))
                        .orElse(ResponseEntity.ok(List.of()));
            } else if (telefono != null && !telefono.trim().isEmpty()) {
                return clienteService.obtenerPorTelefono(telefono)
                        .map(cliente -> ResponseEntity.ok(List.of(cliente)))
                        .orElse(ResponseEntity.ok(List.of()));
            } else {
                // Si no hay parámetros, devolver todos los clientes
                List<Cliente> todosClientes = clienteService.obtenerTodos();
                return ResponseEntity.ok(todosClientes);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en búsqueda: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/existe/usuario/{usuario}")
    @ResponseBody
    public ResponseEntity<?> existeClientePorUsuarioApi(@PathVariable String usuario) {
        try {
            boolean existe = clienteService.existeClientePorUsuario(usuario);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al verificar existencia: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/existe/email/{email}")
    @ResponseBody
    public ResponseEntity<?> existeClientePorEmailApi(@PathVariable String email) {
        try {
            boolean existe = clienteService.existeClientePorEmail(email);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al verificar existencia: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/existe/cedula/{cedula}")
    @ResponseBody
    public ResponseEntity<?> existeClientePorCedulaApi(@PathVariable String cedula) {
        try {
            boolean existe = clienteService.existeClientePorCedula(cedula);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al verificar existencia: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/diagnostico")
    @ResponseBody
    public ResponseEntity<?> diagnosticoApi() {
        try {
            clienteService.diagnostico();
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Diagnóstico completado - ver consola para detalles");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en diagnóstico: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/validar/usuario/{usuario}")
    @ResponseBody
    public ResponseEntity<?> validarUsuarioApi(@PathVariable String usuario) {
        try {
            clienteService.validarSinEspacios(usuario, "usuario");
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario válido");
            response.put("valido", "true");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("valido", "false");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al validar usuario: " + e.getMessage());
            errorResponse.put("valido", "false");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/validar/email/{email}")
    @ResponseBody
    public ResponseEntity<?> validarEmailApi(@PathVariable String email) {
        try {
            clienteService.validarSinEspacios(email, "email");
            String tipoDominio = clienteService.clasificarDominio(email);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Email válido");
            response.put("tipoDominio", tipoDominio);
            response.put("valido", "true");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("valido", "false");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al validar email: " + e.getMessage());
            errorResponse.put("valido", "false");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PatchMapping("/api/modificar")
    @ResponseBody
    public ResponseEntity<?> modificarClienteApi(@RequestBody Cliente clienteModificado) {
        try {
            System.out.println("🔄 Iniciando modificación de cliente...");
            
            // Validar que el cliente exista
            if (clienteModificado.getId() == null) {
                throw new IllegalArgumentException("El ID del cliente es requerido para modificar");
            }
            
            Cliente clienteExistente = clienteService.obtenerPorId(clienteModificado.getId().toString())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteModificado.getId()));
            
            // Mantener datos que no deben cambiar
            clienteModificado.setId(clienteExistente.getId());
            
            // Si no se envía usuario, mantener el existente
            if (clienteModificado.getUsuario() == null || clienteModificado.getUsuario().trim().isEmpty()) {
                clienteModificado.setUsuario(clienteExistente.getUsuario());
            }
            
            // Si no se envía contraseña, mantener la existente
            if (clienteModificado.getContrasena() == null || clienteModificado.getContrasena().trim().isEmpty()) {
                clienteModificado.setContrasena(clienteExistente.getContrasena());
            }
            
            // Llamar al servicio para modificar
            Cliente clienteActualizado = clienteService.actualizarCliente(clienteModificado);
            
            System.out.println("✅ Cliente modificado exitosamente: " + clienteActualizado.getUsuario());
            
            return ResponseEntity.ok(clienteActualizado);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno al modificar cliente: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}