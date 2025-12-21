package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerReservaPago;
import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.enums.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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
        
        // Diagnóstico inicial
        diagnosticarEstadoInicial();
    }

    private void diagnosticarEstadoInicial() {
        System.out.println("\n=== DIAGNÓSTICO INICIAL RESERVAS ===");
        List<Reserva> todasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        System.out.println("📊 Total reservas en sistema: " + todasReservas.size());
        
        for (Reserva r : todasReservas) {
            System.out.println("   • ID: " + r.getId() + 
                             ", Estado: " + r.getEstado() + 
                             ", Cliente: " + r.getClienteId() + 
                             ", Fecha: " + r.getFecha());
        }
    }

    public Reserva crearReserva(Reserva request) {
        System.out.println("\n=== NUEVA SOLICITUD DE RESERVA ===");
        System.out.println("📅 Fecha: " + request.getFecha() + ", Turno: " + request.getTurno());
        System.out.println("👤 Usuario: " + request.getUsuario() + " (ClienteID: " + request.getClienteId() + ")");
        System.out.println("🅿️  Puesto solicitado: " + request.getPuestoId());
        
        // Validar que el puesto existe
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(request.getPuestoId());
                
        if (puestoOpt.isEmpty()) {
            System.err.println("❌ ERROR: Puesto no encontrado: " + request.getPuestoId());
            throw new IllegalArgumentException("Puesto no encontrado: " + request.getPuestoId());
        }

        Puesto puesto = puestoOpt.get();
        System.out.println("📍 Puesto encontrado: " + puesto.getNumero() + 
                          " - " + puesto.getUbicacion() + 
                          " (Tipo: " + puesto.getTipoPuesto() + ")");

        // Validar disponibilidad del puesto
        if (!verificarDisponibilidadPuesto(request.getPuestoId(), request.getFecha(), request.getTurno())) {
            System.err.println("❌ ERROR: Puesto no disponible para fecha/turno");
            throw new IllegalArgumentException("El puesto no está disponible para la fecha y turno seleccionados");
        }

        // Resolver tipo de cliente
        String tipoCliente = resolveTipoCliente(request.getClienteId());
        System.out.println("🎯 Tipo de cliente identificado: " + tipoCliente);
        
        // Validar tipo de cliente vs tipo de puesto
        if (tipoCliente != null && !validarTipoClientePuesto(tipoCliente, puesto.getTipoPuesto())) {
            String errorMsg = "El tipo de cliente '" + tipoCliente + "' no puede reservar un puesto de tipo '" + 
                             puesto.getTipoPuesto().getDescripcion() + "'";
            System.err.println("❌ ERROR: " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Generar ID único para la reserva
        String nuevoId = generarIdReserva();
        System.out.println("🆔 ID de reserva generado: " + nuevoId);

        // Crear nueva reserva usando constructor específico
        Reserva nuevaReserva = new Reserva(
            nuevoId,
            request.getPuestoId(),
            request.getClienteId(),
            request.getUsuario(),
            request.getFecha(),
            request.getTurno()
        );
        
        // Configurar horarios según turno
        setHorariosPorTurno(nuevaReserva, request.getTurno());
        
        // Estado inicial: PENDIENTE (ya está por defecto)
        System.out.println("📊 Estado inicial de reserva: " + nuevaReserva.getEstado());

        // Guardar reserva
        Reserva reservaGuardada = jsonManagerReservaPago.guardarReserva(nuevaReserva);
        
        System.out.println("\n✅ RESERVA CREADA EXITOSAMENTE");
        System.out.println("   ID: " + nuevoId);
        System.out.println("   Puesto: " + puesto.getNumero() + " (" + puesto.getUbicacion() + ")");
        System.out.println("   Horario: " + nuevaReserva.getHoraInicio() + " - " + nuevaReserva.getHoraFin());
        System.out.println("   Estado: " + nuevaReserva.getEstado().getDescripcion());
        
        // Verificar que se guardó correctamente
        Optional<Reserva> reservaVerificada = jsonManagerReservaPago.buscarReservaPorId(nuevoId);
        if (reservaVerificada.isPresent()) {
            System.out.println("✅ Reserva verificada en base de datos");
        } else {
            System.err.println("⚠️  ADVERTENCIA: Reserva no encontrada después de guardar");
        }
        
        return reservaGuardada;
    }

    private String generarIdReserva() {
        List<Reserva> todasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        int maxNumero = 0;
        
        for (Reserva r : todasReservas) {
            if (r.getId() != null && r.getId().startsWith("R")) {
                try {
                    String numeroStr = r.getId().substring(1);
                    int numero = Integer.parseInt(numeroStr);
                    if (numero > maxNumero) {
                        maxNumero = numero;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs mal formados
                }
            }
        }
        
        return "R" + (maxNumero + 1);
    }

    private void setHorariosPorTurno(Reserva reserva, String turno) {
        switch (turno.toUpperCase()) {
            case "MAÑANA":
                reserva.setHoraInicio(LocalTime.of(6, 0));
                reserva.setHoraFin(LocalTime.of(14, 0));
                break;
            case "TARDE":
                reserva.setHoraInicio(LocalTime.of(14, 0));
                reserva.setHoraFin(LocalTime.of(22, 0));
                break;
            case "NOCHE":
                reserva.setHoraInicio(LocalTime.of(22, 0));
                reserva.setHoraFin(LocalTime.of(6, 0));
                break;
            default:
                throw new IllegalArgumentException("Turno no válido: " + turno);
        }
    }

    // Método simplificado para crear reserva desde parámetros
    public Reserva crearReservaDesdeParametros(String puestoId, String clienteId, String usuario, 
                                                      LocalDate fecha, String turno) {
        System.out.println("📅 Creando reserva desde parámetros para fecha: " + fecha + ", turno: " + turno);
        System.out.println("👤 Cliente: " + usuario + " (ID: " + clienteId + ")");
        System.out.println("🅿️  Puesto solicitado: " + puestoId);
        
        // Crear objeto Reserva
        Reserva request = new Reserva();
        request.setPuestoId(puestoId);
        request.setClienteId(clienteId);
        request.setUsuario(usuario);
        request.setFecha(fecha);
        request.setTurno(turno);
        
        return crearReserva(request);
    }

    public Map<String, Object> consultarPuestosDisponibles(LocalDate fecha, String turno) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno);
        return consultarPuestosDisponiblesMap(fecha, turno, null);
    }

    public Map<String, Object> consultarPuestosDisponibles(LocalDate fecha, String turno, String clienteId) {
        System.out.println("🔍 Consultando puestos disponibles para: " + fecha + ", turno: " + turno +
                (clienteId != null ? ", cliente: " + clienteId : ""));
        Map<String, Object> response = consultarPuestosDisponiblesMap(fecha, turno, clienteId);
        System.out.println("✅ Consulta completada: " + response.get("puestosDisponibles") + " puestos disponibles");
        return response;
    }

    // Método principal que devuelve Map
    public Map<String, Object> consultarPuestosDisponiblesMap(LocalDate fecha, String turno, String clienteId) {
        System.out.println("\n=== CONSULTA DE PUESTOS DISPONIBLES ===");
        System.out.println("📅 Fecha: " + fecha);
        System.out.println("⏰ Turno: " + turno);
        System.out.println("👤 Cliente: " + (clienteId != null ? clienteId : "No especificado"));

        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        System.out.println("🅿️  Total puestos en sistema: " + todosLosPuestos.size());
        
        List<Puesto> puestosDisponibles = obtenerPuestosDisponiblesParaFecha(fecha, turno);
        System.out.println("✅ Puestos disponibles inicialmente: " + puestosDisponibles.size());

        // Si se proporcionó clienteId, filtrar por tipo de cliente
        if (clienteId != null && !clienteId.trim().isEmpty()) {
            String tipoCliente = resolveTipoCliente(clienteId);
            System.out.println("🎯 Tipo de cliente detectado: " + tipoCliente);
            
            if (tipoCliente != null) {
                final String tipo = tipoCliente;
                int antes = puestosDisponibles.size();
                puestosDisponibles = puestosDisponibles.stream()
                        .filter(p -> validarTipoClientePuesto(tipo, p.getTipoPuesto()))
                        .collect(Collectors.toList());
                System.out.println("🎯 Puestos después de filtrar por tipo de cliente: " + 
                                 puestosDisponibles.size() + " (se eliminaron " + (antes - puestosDisponibles.size()) + ")");
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fecha", fecha.toString());
        response.put("turno", turno);
        response.put("totalPuestos", todosLosPuestos.size());
        response.put("puestosDisponibles", puestosDisponibles.size());
        response.put("puestos", puestosDisponibles);
        response.put("mensaje", String.format("Se encontraron %d puestos disponibles de %d totales para el %s", 
                puestosDisponibles.size(), todosLosPuestos.size(), turno));

        // Mostrar puestos disponibles
        if (puestosDisponibles.size() > 0) {
            System.out.println("📋 Lista de puestos disponibles:");
            for (Puesto p : puestosDisponibles) {
                System.out.println("   • " + p.getNumero() + " - " + p.getUbicacion() + 
                                 " (Tipo: " + p.getTipoPuesto() + ")");
            }
        }

        return response;
    }

    private String resolveTipoCliente(String clienteId) {
        System.out.println("🔍 Resolviendo tipo de cliente para: " + clienteId);
        
        try {
            // Intentar como UUID primero
            try {
                Optional<Cliente> opt = jsonManagerCliente.buscarPorId(clienteId);
                if (opt.isPresent()) {
                    System.out.println("✅ Cliente encontrado por ID: " + opt.get().getTipoPersona());
                    return opt.get().getTipoPersona();
                }
            } catch (Exception e) {
                // No es UUID, continuar con otros métodos
            }

            // Buscar por usuario
            Optional<Cliente> opt = jsonManagerCliente.buscarPorUsuario(clienteId);
            if (opt.isPresent()) {
                System.out.println("✅ Cliente encontrado por usuario: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            // Buscar por cédula
            opt = jsonManagerCliente.buscarPorCedula(clienteId);
            if (opt.isPresent()) {
                System.out.println("✅ Cliente encontrado por cédula: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            // Buscar por email
            opt = jsonManagerCliente.buscarPorEmail(clienteId);
            if (opt.isPresent()) {
                System.out.println("✅ Cliente encontrado por email: " + opt.get().getTipoPersona());
                return opt.get().getTipoPersona();
            }

            System.out.println("⚠️  Cliente no encontrado con identificador: " + clienteId);
            return null;

        } catch (Exception e) {
            System.err.println("❌ Error resolviendo tipo de cliente: " + e.getMessage());
            return null;
        }
    }

    private boolean validarTipoClientePuesto(String tipoCliente, TipoPuesto tipoPuesto) {
        if (tipoCliente == null) {
            System.out.println("ℹ️  Tipo de cliente no especificado, se permite cualquier puesto");
            return true;
        }
        
        boolean valido;
        
        if ("UCAB".equalsIgnoreCase(tipoCliente)) {
            valido = tipoPuesto == TipoPuesto.REGULAR ||
                    tipoPuesto == TipoPuesto.DOCENTE ||
                    tipoPuesto == TipoPuesto.DISCAPACITADO ||
                    tipoPuesto == TipoPuesto.MOTOCICLETA;
            System.out.println("🎯 Cliente UCAB puede usar puesto " + tipoPuesto + ": " + valido);
        } else if ("VISITANTE".equalsIgnoreCase(tipoCliente)) {
            valido = tipoPuesto == TipoPuesto.REGULAR ||
                    tipoPuesto == TipoPuesto.VISITANTE;
            System.out.println("🎯 Cliente VISITANTE puede usar puesto " + tipoPuesto + ": " + valido);
        } else {
            System.out.println("⚠️  Tipo de cliente desconocido: " + tipoCliente);
            valido = true; // Permitir por defecto si tipo desconocido
        }
        
        return valido;
    }

    public List<Puesto> obtenerPuestosDisponiblesParaFecha(LocalDate fecha, String turno) {
        System.out.println("\n=== OBTENIENDO PUESTOS DISPONIBLES ===");
        System.out.println("📅 Fecha: " + fecha + ", Turno: " + turno);
        
        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        System.out.println("🅿️  Total puestos: " + todosLosPuestos.size());
        
        // Primero filtrar por estado DISPONIBLE
        List<Puesto> disponiblesPorEstado = todosLosPuestos.stream()
                .filter(puesto -> puesto.getEstadoPuesto() == EstadoPuesto.DISPONIBLE)
                .collect(Collectors.toList());
        
        System.out.println("✅ Puestos con estado DISPONIBLE: " + disponiblesPorEstado.size());
        
        // Luego verificar disponibilidad por fecha/turno
        List<Puesto> disponibles = disponiblesPorEstado.stream()
                .filter(puesto -> verificarDisponibilidadPuesto(puesto.getId(), fecha, turno))
                .collect(Collectors.toList());
        
        System.out.println("🎯 Puestos disponibles después de verificar reservas: " + disponibles.size());
        
        return disponibles;
    }

    public boolean verificarDisponibilidadPuesto(String puestoId, LocalDate fecha, String turno) {
        List<Reserva> reservas = jsonManagerReservaPago.obtenerTodasReservas();
        
        System.out.println("🔍 Verificando disponibilidad de puesto " + puestoId + 
                          " para " + fecha + " " + turno);
        System.out.println("📋 Total reservas en sistema: " + reservas.size());
        
        // Verificar si hay reservas activas para este puesto, fecha y turno
        boolean tieneReserva = false;
        
        for (Reserva reserva : reservas) {
            if (reserva.getPuestoId().equals(puestoId) &&
                reserva.getFecha().equals(fecha) &&
                reserva.getTurno().equalsIgnoreCase(turno)) {
                
                System.out.println("   ⚠️  Encontrada reserva: ID=" + reserva.getId() + 
                                 ", Estado=" + reserva.getEstado());
                
                // Solo considerar reservas que no estén canceladas
                if (reserva.getEstado() != EstadoReserva.CANCELADA) {
                    tieneReserva = true;
                    System.out.println("   ❌ Puesto NO disponible por reserva activa");
                    break;
                } else {
                    System.out.println("   ✅ Reserva está cancelada, ignorando");
                }
            }
        }
        
        boolean disponible = !tieneReserva;
        System.out.println("📊 Resultado: " + (disponible ? "DISPONIBLE" : "NO DISPONIBLE"));
        
        return disponible;
    }

    public boolean cancelarReserva(String reservaId) {
        System.out.println("\n=== CANCELANDO RESERVA ===");
        System.out.println("🗑️  ID de reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            System.out.println("✅ Reserva encontrada:");
            System.out.println("   Cliente: " + reserva.getUsuario());
            System.out.println("   Puesto: " + reserva.getPuestoId());
            System.out.println("   Fecha: " + reserva.getFecha() + " " + reserva.getTurno());
            System.out.println("   Estado anterior: " + reserva.getEstado());
            
            reserva.setEstado(EstadoReserva.CANCELADA);
            jsonManagerReservaPago.guardarReserva(reserva);
            
            System.out.println("✅ Reserva cancelada exitosamente");
            System.out.println("🔄 Nuevo estado: " + reserva.getEstado());
            return true;
        }
        
        System.out.println("❌ Reserva no encontrada: " + reservaId);
        return false;
    }

    public Reserva confirmarReserva(String reservaId) {
        System.out.println("\n=== CONFIRMANDO RESERVA ===");
        System.out.println("✅ ID de reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            System.out.println("📋 Datos de reserva:");
            System.out.println("   Cliente: " + reserva.getUsuario());
            System.out.println("   Puesto: " + reserva.getPuestoId());
            System.out.println("   Fecha: " + reserva.getFecha() + " " + reserva.getTurno());
            System.out.println("   Estado anterior: " + reserva.getEstado());
            
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            Reserva reservaConfirmada = jsonManagerReservaPago.guardarReserva(reserva);
            
            System.out.println("✅ Reserva confirmada exitosamente");
            System.out.println("🔄 Nuevo estado: " + reservaConfirmada.getEstado());
            return reservaConfirmada;
        }
        System.err.println("❌ Reserva no encontrada: " + reservaId);
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
        System.out.println("\n=== ACTIVANDO RESERVA ===");
        System.out.println("🚀 ID de reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            System.out.println("📋 Reserva encontrada, estado actual: " + reserva.getEstado());
            
            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                reserva.setEstado(EstadoReserva.ACTIVA);
                jsonManagerReservaPago.guardarReserva(reserva);
                System.out.println("✅ Reserva activada exitosamente");
                return true;
            } else {
                System.err.println("❌ Solo se pueden activar reservas CONFIRMADAS");
                return false;
            }
        }
        
        System.err.println("❌ No se pudo activar reserva: " + reservaId);
        return false;
    }

    public boolean completarReserva(String reservaId) {
        System.out.println("\n=== COMPLETANDO RESERVA ===");
        System.out.println("🏁 ID de reserva: " + reservaId);
        
        Optional<Reserva> reservaOpt = obtenerReservaPorId(reservaId);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            System.out.println("📋 Reserva encontrada, estado actual: " + reserva.getEstado());
            
            if (reserva.getEstado() == EstadoReserva.ACTIVA) {
                reserva.setEstado(EstadoReserva.COMPLETADA);
                jsonManagerReservaPago.guardarReserva(reserva);
                System.out.println("✅ Reserva completada exitosamente");
                return true;
            } else {
                System.err.println("❌ Solo se pueden completar reservas ACTIVAS");
                return false;
            }
        }
        
        System.err.println("❌ No se pudo completar reserva: " + reservaId);
        return false;
    }

    public List<Reserva> obtenerReservasPendientes() {
        List<Reserva> reservasPendientes = jsonManagerReservaPago.buscarReservasPendientes();
        System.out.println("⏳ Reservas pendientes encontradas: " + reservasPendientes.size());
        return reservasPendientes;
    }

    // Métodos adicionales para estadísticas y reporting

    public Map<String, Object> obtenerEstadisticasReservas() {
        List<Reserva> todasLasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        
        System.out.println("\n=== ESTADÍSTICAS DE RESERVAS ===");
        System.out.println("📊 Total reservas: " + todasLasReservas.size());
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalReservas", todasLasReservas.size());
        
        // Estadísticas por estado
        Map<String, Long> porEstado = todasLasReservas.stream()
                .collect(Collectors.groupingBy(
                    reserva -> reserva.getEstado().name(),
                    Collectors.counting()
                ));
        estadisticas.put("reservasPorEstado", porEstado);
        
        System.out.println("📈 Por estado:");
        for (Map.Entry<String, Long> entry : porEstado.entrySet()) {
            System.out.println("   • " + entry.getKey() + ": " + entry.getValue());
        }
        
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
        List<Reserva> activas = todasLasReservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA || r.getEstado() == EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
        
        System.out.println("🎯 Reservas activas/confirmadas: " + activas.size());
        return activas;
    }

    public void diagnostico() {
        System.out.println("\n=== DIAGNÓSTICO COMPLETO DEL SERVICIO RESERVAS ===");
        System.out.println("📅 Total reservas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        System.out.println("🅿️  Total puestos: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        System.out.println("👥 Total clientes: " + jsonManagerCliente.obtenerTodosClientes().size());
        
        // Mostrar todas las reservas
        List<Reserva> todasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        System.out.println("\n📋 LISTA COMPLETA DE RESERVAS:");
        if (todasReservas.isEmpty()) {
            System.out.println("   (No hay reservas)");
        } else {
            for (Reserva r : todasReservas) {
                System.out.println(String.format("   • %-5s | %-20s | %-10s | %-15s | %-8s", 
                    r.getId(), r.getUsuario(), r.getFecha(), r.getTurno(), r.getEstado()));
            }
        }
        
        // Puestos disponibles hoy
        List<Puesto> disponiblesHoy = obtenerPuestosDisponiblesParaFecha(LocalDate.now(), "MAÑANA");
        System.out.println("\n🅿️  Puestos disponibles hoy (mañana): " + disponiblesHoy.size());
    }
}