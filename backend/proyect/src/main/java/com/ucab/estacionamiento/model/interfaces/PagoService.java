package com.ucab.estacionamiento.model.interfaces;

import com.ucab.estacionamiento.model.clases.Pago;
import com.ucab.estacionamiento.model.clases.PagoRequest;

import java.util.List;
import java.util.Optional;

public interface PagoService {
    Pago registrarPago(PagoRequest pagoRequest);
    Optional<Pago> obtenerPagoPorId(String id);
    List<Pago> obtenerPagosPorCliente(String clienteId);
    List<Pago> obtenerTodosLosPagos();
    boolean existePagoParaReserva(String reservaId);
}