package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.service.PuestoService;
import com.ucab.estacionamiento.service.JsonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
/*import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;*/

@RestController
@RequestMapping("/api/puestos")
public class ApiController {

    @Autowired
    private PuestoService puestoService;

    @Autowired
    private JsonManager jsonManager;

    @GetMapping
        public List<Puesto> obtenerTodosLosPuestos() {
            return puestoService.obtenerPuestos();
        }

    @GetMapping("/{id}")
    public ResponseEntity<Puesto> obtenerPuestoPorId(@PathVariable String id) {
        return puestoService.obtenerPuestoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ocupar")
    public ResponseEntity<ResultadoOcupacion> ocuparPuesto(@RequestBody OcuparPuestoRequest request) {
        ResultadoOcupacion resultado = puestoService.ocuparPuesto(
            request.getPuestoId(), 
            request.getUsuario(), 
            request.getClienteId(), 
            request.getTipoCliente()
        );
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<?> crearPuesto(@RequestBody Puesto nuevoPuesto) {
        try {
            Puesto puestoCreado = puestoService.crearPuesto(nuevoPuesto); 
            return new ResponseEntity<>(puestoCreado, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error interno al crear el puesto", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/liberar/{id}")
    public ResponseEntity<?> liberarPuesto(@PathVariable String id) {
        boolean exito = puestoService.liberarPuesto(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Puesto liberado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al liberar el puesto\"}");
        }
    }

    @PostMapping("/bloquear/{id}")
    public ResponseEntity<?> bloquearPuesto(@PathVariable String id) {
        boolean exito = puestoService.bloquearPuesto(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Puesto bloqueado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al bloquear el puesto\"}");
        }
    }

    @PostMapping("/desbloquear/{id}")
    public ResponseEntity<?> desbloquearPuesto(@PathVariable String id) {
        boolean exito = puestoService.desbloquearPuesto(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Puesto desbloqueado exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al desbloquear el puesto\"}");
        }
    }

    @PostMapping("/mantenimiento/{id}")
    public ResponseEntity<?> ponerEnMantenimiento(@PathVariable String id) {
        boolean exito = puestoService.ponerPuestoEnMantenimiento(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Puesto puesto en mantenimiento\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"Error al poner en mantenimiento\"}");
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        int total = puestoService.obtenerPuestos().size();
        int disponibles = puestoService.contarPuestosDisponibles();
        int ocupados = puestoService.contarPuestosOcupados();
        int bloqueados = puestoService.contarPuestosBloqueados();
        
        String estadisticas = String.format(
            "{\"total\": %d, \"disponibles\": %d, \"ocupados\": %d, \"bloqueados\": %d, \"porcentajeOcupacion\": %.1f}",
            total, disponibles, ocupados, bloqueados, total > 0 ? (ocupados * 100.0) / total : 0
        );
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/json")
    public ResponseEntity<?> mostrarArchivoJSON() {
        jsonManager.mostrarArchivoJSON();
        return ResponseEntity.ok().body("{\"mensaje\": \"Contenido del JSON mostrado en consola\"}");
    }

    @GetMapping("/estado")
    public List<Puesto> obtenerPuestosPorEstado(@RequestParam EstadoPuesto estado) {
        return puestoService.obtenerPuestosPorEstado(estado);
    }

    @GetMapping("/tipo")
    public List<Puesto> obtenerPuestosPorTipo(@RequestParam TipoPuesto tipo) {
        return puestoService.obtenerPuestosPorTipo(tipo);
    }

    @GetMapping("/ubicacion")
    public List<Puesto> filtrarPuestosPorUbicacion(@RequestParam String ubicacion) {
        return puestoService.filtrarPuestosPorUbicacion(ubicacion);
    }

    @GetMapping("/historial/{id}")
    public List<String> obtenerHistorial(@PathVariable String id) {
        return puestoService.obtenerHistorial(id);
    }
}
