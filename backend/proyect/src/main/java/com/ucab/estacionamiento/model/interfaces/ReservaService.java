package com.ucab.estacionamiento.model.interfaces;

import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.clases.PuestosDisponiblesResponse;
import com.ucab.estacionamiento.model.clases.Reserva;
import com.ucab.estacionamiento.model.clases.ReservaRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    // Gestión de reservas
    Reserva crearReserva(ReservaRequest request);
    boolean cancelarReserva(String reservaId);
    Reserva confirmarReserva(String reservaId);
    Optional<Reserva> obtenerReservaPorId(String id);
    List<Reserva> obtenerReservasPorCliente(String clienteId);
    List<Reserva> obtenerReservasPorFecha(LocalDate fecha);
    
    // Consulta de disponibilidad
    PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno);
    // Nueva sobrecarga que permite filtrar según cliente (usuario, cédula o email)
    PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno, String clienteId);
    List<Puesto> obtenerPuestosDisponiblesParaFecha(LocalDate fecha, String turno);
    boolean verificarDisponibilidadPuesto(String puestoId, LocalDate fecha, String turno);
    
    // Gestión de estados
    boolean activarReserva(String reservaId);
    boolean completarReserva(String reservaId);
    List<Reserva> obtenerReservasPendientes();
}