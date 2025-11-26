package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservas")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaController {

    @Autowired
    private ReservaServiceImpl reservaService;

    @Autowired
    private PagoServiceImpl pagoService;

    @Autowired
    private ReporteServiceImpl reporteService;

    // ========== VISTAS THYMELEAF ==========

    @GetMapping
    public String mostrarReservas(Model model) {
        try {
            List<Reserva> reservas = reservaService.obtenerReservasPendientes();
            model.addAttribute("reservas", reservas);
            return "reservas/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar reservas: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crear")
    public String mostrarFormularioReserva(Model model) {
        model.addAttribute("reservaRequest", new ReservaRequest());
        return "reservas/crear";
    }

    @GetMapping("/pagos")
    public String mostrarPagos(Model model) {
        try {
            List<Pago> pagos = pagoService.obtenerTodosLosPagos();
            model.addAttribute("pagos", pagos);
            return "pagos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pagos: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        try {
            ReporteOcupacionImpl reporteHoy = reporteService.generarReporteHoy();
            model.addAttribute("reporte", reporteHoy);
            return "reportes/ocupacion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar reportes: " + e.getMessage());
            return "error";
        }
    }

    // ========== API REST - RESERVAS ==========

    @GetMapping("/api/disponibles")
    @ResponseBody
    public ResponseEntity<?> consultarPuestosDisponiblesApi(
            @RequestParam String fecha,
            @RequestParam String turno,
            @RequestParam(required = false) String clienteId) {
        
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            PuestosDisponiblesResponse response;
            if (clienteId != null && !clienteId.trim().isEmpty()) {
                response = reservaService.consultarPuestosDisponibles(fechaLocal, turno, clienteId);
            } else {
                response = reservaService.consultarPuestosDisponibles(fechaLocal, turno);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al consultar puestos disponibles: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> crearReservaApi(@RequestBody ReservaRequest request) {
        try {
            Reserva reserva = reservaService.crearReserva(request);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/api/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<?> cancelarReservaApi(@PathVariable String id) {
        try {
            boolean exito = reservaService.cancelarReserva(id);
            if (exito) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Reserva cancelada exitosamente");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo cancelar la reserva");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al cancelar reserva: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<?> obtenerReservasPorClienteApi(@PathVariable String clienteId) {
        try {
            List<Reserva> reservas = reservaService.obtenerReservasPorCliente(clienteId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas del cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ========== API REST - PAGOS ==========

    @PostMapping("/api/pagos")
    @ResponseBody
    public ResponseEntity<?> registrarPagoApi(@RequestBody PagoRequest pagoRequest) {
        try {
            Pago pago = pagoService.registrarPago(pagoRequest);
            return ResponseEntity.ok(pago);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/pagos/reservas-pendientes")
    @ResponseBody
    public ResponseEntity<?> obtenerReservasPendientesPagoApi() {
        try {
            List<Map<String, Object>> reservasPendientes = pagoService.obtenerReservasPendientesPago();
            return ResponseEntity.ok(reservasPendientes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas pendientes de pago: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/pagos/calcular-tarifa")
    @ResponseBody
    public ResponseEntity<?> calcularTarifaApi(
            @RequestParam String tipoPuesto,
            @RequestParam String turno) {
        try {
            double tarifa = pagoService.calcularTarifa(tipoPuesto, turno);
            Map<String, Object> response = new HashMap<>();
            response.put("tipoPuesto", tipoPuesto);
            response.put("turno", turno);
            response.put("tarifa", tarifa);
            response.put("mensaje", String.format("Tarifa calculada: $%.2f", tarifa));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al calcular tarifa: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ========== API REST - REPORTES ==========

    @GetMapping("/api/reportes/ocupacion")
    @ResponseBody
    public ResponseEntity<?> getReporteOcupacionApi(
            @RequestParam String fecha,
            @RequestParam String turno) {
        
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            ReporteOcupacionImpl reporte = reporteService.generarReporteOcupacion(fechaLocal, turno);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar reporte: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/reportes/ocupacion/hoy")
    @ResponseBody
    public ResponseEntity<?> getReporteHoyApi() {
        try {
            ReporteOcupacionImpl reporte = reporteService.generarReporteHoy();
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar reporte de hoy: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/reportes/estadisticas")
    @ResponseBody
    public ResponseEntity<?> getEstadisticasRapidasApi() {
        try {
            ReporteOcupacionImpl reporteHoy = reporteService.generarReporteHoy();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalPuestos", reporteHoy.getTotalPuestos());
            estadisticas.put("puestosOcupados", reporteHoy.getPuestosOcupados());
            estadisticas.put("puestosDisponibles", reporteHoy.getPuestosDisponibles());
            estadisticas.put("porcentajeOcupacion", reporteHoy.getPorcentajeOcupacion());
            estadisticas.put("ocupacionPorTipo", reporteHoy.getOcupacionPorTipo());
            estadisticas.put("fecha", LocalDate.now().toString());
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}