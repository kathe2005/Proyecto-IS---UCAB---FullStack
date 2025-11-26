/* 

package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Pago;
import com.ucab.estacionamiento.model.clases.PagoRequest;
import com.ucab.estacionamiento.model.implement.PagoServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:4200")
public class PagoController {

    @Autowired
    private PagoServiceImpl pagoService;

    // Registrar nuevo pago
    @PostMapping
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

    // Obtener reservas pendientes de pago
    @GetMapping("/reservas-pendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerReservasPendientesPago() {
        try {
            List<Map<String, Object>> reservasPendientes = pagoService.obtenerReservasPendientesPago();
            return ResponseEntity.ok(reservasPendientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable String id) {
        return pagoService.obtenerPagoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener pagos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pago>> obtenerPagosPorCliente(@PathVariable String clienteId) {
        List<Pago> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    // Calcular tarifa
    @GetMapping("/calcular-tarifa")
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
}
    */