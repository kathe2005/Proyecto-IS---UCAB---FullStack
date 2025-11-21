package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.enums.*;
import com.ucab.estacionamiento.model.interfaces.ReservaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {
    
    private List<Reserva> reservas;
    private final JsonManagerReserva jsonManagerReserva;

    public ReservaServiceImpl() {
        this.jsonManagerReserva = new JsonManagerReserva();
        this.reservas = jsonManagerReserva.cargarReservas();
        System.out.println("✅ ReservaServiceImpl inicializado con " + reservas.size() + " reservas");
    }

    private void guardarCambios() {
        jsonManagerReserva.guardarReservas(reservas);
    }

    @Override
    public Reserva crearReserva(ReservaRequest request) {
        System.out.println("📅 Creando reserva para fecha: " + request.getFecha() + ", turno: " + request.getTurno());
        
        // Validar que el puesto existe
        Optional<Puesto> puestoOpt = JsonManager.cargarPuestos().stream()
                .filter(p -> p.getId().equals(request.getPuestoId()))
                .findFirst();
                
        if (puestoOpt.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + request.getPuestoId());
        }

        // Validar disponibilidad del puesto
        if (!verificarDisponibilidadPuesto(request.getPuestoId(), request.getFecha(), request.getTurno())) {
            throw new IllegalArgumentException("El puesto no está disponible para la fecha y turno seleccionados");
        }

        // Crear nueva reserva
        String nuevoId = "R" + (reservas.size() + 1);
        Reserva nuevaReserva = new Reserva(nuevoId, request.getPuestoId(), 
                                         request.getClienteId(), request.getUsuario(),
                                         request.getFecha(), request.getTurno());
        
        reservas.add(nuevaReserva);
        guardarCambios();
        
        System.out.println("✅ Reserva creada exitosamente: " + nuevoId);
        return nuevaReserva;
    }

    @Override
    public PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno);
        
        List<Puesto> todosLosPuestos = JsonManager.cargarPuestos();
        List<Puesto> puestosDisponibles = obtenerPuestosDisponiblesParaFecha(fecha, turno);
        
        PuestosDisponiblesResponse response = new PuestosDisponiblesResponse(
            fecha, turno, todosLosPuestos.size(), puestosDisponibles.size(), puestosDisponibles
        );
        
        System.out.println("✅ Consulta completada: " + puestosDisponibles.size() + " puestos disponibles");
        return response;
    }

    @Override
    public List<Puesto> obtenerPuestosDisponiblesParaFecha(LocalDate fecha, String turno) {
        List<Puesto> todosLosPuestos = JsonManager.cargarPuestos();
        
        return todosLosPuestos.stream()
                .filter(puesto -> puesto.getEstadoPuesto() == EstadoPuesto.DISPONIBLE)
                .filter(puesto -> verificarDisponibilidadPuesto(puesto.getId(), fecha, turno))
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidadPuesto(String puestoId, LocalDate fecha, String turno) {
        // Verificar si hay reservas activas para este puesto, fecha y turno
        boolean tieneReserva = reservas.stream()
                .anyMatch(reserva -> 
                    reserva.getPuestoId().equals(puestoId) &&
                    reserva.getFecha().equals(fecha) &&
                    reserva.getTurno().equalsIgnoreCase(turno) &&
                    (reserva.getEstado() == EstadoReserva.CONFIRMADA || 
                     reserva.getEstado() == EstadoReserva.PENDIENTE ||
                     reserva.getEstado() == EstadoReserva.ACTIVA)
                );
        
        return !tieneReserva;
    }

    @Override
    public boolean cancelarReserva(String reservaId) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.CANCELADA);
            guardarCambios();
            System.out.println("✅ Reserva cancelada: " + reservaId);
            return true;
        }
        return false;
    }

    @Override
    public Reserva confirmarReserva(String reservaId) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            guardarCambios();
            System.out.println("✅ Reserva confirmada: " + reservaId);
            return reserva;
        }
        throw new IllegalArgumentException("Reserva no encontrada: " + reservaId);
    }

    @Override
    public Optional<Reserva> obtenerReservaPorId(String id) {
        return reservas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Reserva> obtenerReservasPorCliente(String clienteId) {
        return reservas.stream()
                .filter(r -> r.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        return reservas.stream()
                .filter(r -> r.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    @Override
    public boolean activarReserva(String reservaId) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.ACTIVA);
            guardarCambios();
            return true;
        }
        return false;
    }

    @Override
    public boolean completarReserva(String reservaId) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.COMPLETADA);
            guardarCambios();
            return true;
        }
        return false;
    }

    @Override
    public List<Reserva> obtenerReservasPendientes() {
        return reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.PENDIENTE)
                .collect(Collectors.toList());
    }
}