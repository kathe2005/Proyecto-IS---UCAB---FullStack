package com.ucab.estacionamiento.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.ucab.estacionamiento.model.interfaces.ReporteOcupacion;
import com.ucab.estacionamiento.model.interfaces.ReporteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<ReporteOcupacion> getReporteOcupacion(
            @RequestParam String fecha,
            @RequestParam String turno) {
        
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            ReporteOcupacion reporte = reporteService.generarReporteOcupacion(fechaLocal, turno);
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ocupacion/hoy")
    public ResponseEntity<ReporteOcupacion> getReporteHoy() {
        try {
            ReporteOcupacion reporte = reporteService.generarReporteHoy();
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ocupacion/diario")
    public ResponseEntity<ReporteOcupacion> getReporteDiario(@RequestParam String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            ReporteOcupacion reporte = reporteService.generarReporteDiario(fechaLocal);
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ocupacion/tendencia")
    public ResponseEntity<List<ReporteOcupacion>> getTendenciaOcupacion() {
        try {
            List<ReporteOcupacion> tendencia = reporteService.generarReporteTendencia();
            return new ResponseEntity<>(tendencia, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticasRapidas() {
        try {
            ReporteOcupacion reporteHoy = reporteService.generarReporteHoy();
            
            Map<String, Object> estadisticas = Map.of(
                "totalPuestos", reporteHoy.getTotalPuestos(),
                "puestosOcupados", reporteHoy.getPuestosOcupados(),
                "puestosDisponibles", reporteHoy.getPuestosDisponibles(),
                "porcentajeOcupacion", reporteHoy.getPorcentajeOcupacion(),
                "ocupacionPorTipo", reporteHoy.getOcupacionPorTipo(),
                "fecha", LocalDate.now().toString()
            );
            
            return new ResponseEntity<>(estadisticas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}