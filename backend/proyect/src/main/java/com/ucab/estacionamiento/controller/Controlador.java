/*
package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class Controlador {

    @Autowired
    private ReservaServiceImpl reservaService;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private PuestoServiceImpl puestoService;

    @Autowired
    private ReporteServiceImpl reporteService;

    @Autowired
    private PagoServiceImpl pagoService;

    // ========== RESERVA ENDPOINTS ==========

    @GetMapping("/reservas/disponibles")
    public ResponseEntity<PuestosDisponiblesResponse> consultarPuestosDisponibles(
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
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequest request) {
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

    @PostMapping("/reservas/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable String id) {
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

    @PostMapping("/reservas/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable String id) {
        try {
            Reserva reserva = reservaService.confirmarReserva(id);
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

    @GetMapping("/reservas/cliente/{clienteId}")
    public ResponseEntity<?> obtenerReservasPorCliente(@PathVariable String clienteId) {
        try {
            List<Reserva> reservas = reservaService.obtenerReservasPorCliente(clienteId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas del cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/reservas/fecha/{fecha}")
    public ResponseEntity<?> obtenerReservasPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            List<Reserva> reservas = reservaService.obtenerReservasPorFecha(fechaLocal);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas por fecha: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> obtenerReservaPorId(@PathVariable String id) {
        try {
            return reservaService.obtenerReservaPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reserva: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/reservas/pendientes")
    public ResponseEntity<?> obtenerReservasPendientes() {
        try {
            List<Reserva> reservas = reservaService.obtenerReservasPendientes();
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas pendientes: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ========== CLIENTE ENDPOINTS ==========

    @PostMapping("/clientes")
    public ResponseEntity<?> registrarCliente(@RequestBody Cliente cliente) {
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

    @GetMapping("/clientes")
    public ResponseEntity<?> obtenerTodosLosClientes() {
        try {
            List<Cliente> lista = clienteService.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener clientes: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/clientes/usuario/{usuario}")
    public ResponseEntity<?> obtenerClientePorUsuario(@PathVariable String usuario) {
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

    @GetMapping("/clientes/email/{email}")
    public ResponseEntity<?> obtenerClientePorEmail(@PathVariable String email) {
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

    // ========== PUESTO ENDPOINTS ==========

    @GetMapping("/puestos")
    public ResponseEntity<?> obtenerTodosLosPuestos() {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestos();
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puestos: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/disponibles")
    public ResponseEntity<?> obtenerPuestosDisponibles() {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puestos disponibles: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/ocupados")
    public ResponseEntity<?> obtenerPuestosOcupados() {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO);
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puestos ocupados: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/bloqueados")
    public ResponseEntity<?> obtenerPuestosBloqueados() {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.BLOQUEADO);
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puestos bloqueados: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/mantenimiento")
    public ResponseEntity<?> obtenerPuestosMantenimiento() {
        try {
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO);
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener puestos en mantenimiento: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/puestos/ocupar")
    public ResponseEntity<?> ocuparPuesto(@RequestBody OcuparPuestoRequest request) {
        try {
            ResultadoOcupacion resultado = puestoService.ocuparPuesto(
                request.getPuestoId(), 
                request.getUsuario(), 
                request.getClienteId(), 
                request.getTipoCliente()
            );
            return ResponseEntity.ok(resultado);
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

    @PostMapping("/puestos/asignar-manual")
    public ResponseEntity<?> asignarPuestoManual(@RequestBody OcuparPuestoRequest request) {
        try {
            ResultadoOcupacion resultado = puestoService.asignarPuestoManual(request.getPuestoId(), request.getUsuario());
            return ResponseEntity.ok(resultado);
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

    @PostMapping("/puestos/liberar/{id}")
    public ResponseEntity<?> liberarPuesto(@PathVariable String id) {
        try {
            boolean exito = puestoService.liberarPuesto(id);
            if (exito) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Puesto liberado exitosamente");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo liberar el puesto");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al liberar puesto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/puestos/bloquear/{id}")
    public ResponseEntity<?> bloquearPuesto(@PathVariable String id) {
        try {
            boolean exito = puestoService.bloquearPuesto(id);
            if (exito) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Puesto bloqueado exitosamente");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo bloquear el puesto");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al bloquear puesto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/puestos/desbloquear/{id}")
    public ResponseEntity<?> desbloquearPuesto(@PathVariable String id) {
        try {
            boolean exito = puestoService.desbloquearPuesto(id);
            if (exito) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Puesto desbloqueado exitosamente");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo desbloquear el puesto");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al desbloquear puesto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/puestos/mantenimiento/{id}")
    public ResponseEntity<?> ponerEnMantenimiento(@PathVariable String id) {
        try {
            boolean exito = puestoService.ponerPuestoEnMantenimiento(id);
            if (exito) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("mensaje", "Puesto puesto en mantenimiento exitosamente");
                return ResponseEntity.ok(successResponse);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No se pudo poner en mantenimiento el puesto");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al poner en mantenimiento: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasPuestos() {
        try {
            int total = puestoService.obtenerPuestos().size();
            int disponibles = puestoService.contarPuestosDisponibles();
            int ocupados = puestoService.contarPuestosOcupados();
            int bloqueados = puestoService.contarPuestosBloqueados();
            int mantenimiento = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO).size();
            
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

    @GetMapping("/puestos/buscar/estado")
    public ResponseEntity<?> buscarPuestosPorEstado(@RequestParam String estado) {
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

    @GetMapping("/puestos/buscar/tipo")
    public ResponseEntity<?> buscarPuestosPorTipo(@RequestParam String tipo) {
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

    @GetMapping("/puestos/buscar/ubicacion")
    public ResponseEntity<?> buscarPuestosPorUbicacion(@RequestParam String ubicacion) {
        try {
            List<Puesto> puestos = puestoService.filtrarPuestosPorUbicacion(ubicacion);
            return ResponseEntity.ok(puestos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al buscar puestos por ubicación: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/puestos/historial/{id}")
    public ResponseEntity<?> obtenerHistorialPuesto(@PathVariable String id) {
        try {
            List<String> historial = puestoService.obtenerHistorial(id);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener historial: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/puestos/reasignar/{id}")
    public ResponseEntity<?> reasignarPuesto(@PathVariable String id, @RequestParam String nuevaUbicacion) {
        try {
            Puesto puestoActualizado = puestoService.reasignarPuesto(id, nuevaUbicacion);
            return ResponseEntity.ok(puestoActualizado);
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

    // ========== REPORTE ENDPOINTS ==========

    @GetMapping("/reportes/ocupacion")
    public ResponseEntity<?> getReporteOcupacion(
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

    @GetMapping("/reportes/ocupacion/hoy")
    public ResponseEntity<?> getReporteHoy() {
        try {
            ReporteOcupacionImpl reporte = reporteService.generarReporteHoy();
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar reporte de hoy: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/reportes/ocupacion/diario")
    public ResponseEntity<?> getReporteDiario(@RequestParam String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            ReporteOcupacionImpl reporte = reporteService.generarReporteDiario(fechaLocal);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar reporte diario: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/reportes/ocupacion/tendencia")
    public ResponseEntity<?> getTendenciaOcupacion() {
        try {
            List<ReporteOcupacionImpl> tendencia = reporteService.generarReporteTendencia();
            return ResponseEntity.ok(tendencia);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al generar tendencia: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/reportes/estadisticas")
    public ResponseEntity<?> getEstadisticasRapidas() {
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

    // ========== PAGO ENDPOINTS ==========

    @PostMapping("/pagos")
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest pagoRequest) {
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

    @GetMapping("/pagos/reservas-pendientes")
    public ResponseEntity<?> obtenerReservasPendientesPago() {
        try {
            List<Map<String, Object>> reservasPendientes = pagoService.obtenerReservasPendientesPago();
            return ResponseEntity.ok(reservasPendientes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener reservas pendientes de pago: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<?> obtenerPagoPorId(@PathVariable String id) {
        try {
            return pagoService.obtenerPagoPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/pagos/cliente/{clienteId}")
    public ResponseEntity<?> obtenerPagosPorCliente(@PathVariable String clienteId) {
        try {
            List<Pago> pagos = pagoService.obtenerPagosPorCliente(clienteId);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener pagos del cliente: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/pagos")
    public ResponseEntity<?> obtenerTodosLosPagos() {
        try {
            List<Pago> pagos = pagoService.obtenerTodosLosPagos();
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener pagos: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/pagos/calcular-tarifa")
    public ResponseEntity<?> calcularTarifa(
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

    // ========== VIEW ENDPOINTS (Thymeleaf) ==========

    @GetMapping("/")
    public String index(Model model) {
        try {
            int total = puestoService.obtenerPuestos().size();
            int disponibles = puestoService.contarPuestosDisponibles();
            int ocupados = puestoService.contarPuestosOcupados();
            
            model.addAttribute("totalPuestos", total);
            model.addAttribute("disponibles", disponibles);
            model.addAttribute("ocupados", ocupados);
            
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar estadísticas: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/vistas/puestos")
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

    @GetMapping("/vistas/puestos/disponibles")
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

    @GetMapping("/vistas/puestos/ocupados")
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

    @GetMapping("/vistas/puestos/estadisticas")
    public String mostrarEstadisticas(Model model) {
        try {
            int total = puestoService.obtenerPuestos().size();
            int disponibles = puestoService.contarPuestosDisponibles();
            int ocupados = puestoService.contarPuestosOcupados();
            int bloqueados = puestoService.contarPuestosBloqueados();
            int mantenimiento = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO).size();
            
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
}
*/