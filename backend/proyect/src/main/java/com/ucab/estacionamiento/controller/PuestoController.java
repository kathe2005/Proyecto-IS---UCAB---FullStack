package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.service.PuestoServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/puestos")
@CrossOrigin(origins = "http://localhost:4200")
public class PuestoController {

    @Autowired
    private PuestoServiceImpl puestoService;

    // Inyectar JsonManagerPuesto para operaciones directas con JSON
    private final JsonManagerPuesto jsonManagerPuesto = new JsonManagerPuesto();

    

    // ========== VISTAS THYMELEAF ==========

    @GetMapping
    public String mostrarTodosLosPuestos(Model model) {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestos();
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Todos los Puestos");
            return "puestos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar puestos: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/disponibles")
    public String mostrarPuestosDisponibles(Model model) {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos Disponibles");
            return "puestos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar puestos disponibles: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/ocupados")
    public String mostrarPuestosOcupados(Model model) {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos Ocupados");
            return "puestos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar puestos ocupados: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/bloqueados")
    public String mostrarPuestosBloqueados(Model model) {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosBloqueados();
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos Bloqueados");
            return "puestos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar puestos bloqueados: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/mantenimiento")
    public String mostrarPuestosMantenimiento(Model model) {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosMantenimiento();
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos en Mantenimiento");
            return "puestos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar puestos en mantenimiento: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        try {
            int total = puestoService.obtenerPuestos().size();
            int disponibles = puestoService.contarPuestosDisponibles();
            int ocupados = puestoService.contarPuestosOcupados();
            int bloqueados = puestoService.contarPuestosBloqueados();
            int mantenimiento = puestoService.contarPuestosMantenimiento();
            
            model.addAttribute("total", total);
            model.addAttribute("disponibles", disponibles);
            model.addAttribute("ocupados", ocupados);
            model.addAttribute("bloqueados", bloqueados);
            model.addAttribute("mantenimiento", mantenimiento);
            model.addAttribute("porcentajeOcupacion", total > 0 ? (ocupados * 100.0) / total : 0);
            
            return "puestos/estadisticas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar estadísticas: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/ocupar")
    public String mostrarFormularioOcupar(Model model) {
        List<Puesto> disponibles = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
        model.addAttribute("puestosDisponibles", disponibles);
        model.addAttribute("ocuparRequest", new Puesto());
        return "puestos/ocupar";
    }

    @PostMapping("/ocupar")
    public String ocuparPuesto(@ModelAttribute Puesto request, Model model) {
        try {
            Puesto resultado = puestoService.ocuparPuesto(
                request.getPuestoIdSolicitud(), 
                request.getUsuarioSolicitud()
            );
            model.addAttribute("resultado", resultado);
            return "redirect:/puestos";
        } catch (Exception e) {
            List<Puesto> disponibles = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
            model.addAttribute("puestosDisponibles", disponibles);
            model.addAttribute("error", e.getMessage());
            return "puestos/ocupar";
        }
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("tiposPuesto", TipoPuesto.values());
        model.addAttribute("estadosPuesto", EstadoPuesto.values());
        return "puestos/buscar";
    }

