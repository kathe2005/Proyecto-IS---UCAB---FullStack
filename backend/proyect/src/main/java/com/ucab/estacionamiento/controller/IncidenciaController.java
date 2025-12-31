package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Incidencia;
import com.ucab.estacionamiento.model.enums.EstadoIncidencia;
import com.ucab.estacionamiento.service.IncidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/incidencias")
@CrossOrigin(origins = "http://localhost:4200")
public class IncidenciaController {

    @Autowired
    private IncidenciaService incidenciaService;
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Incidencia>> listarIncidenciasApi() {
        List<Incidencia> lista = incidenciaService.obtenerTodasLasIncidencias();
        return ResponseEntity.ok(lista);
    }
    @PostMapping("/api/reportar")
    @ResponseBody
    public ResponseEntity<?> reportarIncidenciaApi(@RequestBody Incidencia incidencia) {
        try {
            Incidencia nueva = incidenciaService.reportarIncidencia(incidencia);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Incidencia reportada con éxito",
                "incidencia", nueva
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al reportar: " + e.getMessage()));
        }
    }
    @PutMapping("/api/estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoApi(@PathVariable int id, @RequestParam EstadoIncidencia nuevoEstado) {
        try {
            Incidencia actualizada = incidenciaService.cambiarEstadoIncidencia(id, nuevoEstado);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Estado actualizado correctamente",
                "incidencia", actualizada
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarIncidenciaApi(@PathVariable int id) {
        boolean eliminado = incidenciaService.eliminarIncidencia(id);
        
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Incidencia eliminada correctamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "No se encontró la incidencia con ID: " + id));
        }
    }
}