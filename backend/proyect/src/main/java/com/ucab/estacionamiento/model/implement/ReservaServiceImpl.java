package com.ucab.estacionamiento.model.implement;

import com.ucab.estacionamiento.model.archivosJson.UnifiedJsonRepository;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.clases.PuestosDisponiblesResponse;
import com.ucab.estacionamiento.model.clases.Reserva;
import com.ucab.estacionamiento.model.clases.ReservaRequest;
import com.ucab.estacionamiento.model.enums.*;
import com.ucab.estacionamiento.model.interfaces.ReservaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ReservaServiceImpl implements ReservaService {
    
    private final UnifiedJsonRepository repository;

    @Autowired
    public ReservaServiceImpl(UnifiedJsonRepository repository) {
        this.repository = repository;
        System.out.println("✅ ReservaServiceImpl inicializado con UnifiedJsonRepository");
        System.out.println("📅 Reservas cargadas: " + repository.obtenerTodasLasReservas().size());
    }

    @Override
    public Reserva crearReserva(ReservaRequest request) {
        System.out.println("📅 Creando reserva para fecha: " + request.getFecha() + ", turno: " + request.getTurno());
        
        // Validar que el puesto existe
        Optional<Puesto> puestoOpt = repository.obtenerPuestoPorId(request.getPuestoId());
                
        if (puestoOpt.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + request.getPuestoId());
        }

        // Validar disponibilidad del puesto
        if (!verificarDisponibilidadPuesto(request.getPuestoId(), request.getFecha(), request.getTurno())) {
            throw new IllegalArgumentException("El puesto no está disponible para la fecha y turno seleccionados");
        }

        // Crear nueva reserva
        String nuevoId = "R" + (repository.obtenerTodasLasReservas().size() + 1);
        Reserva nuevaReserva = new Reserva(nuevoId, request.getPuestoId(), 
                                         request.getClienteId(), request.getUsuario(),
                                         request.getFecha(), request.getTurno());
        
        Reserva reservaGuardada = repository.guardarReserva(nuevaReserva);
        
        System.out.println("✅ Reserva creada exitosamente: " + nuevoId);
        return reservaGuardada;
    }

    @Override
    public PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno);
        return consultarPuestosDisponibles(fecha, turno, null);
    }

    @Override
    public PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno, String clienteId) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno + ", cliente: " + clienteId);

        List<Puesto> todosLosPuestos = repository.obtenerTodosLosPuestos();
        List<Puesto> puestosDisponibles = obtenerPuestosDisponiblesParaFecha(fecha, turno);

        // Si se proporcionó clienteId (puede ser usuario, cédula o email), filtrar por tipo de cliente
        if (clienteId != null && !clienteId.trim().isEmpty()) {
            String tipoCliente = resolveTipoCliente(clienteId);
            if (tipoCliente != null) {
                final String tipo = tipoCliente;
                puestosDisponibles = puestosDisponibles.stream()
                        .filter(p -> validarTipoClientePuesto(tipo, p.getTipoPuesto()))
                        .collect(Collectors.toList());
            }
        }

        PuestosDisponiblesResponse response = new PuestosDisponiblesResponse(
                fecha, turno, todosLosPuestos.size(), puestosDisponibles.size(), puestosDisponibles
        );

        System.out.println("✅ Consulta completada: " + puestosDisponibles.size() + " puestos disponibles");
        return response;
    }

    // Intentar resolver el tipo de cliente consultando el repositorio de clientes
    private String resolveTipoCliente(String clienteId) {
        try {
            // Buscar por usuario
            var opt = repository.findByUsuario(clienteId);
            if (opt.isPresent()) return opt.get().getTipoPersona();

            // Buscar por cedula
            opt = repository.findByCedula(clienteId);
            if (opt.isPresent()) return opt.get().getTipoPersona();

            // Buscar por email
            opt = repository.findByEmail(clienteId);
            if (opt.isPresent()) return opt.get().getTipoPersona();

        } catch (Exception e) {
            System.err.println("❌ Error resolviendo tipo de cliente: " + e.getMessage());
        }
        return null;
    }

    // Validación de compatibilidad similar a la del PuestoServiceImpl
    private boolean validarTipoClientePuesto(String tipoCliente, TipoPuesto tipoPuesto) {
        if (tipoCliente == null) return true;
        if ("UCAB".equalsIgnoreCase(tipoCliente)) {
            return tipoPuesto == TipoPuesto.REGULAR ||
                    tipoPuesto == TipoPuesto.DOCENTE ||
                    tipoPuesto == TipoPuesto.DISCAPACITADO;
        } else if ("VISITANTE".equalsIgnoreCase(tipoCliente)) {
            return tipoPuesto == TipoPuesto.REGULAR ||
                    tipoPuesto == TipoPuesto.VISITANTE;
        }
        return true;
    }

    @Override
    public List<Puesto> obtenerPuestosDisponiblesParaFecha(LocalDate fecha, String turno) {
        List<Puesto> todosLosPuestos = repository.obtenerTodosLosPuestos();
        
        return todosLosPuestos.stream()
                .filter(puesto -> puesto.getEstadoPuesto() == EstadoPuesto.DISPONIBLE)
                .filter(puesto -> verificarDisponibilidadPuesto(puesto.getId(), fecha, turno))
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidadPuesto(String puestoId, LocalDate fecha, String turno) {
        List<Reserva> reservas = repository.obtenerTodasLasReservas();
        
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
            repository.guardarReserva(reserva);
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
            Reserva reservaConfirmada = repository.guardarReserva(reserva);
            System.out.println("✅ Reserva confirmada: " + reservaId);
            return reservaConfirmada;
        }
        throw new IllegalArgumentException("Reserva no encontrada: " + reservaId);
    }

    @Override
    public Optional<Reserva> obtenerReservaPorId(String id) {
        return repository.obtenerReservaPorId(id);
    }

    @Override
    public List<Reserva> obtenerReservasPorCliente(String clienteId) {
        return repository.obtenerReservasPorCliente(clienteId);
    }

    @Override
    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        return repository.obtenerReservasPorFecha(fecha);
    }

    @Override
    public boolean activarReserva(String reservaId) {
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.ACTIVA);
            repository.guardarReserva(reserva);
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
            repository.guardarReserva(reserva);
            return true;
        }
        return false;
    }

    @Override
    public List<Reserva> obtenerReservasPendientes() {
        return repository.obtenerReservasPendientes();
    }
}