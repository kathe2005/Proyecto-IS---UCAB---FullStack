package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.model.interfaces.PuestoService;
import com.ucab.estacionamiento.service.JsonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
public class ApiController {

    @Autowired
    private PuestoService puestoService;

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
        JsonManager.mostrarArchivoJSON();
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

    // En ApiController.java - AGREGAR ESTE MÉTODO
    @PostMapping
    public ResponseEntity<?> crearPuesto(@RequestBody Puesto puesto) {
        try {
            Puesto puestoCreado = puestoService.crearPuesto(puesto);
            return ResponseEntity.ok(puestoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    @PostMapping("/test-crear")
    public ResponseEntity<?> testCrearPuesto() {
        try {
            System.out.println("🧪 Probando creación de puesto...");
            
            Puesto testPuesto = new Puesto();
            testPuesto.setNumero("TEST-001");
            testPuesto.setTipoPuesto(TipoPuesto.REGULAR);
            testPuesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            testPuesto.setUbicacion("Zona Test");
            
            Puesto creado = puestoService.crearPuesto(testPuesto);
            
            return ResponseEntity.ok().body("{\"mensaje\": \"Puesto de prueba creado\", \"puesto\": " + 
                "{\"id\": \"" + creado.getId() + "\", \"numero\": \"" + creado.getNumero() + "\"}}");
            
        } catch (Exception e) {
            System.err.println("❌ Error en test: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/debug/info")
    public ResponseEntity<?> getDebugInfo() {
        String filePath = JsonManager.getFilePath();
        File file = new File(filePath);
        
        String info = String.format(
            "{\"rutaArchivo\": \"%s\", \"existe\": %b, \"tamaño\": %d, \"puestosEnMemoria\": %d}",
            filePath, file.exists(), file.length(), puestoService.obtenerPuestos().size()
        );
        
        return ResponseEntity.ok(info);
    }
}

