package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.interfaces.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // Consultar puestos disponibles por fecha y turno
    @GetMapping("/disponibles")
    public ResponseEntity<PuestosDisponiblesResponse> consultarPuestosDisponibles(
            @RequestParam String fecha,
            @RequestParam String turno) {
        
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            PuestosDisponiblesResponse response = reservaService.consultarPuestosDisponibles(fechaLocal, turno);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear nueva reserva
    @PostMapping
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

    // Cancelar reserva
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable String id) {
        boolean exito = reservaService.cancelarReserva(id);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Reserva cancelada exitosamente\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"error\": \"No se pudo cancelar la reserva\"}");
        }
    }

    // Confirmar reserva
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable String id) {
        try {
            Reserva reserva = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Obtener reservas por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCliente(@PathVariable String clienteId) {
        List<Reserva> reservas = reservaService.obtenerReservasPorCliente(clienteId);
        return ResponseEntity.ok(reservas);
    }

    // Obtener reservas por fecha
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            List<Reserva> reservas = reservaService.obtenerReservasPorFecha(fechaLocal);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable String id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener reservas pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<Reserva>> obtenerReservasPendientes() {
        List<Reserva> reservas = reservaService.obtenerReservasPendientes();
        return ResponseEntity.ok(reservas);
    }
}