package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerReservaPago;
import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.enums.EstadoReserva;
import com.ucab.estacionamiento.model.enums.MetodoPago;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl {
    
    private final JsonManagerReservaPago jsonManagerReservaPago;
    private final JsonManagerCliente jsonManagerCliente;
    private final JsonManagerPuesto jsonManagerPuesto;

    public PagoServiceImpl() {
        this.jsonManagerReservaPago = new JsonManagerReservaPago();
        this.jsonManagerCliente = new JsonManagerCliente();
        this.jsonManagerPuesto = new JsonManagerPuesto();
        System.out.println("✅ PagoServiceImpl inicializado con gestores JSON");
        System.out.println("💰 Pagos cargados: " + jsonManagerReservaPago.obtenerTodosPagos().size());
        System.out.println("📅 Reservas cargadas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        System.out.println("👥 Clientes cargados: " + jsonManagerCliente.obtenerTodosClientes().size());
        System.out.println("🅿️  Puestos cargados: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        
        // Diagnóstico inicial
        diagnosticarEstadoInicial();
    }

    private void diagnosticarEstadoInicial() {
        System.out.println("\n=== DIAGNÓSTICO INICIAL PAGOS ===");
        
        // Mostrar todas las reservas
        List<Reserva> todasReservas = jsonManagerReservaPago.obtenerTodasReservas();
        System.out.println("📊 Total reservas: " + todasReservas.size());
        
        for (Reserva r : todasReservas) {
            System.out.println(String.format("   • %-5s | Cliente: %-20s | Fecha: %-10s | Turno: %-8s | Estado: %-12s", 
                r.getId(), r.getUsuario(), r.getFecha(), r.getTurno(), r.getEstado()));
        }
        
        // Mostrar pagos existentes
        List<Pago> todosPagos = jsonManagerReservaPago.obtenerTodosPagos();
        System.out.println("\n💰 Total pagos: " + todosPagos.size());
        for (Pago p : todosPagos) {
            System.out.println(String.format("   • %-8s | Reserva: %-5s | Monto: %-8.2f | Método: %-15s", 
                p.getId(), p.getReservaId(), p.getMonto(), p.getMetodoPago()));
        }
    }

    public Pago registrarPago(Pago pagoRequest) {
        System.out.println("\n=== NUEVA SOLICITUD DE PAGO ===");
        System.out.println("💰 Reserva: " + pagoRequest.getReservaId());
        System.out.println("👤 Cliente: " + pagoRequest.getClienteId());
        System.out.println("💵 Monto solicitado: $" + pagoRequest.getMonto());
        System.out.println("💳 Método: " + pagoRequest.getMetodoPago());
        
        // Verificar que la reserva existe
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(pagoRequest.getReservaId());
        if (reservaOpt.isEmpty()) {
            System.err.println("❌ ERROR: La reserva especificada no existe: " + pagoRequest.getReservaId());
            throw new IllegalArgumentException("La reserva especificada no existe");
        }

        Reserva reserva = reservaOpt.get();
        System.out.println("✅ Reserva encontrada:");
        System.out.println("   ID: " + reserva.getId());
        System.out.println("   Cliente: " + reserva.getUsuario());
        System.out.println("   Fecha: " + reserva.getFecha() + " " + reserva.getTurno());
        System.out.println("   Estado: " + reserva.getEstado());

        // Validar que no exista ya un pago para esta reserva
        if (existePagoParaReserva(pagoRequest.getReservaId())) {
            System.err.println("❌ ERROR: Ya existe un pago registrado para esta reserva");
            throw new IllegalArgumentException("Ya existe un pago registrado para esta reserva");
        }

        // Validar monto positivo
        if (pagoRequest.getMonto() <= 0) {
            System.err.println("❌ ERROR: El monto debe ser mayor a cero");
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Verificar que el monto sea razonable
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(reserva.getPuestoId());
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            double tarifaEsperada = calcularTarifa(puesto.getTipoPuesto().name(), reserva.getTurno());
            System.out.println("💰 Tarifa esperada para puesto " + puesto.getTipoPuesto() + 
                             " turno " + reserva.getTurno() + ": $" + tarifaEsperada);
            
            if (Math.abs(pagoRequest.getMonto() - tarifaEsperada) > 1.0) {
                System.out.println("⚠️  ADVERTENCIA: Monto diferente de la tarifa esperada");
            }
        }

        // Crear nuevo pago
        String nuevoId = generarIdPago();
        System.out.println("🆔 ID de pago generado: " + nuevoId);
        
        Pago nuevoPago = new Pago(
            nuevoId, 
            pagoRequest.getReservaId(), 
            pagoRequest.getClienteId(), 
            pagoRequest.getMonto(),
            pagoRequest.getMetodoPago(), 
            pagoRequest.getReferencia()
        );
        
        nuevoPago.setDescripcion(pagoRequest.getDescripcion());
        nuevoPago.setFechaPago(java.time.LocalDateTime.now());

        // Guardar pago
        Pago pagoGuardado = jsonManagerReservaPago.guardarPago(nuevoPago);
        
        // Actualizar estado de la reserva a CONFIRMADA
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            jsonManagerReservaPago.guardarReserva(reserva);
            System.out.println("✅ Estado de reserva actualizado a CONFIRMADA");
        }
        
        System.out.println("\n✅ PAGO REGISTRADO EXITOSAMENTE");
        System.out.println("   ID Pago: " + nuevoId);
        System.out.println("   Reserva: " + pagoRequest.getReservaId());
        System.out.println("   Monto: $" + pagoRequest.getMonto());
        System.out.println("   Método: " + pagoRequest.getMetodoPago());
        System.out.println("   Referencia: " + pagoRequest.getReferencia());
        
        // Verificar que se guardó
        Optional<Pago> pagoVerificado = jsonManagerReservaPago.buscarPagoPorId(nuevoId);
        if (pagoVerificado.isPresent()) {
            System.out.println("✅ Pago verificado en base de datos");
        } else {
            System.err.println("⚠️  ADVERTENCIA: Pago no encontrado después de guardar");
        }
        
        return pagoGuardado;
    }

    private String generarIdPago() {
        List<Pago> todosPagos = jsonManagerReservaPago.obtenerTodosPagos();
        int maxNumero = 0;
        
        for (Pago p : todosPagos) {
            if (p.getId() != null && p.getId().startsWith("PAY")) {
                try {
                    String numeroStr = p.getId().substring(3);
                    int numero = Integer.parseInt(numeroStr);
                    if (numero > maxNumero) {
                        maxNumero = numero;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs mal formados
                }
            }
        }
        
        return "PAY" + (maxNumero + 1);
    }

    // Método simplificado para registrar pago desde parámetros
    public Pago registrarPagoDesdeParametros(String reservaId, String clienteId, double monto, 
                                                    MetodoPago metodoPago, String referencia, String descripcion) {
        System.out.println("💰 Registrando pago simplificado para reserva: " + reservaId);
        
        Pago pagoRequest = new Pago();
        pagoRequest.setReservaId(reservaId);
        pagoRequest.setClienteId(clienteId);
        pagoRequest.setMonto(monto);
        pagoRequest.setMetodoPago(metodoPago);
        pagoRequest.setReferencia(referencia);
        pagoRequest.setDescripcion(descripcion);
        
        return registrarPago(pagoRequest);
    }

    public Optional<Pago> obtenerPagoPorId(String id) {
        return jsonManagerReservaPago.buscarPagoPorId(id);
    }

    public List<Pago> obtenerPagosPorCliente(String clienteId) {
        List<Pago> pagos = jsonManagerReservaPago.buscarPagosPorCliente(clienteId);
        System.out.println("💰 Obteniendo " + pagos.size() + " pagos para cliente: " + clienteId);
        return pagos;
    }

    public List<Pago> obtenerTodosLosPagos() {
        List<Pago> pagos = jsonManagerReservaPago.obtenerTodosPagos();
        System.out.println("💰 Total pagos en sistema: " + pagos.size());
        return pagos;
    }

    public boolean existePagoParaReserva(String reservaId) {
        boolean existe = jsonManagerReservaPago.existePagoParaReserva(reservaId);
        System.out.println("🔍 ¿Existe pago para reserva " + reservaId + "? " + (existe ? "SÍ" : "NO"));
        return existe;
    }

    // Método para obtener reservas pendientes de pago - **MEJORADO CON LOGS**
    public List<Map<String, Object>> obtenerReservasPendientesPago() {
        System.out.println("\n=== OBTENIENDO RESERVAS PENDIENTES DE PAGO ===");
        
        List<Reserva> reservas = jsonManagerReservaPago.obtenerTodasReservas();
        List<Cliente> clientes = jsonManagerCliente.obtenerTodosClientes();
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();

        System.out.println("📊 Datos disponibles:");
        System.out.println("   • Total reservas: " + reservas.size());
        System.out.println("   • Total clientes: " + clientes.size());
        System.out.println("   • Total puestos: " + puestos.size());
        
        // Mostrar TODAS las reservas para debug
        System.out.println("\n📋 LISTA COMPLETA DE RESERVAS:");
        for (Reserva r : reservas) {
            System.out.println(String.format("   • %-5s | %-20s | %-10s | %-8s | %-12s", 
                r.getId(), r.getUsuario(), r.getFecha(), r.getTurno(), r.getEstado()));
        }

        // Filtrar reservas pendientes o confirmadas SIN PAGO
        List<Map<String, Object>> reservasPendientes = reservas.stream()
                .filter(reserva -> 
                    (reserva.getEstado() == EstadoReserva.PENDIENTE || 
                     reserva.getEstado() == EstadoReserva.CONFIRMADA) &&
                    !existePagoParaReserva(reserva.getId())
                )
                .map(reserva -> {
                    System.out.println("✅ Reserva elegible para pago: " + reserva.getId() + 
                                     " (Estado: " + reserva.getEstado() + ")");
                    
                    Map<String, Object> reservaInfo = new HashMap<>();
                    reservaInfo.put("id", reserva.getId());
                    reservaInfo.put("puestoId", reserva.getPuestoId());
                    reservaInfo.put("clienteId", reserva.getClienteId());
                    reservaInfo.put("usuario", reserva.getUsuario());
                    reservaInfo.put("fecha", reserva.getFecha().toString());
                    reservaInfo.put("turno", reserva.getTurno());
                    reservaInfo.put("estado", reserva.getEstado().name());
                    reservaInfo.put("horaInicio", reserva.getHoraInicio() != null ? reserva.getHoraInicio().toString() : "");
                    reservaInfo.put("horaFin", reserva.getHoraFin() != null ? reserva.getHoraFin().toString() : "");

                    // Calcular tarifa automáticamente
                    Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(reserva.getPuestoId());
                    if (puestoOpt.isPresent()) {
                        Puesto puesto = puestoOpt.get();
                        double tarifa = calcularTarifa(puesto.getTipoPuesto().name(), reserva.getTurno());
                        reservaInfo.put("tarifaCalculada", tarifa);
                        System.out.println("   💰 Tarifa calculada: $" + tarifa + 
                                         " (Puesto: " + puesto.getTipoPuesto() + 
                                         ", Turno: " + reserva.getTurno() + ")");
                    } else {
                        reservaInfo.put("tarifaCalculada", 0.0);
                        System.err.println("   ⚠️  Puesto no encontrado para tarifa: " + reserva.getPuestoId());
                    }

                    // Información del cliente
                    Optional<Cliente> clienteOpt = clientes.stream()
                            .filter(c -> c.getId().toString().equals(reserva.getClienteId()) || 
                                       c.getUsuario().equals(reserva.getUsuario()))
                            .findFirst();
                    
                    if (clienteOpt.isPresent()) {
                        Cliente cliente = clienteOpt.get();
                        Map<String, String> clienteInfo = new HashMap<>();
                        clienteInfo.put("nombre", cliente.getNombre());
                        clienteInfo.put("apellido", cliente.getApellido());
                        clienteInfo.put("cedula", cliente.getCedula());
                        clienteInfo.put("email", cliente.getEmail());
                        clienteInfo.put("tipoPersona", cliente.getTipoPersona());
                        clienteInfo.put("id", cliente.getId().toString());
                        reservaInfo.put("cliente", clienteInfo);
                        System.out.println("   👤 Cliente encontrado: " + cliente.getNombre() + " " + cliente.getApellido());
                    } else {
                        System.err.println("   ⚠️  Cliente no encontrado para reserva: " + reserva.getUsuario());
                        Map<String, String> clienteInfo = new HashMap<>();
                        clienteInfo.put("nombre", "No encontrado");
                        clienteInfo.put("apellido", "");
                        clienteInfo.put("cedula", "");
                        clienteInfo.put("email", "");
                        clienteInfo.put("tipoPersona", "");
                        reservaInfo.put("cliente", clienteInfo);
                    }

                    // Información del puesto
                    puestoOpt.ifPresent(puesto -> {
                        Map<String, String> puestoInfo = new HashMap<>();
                        puestoInfo.put("numero", puesto.getNumero());
                        puestoInfo.put("ubicacion", puesto.getUbicacion());
                        puestoInfo.put("tipoPuesto", puesto.getTipoPuesto().name());
                        puestoInfo.put("estadoPuesto", puesto.getEstadoPuesto().name());
                        reservaInfo.put("puesto", puestoInfo);
                        System.out.println("   🅿️  Puesto: " + puesto.getNumero() + " (" + puesto.getUbicacion() + ")");
                    });

                    return reservaInfo;
                })
                .collect(Collectors.toList());

        System.out.println("\n✅ RESERVAS PENDIENTES DE PAGO ENCONTRADAS: " + reservasPendientes.size());
        
        if (reservasPendientes.isEmpty()) {
            System.out.println("ℹ️  No hay reservas pendientes de pago");
            System.out.println("   Verifique que:");
            System.out.println("   1. Las reservas estén en estado PENDIENTE o CONFIRMADA");
            System.out.println("   2. No tengan pagos registrados");
            System.out.println("   3. Los clientes existan en la base de datos");
        }

        return reservasPendientes;
    }

    // Método para calcular tarifas
    public double calcularTarifa(String tipoPuesto, String turno) {
        System.out.println("🧮 Calculando tarifa para tipo: " + tipoPuesto + ", turno: " + turno);
        
        // Tarifas base
        Map<String, Map<String, Double>> tarifas = new HashMap<>();
        
        Map<String, Double> regular = new HashMap<>();
        regular.put("MAÑANA", 5.0);
        regular.put("TARDE", 7.0);
        regular.put("NOCHE", 10.0);
        tarifas.put("REGULAR", regular);

        Map<String, Double> discapacitado = new HashMap<>();
        discapacitado.put("MAÑANA", 3.0);
        discapacitado.put("TARDE", 5.0);
        discapacitado.put("NOCHE", 7.0);
        tarifas.put("DISCAPACITADO", discapacitado);

        Map<String, Double> docente = new HashMap<>();
        docente.put("MAÑANA", 4.0);
        docente.put("TARDE", 6.0);
        docente.put("NOCHE", 8.0);
        tarifas.put("DOCENTE", docente);

        Map<String, Double> visitante = new HashMap<>();
        visitante.put("MAÑANA", 8.0);
        visitante.put("TARDE", 12.0);
        visitante.put("NOCHE", 15.0);
        tarifas.put("VISITANTE", visitante);

        Map<String, Double> motocicleta = new HashMap<>();
        motocicleta.put("MAÑANA", 3.0);
        motocicleta.put("TARDE", 4.0);
        motocicleta.put("NOCHE", 5.0);
        tarifas.put("MOTOCICLETA", motocicleta);

        String tipo = tipoPuesto.toUpperCase();
        String turnoUpper = turno.toUpperCase();
        
        double tarifa;
        if (tarifas.containsKey(tipo) && tarifas.get(tipo).containsKey(turnoUpper)) {
            tarifa = tarifas.get(tipo).get(turnoUpper);
        } else {
            tarifa = 5.0; // Tarifa por defecto
            System.out.println("⚠️  Usando tarifa por defecto: $" + tarifa);
        }
        
        System.out.println("💰 Tarifa calculada: $" + tarifa);
        return tarifa;
    }

    // Métodos adicionales para estadísticas
    public Map<String, Object> obtenerEstadisticasPagos() {
        List<Pago> pagos = jsonManagerReservaPago.obtenerTodosPagos();
        
        System.out.println("\n=== ESTADÍSTICAS DE PAGOS ===");
        System.out.println("💰 Total pagos: " + pagos.size());
        
        double totalRecaudado = pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
        
        System.out.println("💵 Total recaudado: $" + totalRecaudado);
        
        Map<String, Long> pagosPorMetodo = pagos.stream()
                .collect(Collectors.groupingBy(
                    pago -> pago.getMetodoPago().name(),
                    Collectors.counting()
                ));
        
        System.out.println("💳 Distribución por método:");
        for (Map.Entry<String, Long> entry : pagosPorMetodo.entrySet()) {
            System.out.println("   • " + entry.getKey() + ": " + entry.getValue());
        }
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalPagos", pagos.size());
        estadisticas.put("totalRecaudado", totalRecaudado);
        estadisticas.put("pagosPorMetodo", pagosPorMetodo);
        estadisticas.put("fechaGeneracion", new Date());
        
        return estadisticas;
    }

    public List<Pago> obtenerPagosPorRangoFechas(Date fechaInicio, Date fechaFin) {
        return jsonManagerReservaPago.obtenerTodosPagos().stream()
                .filter(pago -> pago.getFechaPago() != null)
                .filter(pago -> {
                    // Implementar lógica de filtrado por fecha si es necesario
                    return true;
                })
                .collect(Collectors.toList());
    }

    public Optional<Pago> obtenerPagoPorReserva(String reservaId) {
        List<Pago> pagosReserva = jsonManagerReservaPago.buscarPagosPorReserva(reservaId);
        System.out.println("🔍 Buscando pago para reserva " + reservaId + ": " + 
                         (pagosReserva.isEmpty() ? "NO encontrado" : "ENCONTRADO"));
        return pagosReserva.isEmpty() ? Optional.empty() : Optional.of(pagosReserva.get(0));
    }

    public void diagnostico() {
        System.out.println("\n=== DIAGNÓSTICO COMPLETO DEL SERVICIO PAGOS ===");
        System.out.println("💰 Total pagos registrados: " + jsonManagerReservaPago.obtenerTodosPagos().size());
        System.out.println("📅 Total reservas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        
        List<Reserva> reservasConfirmadas = jsonManagerReservaPago.obtenerTodasReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
        
        long reservasSinPago = reservasConfirmadas.stream()
                .filter(r -> !existePagoParaReserva(r.getId()))
                .count();
        
        System.out.println("📊 Reservas confirmadas: " + reservasConfirmadas.size());
        System.out.println("⏳ Reservas confirmadas SIN pago: " + reservasSinPago);
        
        Map<String, Object> estadisticas = obtenerEstadisticasPagos();
        System.out.println("💵 Total recaudado: $" + estadisticas.get("totalRecaudado"));
        System.out.println("💳 Distribución por método: " + estadisticas.get("pagosPorMetodo"));
        
        // Obtener reservas pendientes de pago
        List<Map<String, Object>> pendientes = obtenerReservasPendientesPago();
        System.out.println("📋 Reservas pendientes de pago disponibles: " + pendientes.size());
    }
}