    @GetMapping("/buscar/estado")
    public String buscarPorEstado(@RequestParam String estado, Model model) {
        try {
            EstadoPuesto estadoPuesto = EstadoPuesto.valueOf(estado.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(estadoPuesto);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos - Estado: " + estadoPuesto.getDescripcion());
            model.addAttribute("criterioBusqueda", "Estado: " + estadoPuesto.getDescripcion());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Estado no válido: " + estado);
        }
        return "puestos/lista";
    }

    @GetMapping("/buscar/tipo")
    public String buscarPorTipo(@RequestParam String tipo, Model model) {
        try {
            TipoPuesto tipoPuesto = TipoPuesto.valueOf(tipo.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorTipo(tipoPuesto);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos - Tipo: " + tipoPuesto.getDescripcion());
            model.addAttribute("criterioBusqueda", "Tipo: " + tipoPuesto.getDescripcion());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Tipo no válido: " + tipo);
        }
        return "puestos/lista";
    }

    @GetMapping("/historial/{id}")
    public String mostrarHistorial(@PathVariable String id, Model model) {
        List<String> historial = puestoService.obtenerHistorial(id);
        Puesto puesto = puestoService.obtenerPuestoPorId(id).orElse(null);
        
        model.addAttribute("historial", historial);
        model.addAttribute("puesto", puesto);
        
        return "puestos/historial";
    }

    // ========== API REST ==========

    @GetMapping("/api")
        @ResponseBody
        public ResponseEntity<?> obtenerTodosLosPuestosApi() {
            try {
                System.out.println("📥 GET /puestos/api recibido");
                List<Puesto> puestos = puestoService.obtenerPuestos();
                System.out.println("✅ Puestos encontrados: " + puestos.size());
                return ResponseEntity.ok(puestos);
            } catch (Exception e) {
                System.err.println("❌ Error en /puestos/api: " + e.getMessage());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Error al obtener puestos: " + e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerPuestoPorIdApi(@PathVariable String id) {
        try {
            return puestoService.obtenerPuestoPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puesto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/api/ocupar")
    @ResponseBody
    public ResponseEntity<?> ocuparPuestoApi(@RequestBody Map<String, String> request) {
        System.out.println("========== OCUPAR PUESTO API ==========");
        System.out.println("📦 Request body recibido: " + request);
        
        try {
            String puestoId = request.get("puestoId");
            String usuario = request.get("usuario");
            
            System.out.println("🚗 Datos extraídos - PuestoID: " + puestoId + ", Usuario: " + usuario);
            
            if (puestoId == null || puestoId.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID del puesto es requerido");
            }
            if (usuario == null || usuario.trim().isEmpty()) {
                throw new IllegalArgumentException("El usuario es requerido");
            }
            
            Puesto resultado = puestoService.ocuparPuesto(puestoId, usuario);
            System.out.println("✅ Puesto ocupado exitosamente: " + resultado.getId());
            
            return ResponseEntity.ok(resultado);
            
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️  Error de validación: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("💥 EXCEPCIÓN en ocuparPuestoApi: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", "Error ocupando puesto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/api/liberar/{id}")
    @ResponseBody
    public ResponseEntity<?> liberarPuestoApi(@PathVariable String id) {
        System.out.println("========== LIBERAR PUESTO API ==========");
        System.out.println("🔍 ID recibido: " + id);
        
        try {
            boolean exito = puestoService.liberarPuesto(id);
            
            if (exito) {
                Map<String, Object> response = new HashMap<>();
                response.put("exito", true);
                response.put("mensaje", "Puesto liberado exitosamente");
                response.put("puestoId", id);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("exito", false);
                error.put("error", "No se pudo liberar el puesto");
                error.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.err.println("💥 EXCEPCIÓN en liberarPuestoApi: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", "Error liberando puesto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/api/bloquear/{id}")
    @ResponseBody
    public ResponseEntity<?> bloquearPuestoApi(@PathVariable String id) {
        System.out.println("========== BLOQUEAR PUESTO API ==========");
        
        try {
            boolean exito = puestoService.bloquearPuesto(id);
            
            if (exito) {
                Map<String, Object> response = new HashMap<>();
                response.put("exito", true);
                response.put("mensaje", "Puesto bloqueado exitosamente");
                response.put("puestoId", id);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("exito", false);
                error.put("error", "No se pudo bloquear el puesto");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.err.println("💥 EXCEPCIÓN en bloquearPuestoApi: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", "Error bloqueando puesto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/api/desbloquear/{id}")
    @ResponseBody
    public ResponseEntity<?> desbloquearPuestoApi(@PathVariable String id) {
        System.out.println("========== DESBLOQUEAR PUESTO API ==========");
        
        try {
            boolean exito = puestoService.desbloquearPuesto(id);
            
            if (exito) {
                Map<String, Object> response = new HashMap<>();
                response.put("exito", true);
                response.put("mensaje", "Puesto desbloqueado exitosamente");
                response.put("puestoId", id);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("exito", false);
                error.put("error", "No se pudo desbloquear el puesto");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.err.println("💥 EXCEPCIÓN en desbloquearPuestoApi: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", "Error desbloqueando puesto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/api/mantenimiento/{id}")
    @ResponseBody
    public ResponseEntity<?> ponerEnMantenimientoApi(@PathVariable String id) {
        System.out.println("========== MANTENIMIENTO PUESTO API ==========");
        
        try {
            boolean exito = puestoService.ponerPuestoEnMantenimiento(id);
            
            if (exito) {
                Map<String, Object> response = new HashMap<>();
                response.put("exito", true);
                response.put("mensaje", "Puesto puesto en mantenimiento exitosamente");
                response.put("puestoId", id);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("exito", false);
                error.put("error", "No se pudo poner en mantenimiento el puesto");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.err.println("💥 EXCEPCIÓN en ponerEnMantenimientoApi: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("error", "Error poniendo en mantenimiento: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticasApi() {
        try {
            int total = puestoService.obtenerPuestos().size();
            int disponibles = puestoService.contarPuestosDisponibles();
            int ocupados = puestoService.contarPuestosOcupados();
            int bloqueados = puestoService.contarPuestosBloqueados();
            int mantenimiento = puestoService.contarPuestosMantenimiento();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("total", total);
            estadisticas.put("disponibles", disponibles);
            estadisticas.put("ocupados", ocupados);
            estadisticas.put("bloqueados", bloqueados);
            estadisticas.put("mantenimiento", mantenimiento);
            estadisticas.put("porcentajeOcupacion", total > 0 ? (ocupados * 100.0) / total : 0);
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> crearPuestoApi(@RequestBody Puesto puesto) {
        try {
            System.out.println("📝 Creando nuevo puesto: " + puesto.getNumero());
            
            // Validar datos requeridos
            if (puesto.getNumero() == null || puesto.getNumero().trim().isEmpty()) {
                throw new IllegalArgumentException("El número de puesto es requerido");
            }
            
            if (puesto.getUbicacion() == null || puesto.getUbicacion().trim().isEmpty()) {
                throw new IllegalArgumentException("La ubicación es requerida");
            }
            
            if (puesto.getTipoPuesto() == null) {
                puesto.setTipoPuesto(TipoPuesto.REGULAR);
            }
            
            if (puesto.getEstadoPuesto() == null) {
                puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            }
            
            // Generar ID automático si no viene
            if (puesto.getId() == null || puesto.getId().trim().isEmpty()) {
                String nuevoId = "P" + (puestoService.obtenerPuestos().size() + 1);
                puesto.setId(nuevoId);
            }
            
            // Crear el puesto usando el servicio
            Puesto puestoCreado = puestoService.crearPuesto(puesto);
            
            System.out.println("✅ Puesto creado exitosamente: " + puestoCreado.getId());
            return ResponseEntity.ok(puestoCreado);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            System.err.println("❌ Error creando puesto: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/estado")
    @ResponseBody
    public ResponseEntity<?> obtenerPuestosPorEstadoApi(@RequestParam String estado) {
        try {
            EstadoPuesto estadoPuesto = EstadoPuesto.valueOf(estado.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(estadoPuesto);
            return ResponseEntity.ok(puestos);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Estado de puesto no válido: " + estado);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al buscar puestos por estado: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/tipo")
    @ResponseBody
    public ResponseEntity<?> obtenerPuestosPorTipoApi(@RequestParam String tipo) {
        try {
            TipoPuesto tipoPuesto = TipoPuesto.valueOf(tipo.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorTipo(tipoPuesto);
            return ResponseEntity.ok(puestos);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Tipo de puesto no válido: " + tipo);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al buscar puestos por tipo: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/json")
    @ResponseBody
    public ResponseEntity<?> mostrarArchivoJSONApi() {
        jsonManagerPuesto.diagnostico();
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Diagnóstico de puestos mostrado en consola");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/debug/info")
    @ResponseBody
    public ResponseEntity<?> getDebugInfoApi() {
        // Usar JsonManagerPuesto para obtener información del archivo
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
        
        Map<String, Object> info = new HashMap<>();
        info.put("puestosEnArchivo", puestos.size());
        info.put("puestosEnMemoria", puestoService.obtenerPuestos().size());
        info.put("disponibles", puestoService.contarPuestosDisponibles());
        info.put("ocupados", puestoService.contarPuestosOcupados());
        info.put("bloqueados", puestoService.contarPuestosBloqueados());
        
        return ResponseEntity.ok(info);
    }

    @PostMapping("/api/test-crear")
    @ResponseBody
    public ResponseEntity<?> testCrearPuestoApi() {
        try {
            System.out.println("🧪 Probando creación de puesto...");
            
            Puesto testPuesto = new Puesto();
            testPuesto.setNumero("TEST-001");
            testPuesto.setTipoPuesto(TipoPuesto.REGULAR);
            testPuesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            testPuesto.setUbicacion("Zona Test");
            
            Puesto creado = puestoService.crearPuesto(testPuesto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Puesto de prueba creado");
            response.put("puesto", Map.of(
                "id", creado.getId(),
                "numero", creado.getNumero()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en test: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}