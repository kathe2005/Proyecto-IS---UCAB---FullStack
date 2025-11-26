package com.ucab.estacionamiento.model.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerReservaPago;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.clases.PuestosDisponiblesResponse;
import com.ucab.estacionamiento.model.clases.Reserva;
import com.ucab.estacionamiento.model.clases.ReservaRequest;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.enums.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl {
    
    private final JsonManagerReservaPago jsonManagerReservaPago;
    private final JsonManagerPuesto jsonManagerPuesto;
    private final JsonManagerCliente jsonManagerCliente;

    public ReservaServiceImpl() {
        this.jsonManagerReservaPago = new JsonManagerReservaPago();
        this.jsonManagerPuesto = new JsonManagerPuesto();
        this.jsonManagerCliente = new JsonManagerCliente();
        System.out.println("✅ ReservaServiceImpl inicializado con gestores JSON");
        System.out.println("📅 Reservas cargadas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        System.out.println("🅿️  Puestos cargados: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        System.out.println("👥 Clientes cargados: " + jsonManagerCliente.obtenerTodosClientes().size());
    }

    public Reserva crearReserva(ReservaRequest request) {
        System.out.println("📅 Creando reserva para fecha: " + request.getFecha() + ", turno: " + request.getTurno());
        System.out.println("👤 Cliente: " + request.getUsuario() + " (ID: " + request.getClienteId() + ")");
        System.out.println("🅿️  Puesto solicitado: " + request.getPuestoId());
        
        // Validar que el puesto existe
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(request.getPuestoId());
                
        if (puestoOpt.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + request.getPuestoId());
        }

        Puesto puesto = puestoOpt.get();
        System.out.println("📍 Puesto encontrado: " + puesto.getNumero() + " - " + puesto.getUbicacion());

        // Validar disponibilidad del puesto
        if (!verificarDisponibilidadPuesto(request.getPuestoId(), request.getFecha(), request.getTurno())) {
            throw new IllegalArgumentException("El puesto no está disponible para la fecha y turno seleccionados");
        }

        // Validar tipo de cliente vs tipo de puesto
        String tipoCliente = resolveTipoCliente(request.getClienteId());
        if (tipoCliente != null && !validarTipoClientePuesto(tipoCliente, puesto.getTipoPuesto())) {
            throw new IllegalArgumentException("El tipo de cliente '" + tipoCliente + "' no puede reservar un puesto de tipo '" + puesto.getTipoPuesto().getDescripcion() + "'");
        }

        // Crear nueva reserva
        String nuevoId = "R" + (jsonManagerReservaPago.obtenerTodasReservas().size() + 1);
        Reserva nuevaReserva = new Reserva(nuevoId, request.getPuestoId(), 
                                         request.getClienteId(), request.getUsuario(),
                                         request.getFecha(), request.getTurno());
        
        Reserva reservaGuardada = jsonManagerReservaPago.guardarReserva(nuevaReserva);
        
        System.out.println("✅ Reserva creada exitosamente: " + nuevoId);
        System.out.println("⏰ Horario: " + nuevaReserva.getHoraInicio() + " - " + nuevaReserva.getHoraFin());
        System.out.println("📊 Estado inicial: " + nuevaReserva.getEstado().getDescripcion());
        
        return reservaGuardada;
    }

    public PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno);
        return consultarPuestosDisponibles(fecha, turno, null);
    }

    public PuestosDisponiblesResponse consultarPuestosDisponibles(LocalDate fecha, String turno, String clienteId) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno + 
                          (clienteId != null ? ", cliente: " + clienteId : ""));

        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        List<Puesto> puestosDisponibles = obtenerPuestosDisponiblesParaFecha(fecha, turno);

        System.out.println("📊 Base: " + todosLosPuestos.size() + " puestos totales, " + 
                          puestosDisponibles.size() + " disponibles inicialmente");

        // Si se proporcionó clienteId, filtrar por tipo de cliente
        if (clienteId != null && !clienteId.trim().isEmpty()) {
            String tipoCliente = resolveTipoCliente(clienteId);
            if (tipoCliente != null) {
                final String tipo = tipoCliente;
                int antesFiltro = puestosDisponibles.size();
                puestosDisponibles = puestosDisponibles.stream()
                        .filter(p -> validarTipoClientePuesto(tipo, p.getTipoPuesto()))
                        .collect(Collectors.toList());
                System.out.println("🎯 Filtrado por tipo '" + tipo + "': " + antesFiltro + " → " + puestosDisponibles.size() + " puestos");
            } else {
                System.out.println("⚠️  No se pudo determinar el tipo de cliente para: " + clienteId);
            }
        }

        PuestosDisponiblesResponse response = new PuestosDisponiblesResponse(
                fecha, turno, todosLosPuestos.size(), puestosDisponibles.size(), puestosDisponibles
        );

        System.out.println("✅ Consulta completada: " + puestosDisponibles.size() + " puestos disponibles");
        
        // Mostrar distribución por tipo
        Map<String, Long> distribucion = puestosDisponibles.stream()
                .collect(Collectors.groupingBy(p -> p.getTipoPuesto().getDescripcion(), Collectors.counting()));
        System.out.println("📈 Distribución: " + distribucion);
        
        return response;
    }

    // Intentar resolver el tipo de cliente consultando el repositorio de clientes
    private String resolveTipoCliente(String clienteId) {
        try {
            // Buscar por usuario
            Optional<Cliente> opt = jsonManagerCliente.buscarPorUsuario(clienteId);
            if (opt.isPresent()) {
                System.out.println("👤 Cliente encontrado por usuario: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            // Buscar por cedula
            opt = jsonManagerCliente.buscarPorCedula(clienteId);
            if (opt.isPresent()) {
                System.out.println("👤 Cliente encontrado por cédula: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            // Buscar por email
            opt = jsonManagerCliente.buscarPorEmail(clienteId);
            if (opt.isPresent()) {
                System.out.println("👤 Cliente encontrado por email: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            System.out.println("⚠️  Cliente no encontrado con identificador: " + clienteId);

        } catch (Exception e) {
            System.err.println("❌ Error resolviendo tipo de cliente: " + e.getMessage());
        }
        return null;
    }

    // Validación de compatibilidad entre tipo de cliente y tipo de puesto
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

    public List<Puesto> obtenerPuestosDisponiblesParaFecha(LocalDate fecha, String turno) {
        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        
        List<Puesto> disponibles = todosLosPuestos.stream()
                .filter(puesto -> puesto.getEstadoPuesto() == EstadoPuesto.DISPONIBLE)
                .filter(puesto -> verificarDisponibilidadPuesto(puesto.getId(), fecha, turno))
                .collect(Collectors.toList());
        
        System.out.println("🅿️  Puestos disponibles para " + fecha + " " + turno + ": " + 
                          disponibles.size() + "/" + todosLosPuestos.size());
        
        return disponibles;
    }

    public boolean verificarDisponibilidadPuesto(String puestoId, LocalDate fecha, String turno) {
        List<Reserva> reservas = jsonManagerReservaPago.obtenerTodasReservas();
        
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
        
        if (tieneReserva) {
            System.out.println("❌ Puesto " + puestoId + " ya tiene reserva para " + fecha + " " + turno);
        }
        
        return !tieneReserva;
    }

    public boolean cancelarReserva(String reservaId) {
        System.out.println("🗑️  Cancelando reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.CANCELADA);
            jsonManagerReservaPago.guardarReserva(reserva);
            System.out.println("✅ Reserva cancelada: " + reservaId);
            System.out.println("👤 Cliente: " + reserva.getUsuario());
            System.out.println("🅿️  Puesto liberado: " + reserva.getPuestoId());
            return true;
        }
        
        System.out.println("❌ Reserva no encontrada: " + reservaId);
        return false;
    }

    public Reserva confirmarReserva(String reservaId) {
        System.out.println("✅ Confirmando reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            Reserva reservaConfirmada = jsonManagerReservaPago.guardarReserva(reserva);
            System.out.println("✅ Reserva confirmada: " + reservaId);
            System.out.println("👤 Cliente: " + reserva.getUsuario());
            System.out.println("📅 Fecha: " + reserva.getFecha() + " " + reserva.getTurno());
            return reservaConfirmada;
        }
        throw new IllegalArgumentException("Reserva no encontrada: " + reservaId);
    }

    public Optional<Reserva> obtenerReservaPorId(String id) {
        return jsonManagerReservaPago.buscarReservaPorId(id);
    }

    public List<Reserva> obtenerReservasPorCliente(String clienteId) {
        List<Reserva> reservas = jsonManagerReservaPago.buscarReservasPorCliente(clienteId);
        System.out.println("📋 Obteniendo " + reservas.size() + " reservas para cliente: " + clienteId);
        return reservas;
    }

    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        List<Reserva> reservas = jsonManagerReservaPago.buscarReservasPorFecha(fecha);
        System.out.println("📅 Obteniendo " + reservas.size() + " reservas para fecha: " + fecha);
        
        // Mostrar distribución por turno
        Map<String, Long> porTurno = reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getTurno, Collectors.counting()));
        System.out.println("⏰ Distribución por turno: " + porTurno);
        
        return reservas;
    }

    public boolean activarReserva(String reservaId) {
        System.out.println("🚀 Activando reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.ACTIVA);
            jsonManagerReservaPago.guardarReserva(reserva);
            System.out.println("✅ Reserva activada: " + reservaId);
            return true;
        }
        
        System.out.println("❌ No se pudo activar reserva: " + reservaId);
        return false;
    }

    public boolean completarReserva(String reservaId) {
        System.out.println("🏁 Completando reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado(EstadoReserva.COMPLETADA);
            jsonManagerReservaPago.guardarReserva(reserva);
            System.out.println("✅ Reserva completada: " + reservaId);
            return true;
        }
        
        System.out.println("❌ No se pudo completar reserva: " + reservaId);
        return false;
    }

    public List<Reserva> obtenerReservasPendientes() {
        List<Reserva> reservasPendientes = jsonManagerReservaPago.buscarReservasPendientes();
        System.out.println("⏳ Reservas pendientes: " + reservasPendientes.size());
        return reservasPendientes;
    }

    // Métodos adicionales para estadísticas y reporting

    public Map<String, Object> obtenerEstadisticasReservas() {
        List<Reserva> todasLasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalReservas", todasLasReservas.size());
        
        // Estadísticas por estado
        Map<String, Long> porEstado = todasLasReservas.stream()
                .collect(Collectors.groupingBy(
                    reserva -> reserva.getEstado().name(),
                    Collectors.counting()
                ));
        estadisticas.put("reservasPorEstado", porEstado);
        
        // Estadísticas por turno
        Map<String, Long> porTurno = todasLasReservas.stream()
                .collect(Collectors.groupingBy(Reserva::getTurno, Collectors.counting()));
        estadisticas.put("reservasPorTurno", porTurno);
        
        // Reservas de hoy
        long reservasHoy = todasLasReservas.stream()
                .filter(r -> r.getFecha().equals(LocalDate.now()))
                .count();
        estadisticas.put("reservasHoy", reservasHoy);
        
        estadisticas.put("fechaGeneracion", LocalDate.now());
        
        return estadisticas;
    }

    public List<Reserva> obtenerReservasActivas() {
        List<Reserva> todasLasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        return todasLasReservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA || r.getEstado() == EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
    }

    public void diagnostico() {
        System.out.println("🩺 DIAGNÓSTICO DEL SERVICIO RESERVAS");
        System.out.println("📅 Total reservas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        System.out.println("🅿️  Total puestos: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        System.out.println("👥 Total clientes: " + jsonManagerCliente.obtenerTodosClientes().size());
        
        Map<String, Object> estadisticas = obtenerEstadisticasReservas();
        System.out.println("📊 Estadísticas de reservas:");
        System.out.println("   Por estado: " + estadisticas.get("reservasPorEstado"));
        System.out.println("   Por turno: " + estadisticas.get("reservasPorTurno"));
        System.out.println("   Hoy: " + estadisticas.get("reservasHoy"));
        
        // Puestos disponibles hoy
        List<Puesto> disponiblesHoy = obtenerPuestosDisponiblesParaFecha(LocalDate.now(), "MAÑANA");
        System.out.println("🅿️  Puestos disponibles hoy (mañana): " + disponiblesHoy.size());
    }
}