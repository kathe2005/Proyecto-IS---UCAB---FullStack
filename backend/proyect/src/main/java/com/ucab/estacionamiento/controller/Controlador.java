package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.model.interfaces.*;
import com.ucab.estacionamiento.model.implement.PagoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class Controlador {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PuestoService puestoService;

    @Autowired
    private ReporteService reporteService;

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
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequest request) {
        try {
            Reserva reserva = reservaService.crearReserva(request);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    @PostMapping("/reservas/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable String id) {
        boolean exito = reservaService.cancelarReserva(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Reserva cancelada exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo cancelar la reserva\"}");
        }
    }

    @PostMapping("/reservas/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable String id) {
        try {
            Reserva reserva = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/reservas/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCliente(@PathVariable String clienteId) {
        List<Reserva> reservas = reservaService.obtenerReservasPorCliente(clienteId);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/reservas/fecha/{fecha}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            List<Reserva> reservas = reservaService.obtenerReservasPorFecha(fechaLocal);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable String id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservas/pendientes")
    public ResponseEntity<List<Reserva>> obtenerReservasPendientes() {
        List<Reserva> reservas = reservaService.obtenerReservasPendientes();
        return ResponseEntity.ok(reservas);
    }

    // ========== CLIENTE ENDPOINTS ==========

    @PostMapping("/clientes")
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente cliente) {
        Cliente creado = clienteService.registrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> lista = clienteService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }

    // ========== PUESTO ENDPOINTS ==========

    // API Endpoints
    @GetMapping("/puestos")
    public ResponseEntity<List<Puesto>> obtenerTodosLosPuestos() {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/puestos/disponibles")
    public ResponseEntity<List<Puesto>> obtenerPuestosDisponibles() {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/puestos/ocupados")
    public ResponseEntity<List<Puesto>> obtenerPuestosOcupados() {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO);
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/puestos/bloqueados")
    public ResponseEntity<List<Puesto>> obtenerPuestosBloqueados() {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.BLOQUEADO);
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/puestos/mantenimiento")
    public ResponseEntity<List<Puesto>> obtenerPuestosMantenimiento() {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO);
        return ResponseEntity.ok(puestos);
    }

    @PostMapping("/puestos/ocupar")
    public ResponseEntity<ResultadoOcupacion> ocuparPuesto(@RequestBody OcuparPuestoRequest request) {
        ResultadoOcupacion resultado = puestoService.ocuparPuesto(request.getPuestoId(), request.getUsuario());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/puestos/asignar-manual")
    public ResponseEntity<ResultadoOcupacion> asignarPuestoManual(@RequestBody OcuparPuestoRequest request) {
        ResultadoOcupacion resultado = puestoService.asignarPuestoManual(request.getPuestoId(), request.getUsuario());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/puestos/liberar/{id}")
    public ResponseEntity<?> liberarPuesto(@PathVariable String id) {
        puestoService.liberarPuesto(id);
        return ResponseEntity.ok().body("{\"mensaje\": \"Puesto liberado exitosamente\"}");
    }

    @PostMapping("/puestos/bloquear/{id}")
    public ResponseEntity<?> bloquearPuesto(@PathVariable String id) {
        puestoService.bloquearPuesto(id);
        return ResponseEntity.ok().body("{\"mensaje\": \"Puesto bloqueado exitosamente\"}");
    }

    @PostMapping("/puestos/desbloquear/{id}")
    public ResponseEntity<?> desbloquearPuesto(@PathVariable String id) {
        puestoService.desbloquearPuesto(id);
        return ResponseEntity.ok().body("{\"mensaje\": \"Puesto desbloqueado exitosamente\"}");
    }

    @PostMapping("/puestos/mantenimiento/{id}")
    public ResponseEntity<?> ponerEnMantenimiento(@PathVariable String id) {
        puestoService.ponerPuestoEnMantenimiento(id);
        return ResponseEntity.ok().body("{\"mensaje\": \"Puesto puesto en mantenimiento exitosamente\"}");
    }

    @GetMapping("/puestos/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasPuestos() {
        int total = puestoService.obtenerPuestos().size();
        int disponibles = puestoService.contarPuestosDisponibles();
        int ocupados = puestoService.contarPuestosOcupados();
        int bloqueados = puestoService.contarPuestosBloqueados();
        int mantenimiento = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO).size();
        
        Map<String, Object> estadisticas = Map.of(
            "total", total,
            "disponibles", disponibles,
            "ocupados", ocupados,
            "bloqueados", bloqueados,
            "mantenimiento", mantenimiento,
            "porcentajeOcupacion", total > 0 ? (ocupados * 100.0) / total : 0
        );
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/puestos/buscar/estado")
    public ResponseEntity<List<Puesto>> buscarPuestosPorEstado(@RequestParam String estado) {
        try {
            EstadoPuesto estadoPuesto = EstadoPuesto.valueOf(estado.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(estadoPuesto);
            return ResponseEntity.ok(puestos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/puestos/buscar/tipo")
    public ResponseEntity<List<Puesto>> buscarPuestosPorTipo(@RequestParam String tipo) {
        try {
            TipoPuesto tipoPuesto = TipoPuesto.valueOf(tipo.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorTipo(tipoPuesto);
            return ResponseEntity.ok(puestos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/puestos/buscar/ubicacion")
    public ResponseEntity<List<Puesto>> buscarPuestosPorUbicacion(@RequestParam String ubicacion) {
        List<Puesto> puestos = puestoService.filtrarPuestosPorUbicacion(ubicacion);
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/puestos/historial/{id}")
    public ResponseEntity<List<String>> obtenerHistorialPuesto(@PathVariable String id) {
        List<String> historial = puestoService.obtenerHistorial(id);
        return ResponseEntity.ok(historial);
    }

    @PostMapping("/puestos/reasignar/{id}")
    public ResponseEntity<?> reasignarPuesto(@PathVariable String id, @RequestParam String nuevaUbicacion) {
        try {
            Puesto puestoActualizado = puestoService.reasignarPuesto(id, nuevaUbicacion);
            return ResponseEntity.ok(puestoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ========== REPORTE ENDPOINTS ==========

    @GetMapping("/reportes/ocupacion")
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

    @GetMapping("/reportes/ocupacion/hoy")
    public ResponseEntity<ReporteOcupacion> getReporteHoy() {
        try {
            ReporteOcupacion reporte = reporteService.generarReporteHoy();
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reportes/ocupacion/diario")
    public ResponseEntity<ReporteOcupacion> getReporteDiario(@RequestParam String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            ReporteOcupacion reporte = reporteService.generarReporteDiario(fechaLocal);
            return new ResponseEntity<>(reporte, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reportes/ocupacion/tendencia")
    public ResponseEntity<List<ReporteOcupacion>> getTendenciaOcupacion() {
        try {
            List<ReporteOcupacion> tendencia = reporteService.generarReporteTendencia();
            return new ResponseEntity<>(tendencia, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reportes/estadisticas")
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

    // ========== PAGO ENDPOINTS ==========

    @PostMapping("/pagos")
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest pagoRequest) {
        try {
            Pago pago = pagoService.registrarPago(pagoRequest);
            return ResponseEntity.ok(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    @GetMapping("/pagos/reservas-pendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerReservasPendientesPago() {
        try {
            List<Map<String, Object>> reservasPendientes = pagoService.obtenerReservasPendientesPago();
            return ResponseEntity.ok(reservasPendientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable String id) {
        return pagoService.obtenerPagoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pagos/cliente/{clienteId}")
    public ResponseEntity<List<Pago>> obtenerPagosPorCliente(@PathVariable String clienteId) {
        List<Pago> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/pagos/calcular-tarifa")
    public ResponseEntity<Map<String, Object>> calcularTarifa(
            @RequestParam String tipoPuesto,
            @RequestParam String turno) {
        try {
            double tarifa = pagoService.calcularTarifa(tipoPuesto, turno);
            Map<String, Object> response = Map.of(
                "tipoPuesto", tipoPuesto,
                "turno", turno,
                "tarifa", tarifa,
                "mensaje", String.format("Tarifa calculada: $%.2f", tarifa)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== VIEW ENDPOINTS (Thymeleaf) ==========

    @GetMapping("/")
    public String index(Model model) {
        int total = puestoService.obtenerPuestos().size();
        int disponibles = puestoService.contarPuestosDisponibles();
        int ocupados = puestoService.contarPuestosOcupados();
        
        model.addAttribute("totalPuestos", total);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("ocupados", ocupados);
        
        return "index";
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping("/vistas/puestos")
    public String mostrarTodosLosPuestos(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Todos los Puestos");
        return "puestos/lista";
    }

    @GetMapping("/vistas/puestos/disponibles")
    public String mostrarPuestosDisponibles(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos Disponibles");
        return "puestos/lista";
    }

    @GetMapping("/vistas/puestos/ocupados")
    public String mostrarPuestosOcupados(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos Ocupados");
        return "puestos/lista";
    }

    @GetMapping("/vistas/puestos/estadisticas")
    public String mostrarEstadisticas(Model model) {
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
    }
}