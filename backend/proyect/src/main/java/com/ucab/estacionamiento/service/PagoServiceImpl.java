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
    }

    public Pago registrarPago(Pago pagoRequest) {
        System.out.println("💰 Registrando pago para reserva: " + pagoRequest.getReservaId());
        
        // Validar que no exista ya un pago para esta reserva
        if (existePagoParaReserva(pagoRequest.getReservaId())) {
            throw new IllegalArgumentException("Ya existe un pago registrado para esta reserva");
        }

        // Validar monto positivo
        if (pagoRequest.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Verificar que la reserva existe
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(pagoRequest.getReservaId());
        if (reservaOpt.isEmpty()) {
            throw new IllegalArgumentException("La reserva especificada no existe");
        }

        // Crear nuevo pago usando el método de la clase fusionada
        String nuevoId = "PAY" + (jsonManagerReservaPago.obtenerTodosPagos().size() + 1);
        Pago nuevoPago = new Pago(nuevoId, pagoRequest.getReservaId(), 
                                pagoRequest.getClienteId(), pagoRequest.getMonto(),
                                pagoRequest.getMetodoPago(), pagoRequest.getReferencia());
        
        nuevoPago.setDescripcion(pagoRequest.getDescripcion());

        Pago pagoGuardado = jsonManagerReservaPago.guardarPago(nuevoPago);
        
        System.out.println("✅ Pago registrado exitosamente: " + nuevoId);
        System.out.println("📊 Monto: $" + pagoRequest.getMonto());
        System.out.println("💳 Método: " + pagoRequest.getMetodoPago());
        return pagoGuardado;
    }

    // Método simplificado para registrar pago desde parámetros individuales
    public Pago registrarPagoDesdeParametros(String reservaId, String clienteId, double monto, 
                                                    MetodoPago metodoPago, String referencia, String descripcion) {
        System.out.println("💰 Registrando pago simplificado para reserva: " + reservaId);
        
        // Validar que no exista ya un pago para esta reserva
        if (existePagoParaReserva(reservaId)) {
            throw new IllegalArgumentException("Ya existe un pago registrado para esta reserva");
        }

        // Validar monto positivo
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Verificar que la reserva existe
        Optional<Reserva> reservaOpt = jsonManagerReservaPago.buscarReservaPorId(reservaId);
        if (reservaOpt.isEmpty()) {
            throw new IllegalArgumentException("La reserva especificada no existe");
        }

        // Crear nuevo pago usando el método de la clase fusionada
        String nuevoId = "PAY" + (jsonManagerReservaPago.obtenerTodosPagos().size() + 1);
        Pago nuevoPago = new Pago(nuevoId, reservaId, clienteId, monto, metodoPago, referencia);
        nuevoPago.setDescripcion(descripcion);

        Pago pagoGuardado = jsonManagerReservaPago.guardarPago(nuevoPago);
        
        System.out.println("✅ Pago registrado exitosamente: " + nuevoId);
        System.out.println("📊 Monto: $" + monto);
        System.out.println("💳 Método: " + metodoPago);
        return pagoGuardado;
    }

    public Optional<Pago> obtenerPagoPorId(String id) {
        return jsonManagerReservaPago.buscarPagoPorId(id);
    }

    public List<Pago> obtenerPagosPorCliente(String clienteId) {
        return jsonManagerReservaPago.buscarPagosPorCliente(clienteId);
    }

    public List<Pago> obtenerTodosLosPagos() {
        return jsonManagerReservaPago.obtenerTodosPagos();
    }

    public boolean existePagoParaReserva(String reservaId) {
        return jsonManagerReservaPago.existePagoParaReserva(reservaId);
    }

    // Método para obtener reservas pendientes de pago
    public List<Map<String, Object>> obtenerReservasPendientesPago() {
        List<Reserva> reservas = jsonManagerReservaPago.obtenerTodasReservas();
        List<Cliente> clientes = jsonManagerCliente.obtenerTodosClientes();
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();

        System.out.println("🔍 Buscando reservas pendientes de pago...");
        System.out.println("📊 Total reservas: " + reservas.size());
        System.out.println("👥 Total clientes: " + clientes.size());
        System.out.println("🅿️  Total puestos: " + puestos.size());

        List<Map<String, Object>> reservasPendientes = reservas.stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(reserva -> !existePagoParaReserva(reserva.getId()))
                .map(reserva -> {
                    Map<String, Object> reservaInfo = new HashMap<>();
                    reservaInfo.put("id", reserva.getId());
                    reservaInfo.put("puestoId", reserva.getPuestoId());
                    reservaInfo.put("clienteId", reserva.getClienteId());
                    reservaInfo.put("usuario", reserva.getUsuario());
                    reservaInfo.put("fecha", reserva.getFecha());
                    reservaInfo.put("turno", reserva.getTurno());
                    reservaInfo.put("estado", reserva.getEstado().name());
                    reservaInfo.put("horaInicio", reserva.getHoraInicio());
                    reservaInfo.put("horaFin", reserva.getHoraFin());

                    // Calcular tarifa automáticamente
                    Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(reserva.getPuestoId());
                    if (puestoOpt.isPresent()) {
                        Puesto puesto = puestoOpt.get();
                        double tarifa = calcularTarifa(puesto.getTipoPuesto().name(), reserva.getTurno());
                        reservaInfo.put("tarifaCalculada", tarifa);
                    }

                    // Información del cliente
                    clientes.stream()
                            .filter(c -> c.getId().toString().equals(reserva.getClienteId()))
                            .findFirst()
                            .ifPresent(cliente -> {
                                Map<String, String> clienteInfo = new HashMap<>();
                                clienteInfo.put("nombre", cliente.getNombre());
                                clienteInfo.put("apellido", cliente.getApellido());
                                clienteInfo.put("cedula", cliente.getCedula());
                                clienteInfo.put("email", cliente.getEmail());
                                clienteInfo.put("tipoPersona", cliente.getTipoPersona());
                                reservaInfo.put("cliente", clienteInfo);
                            });

                    // Información del puesto
                    puestos.stream()
                            .filter(p -> p.getId().equals(reserva.getPuestoId()))
                            .findFirst()
                            .ifPresent(puesto -> {
                                Map<String, String> puestoInfo = new HashMap<>();
                                puestoInfo.put("numero", puesto.getNumero());
                                puestoInfo.put("ubicacion", puesto.getUbicacion());
                                puestoInfo.put("tipoPuesto", puesto.getTipoPuesto().name());
                                puestoInfo.put("estadoPuesto", puesto.getEstadoPuesto().name());
                                reservaInfo.put("puesto", puestoInfo);
                            });

                    return reservaInfo;
                })
                .collect(Collectors.toList());

        System.out.println("✅ Reservas pendientes de pago encontradas: " + reservasPendientes.size());
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

        double tarifa = tarifas.getOrDefault(tipoPuesto.toUpperCase(), tarifas.get("REGULAR"))
                     .getOrDefault(turno.toUpperCase(), 5.0);
        
        System.out.println("💰 Tarifa calculada: $" + tarifa);
        return tarifa;
    }

    // Métodos adicionales para estadísticas

    public Map<String, Object> obtenerEstadisticasPagos() {
        List<Pago> pagos = jsonManagerReservaPago.obtenerTodosPagos();
        
        double totalRecaudado = pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
        
        Map<String, Long> pagosPorMetodo = pagos.stream()
                .collect(Collectors.groupingBy(
                    pago -> pago.getMetodoPago().name(),
                    Collectors.counting()
                ));
        
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
                    return true; // Placeholder - ajustar según necesidad
                })
                .collect(Collectors.toList());
    }

    public Optional<Pago> obtenerPagoPorReserva(String reservaId) {
        List<Pago> pagosReserva = jsonManagerReservaPago.buscarPagosPorReserva(reservaId);
        return pagosReserva.isEmpty() ? Optional.empty() : Optional.of(pagosReserva.get(0));
    }

    public void diagnostico() {
        System.out.println("🩺 DIAGNÓSTICO DEL SERVICIO PAGOS");
        System.out.println("💰 Total pagos registrados: " + jsonManagerReservaPago.obtenerTodosPagos().size());
        System.out.println("📅 Total reservas: " + jsonManagerReservaPago.obtenerTodasReservas().size());
        
        List<Reserva> reservasConfirmadas = jsonManagerReservaPago.obtenerTodasReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .collect(Collectors.toList());
        
        long reservasSinPago = reservasConfirmadas.stream()
                .filter(r -> !existePagoParaReserva(r.getId()))
                .count();
        
        System.out.println("📊 Reservas confirmadas: " + reservasConfirmadas.size());
        System.out.println("⏳ Reservas pendientes de pago: " + reservasSinPago);
        
        Map<String, Object> estadisticas = obtenerEstadisticasPagos();
        System.out.println("💵 Total recaudado: $" + estadisticas.get("totalRecaudado"));
        System.out.println("💳 Distribución por método: " + estadisticas.get("pagosPorMetodo"));
    }
}