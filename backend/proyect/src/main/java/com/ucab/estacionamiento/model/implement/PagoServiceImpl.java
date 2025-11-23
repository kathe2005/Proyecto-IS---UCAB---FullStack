package com.ucab.estacionamiento.model.implement;

import com.ucab.estacionamiento.model.archivosJson.ClienteRepository;
import com.ucab.estacionamiento.model.archivosJson.JsonManager;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerPago;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerReserva;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.clases.Pago;
import com.ucab.estacionamiento.model.clases.PagoRequest;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.clases.Reserva;
import com.ucab.estacionamiento.model.enums.EstadoReserva;
import com.ucab.estacionamiento.model.interfaces.PagoService;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {
    
    private List<Pago> pagos;
    private final JsonManagerPago jsonManagerPago;

    public PagoServiceImpl() {
        this.jsonManagerPago = new JsonManagerPago();
        this.pagos = jsonManagerPago.cargarPagos();
        System.out.println("✅ PagoServiceImpl inicializado con " + pagos.size() + " pagos");
    }

    private void guardarCambios() {
        jsonManagerPago.guardarPagos(pagos);
    }

    @Override
    public Pago registrarPago(PagoRequest pagoRequest) {
        System.out.println("💰 Registrando pago para reserva: " + pagoRequest.getReservaId());
        
        // Validar que no exista ya un pago para esta reserva
        if (existePagoParaReserva(pagoRequest.getReservaId())) {
            throw new IllegalArgumentException("Ya existe un pago registrado para esta reserva");
        }

        // Validar monto positivo
        if (pagoRequest.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }

        // Crear nuevo pago
        String nuevoId = "PAY" + (pagos.size() + 1);
        Pago nuevoPago = new Pago(nuevoId, pagoRequest.getReservaId(), 
                                pagoRequest.getClienteId(), pagoRequest.getMonto(),
                                pagoRequest.getMetodoPago(), pagoRequest.getReferencia());
        
        nuevoPago.setDescripcion(pagoRequest.getDescripcion());

        pagos.add(nuevoPago);
        guardarCambios();
        
        System.out.println("✅ Pago registrado exitosamente: " + nuevoId);
        return nuevoPago;
    }

    @Override
    public Optional<Pago> obtenerPagoPorId(String id) {
        return pagos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Pago> obtenerPagosPorCliente(String clienteId) {
        return pagos.stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pago> obtenerTodosLosPagos() {
        return new ArrayList<>(pagos);
    }

    @Override
    public boolean existePagoParaReserva(String reservaId) {
        return pagos.stream()
                .anyMatch(p -> p.getReservaId().equals(reservaId));
    }

    // Método para obtener reservas pendientes de pago
    public List<Map<String, Object>> obtenerReservasPendientesPago() {
        // Cargar reservas
        List<Reserva> reservas = new JsonManagerReserva().cargarReservas();
        // Cargar clientes
        List<Cliente> clientes = new ClienteRepository().findAll();
        // Cargar puestos
        List<Puesto> puestos = JsonManager.cargarPuestos();

        return reservas.stream()
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

                    // Información del cliente
                    clientes.stream()
                            .filter(c -> c.getId().toString().equals(reserva.getClienteId()))
                            .findFirst()
                            .ifPresent(cliente -> {
                                Map<String, String> clienteInfo = new HashMap<>();
                                clienteInfo.put("nombre", cliente.getNombre());
                                clienteInfo.put("apellido", cliente.getApellido());
                                clienteInfo.put("cedula", cliente.getCedula());
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
                                reservaInfo.put("puesto", puestoInfo);
                            });

                    return reservaInfo;
                })
                .collect(Collectors.toList());
    }

    // Método para calcular tarifas
    public double calcularTarifa(String tipoPuesto, String turno) {
        // Tarifas base (puedes ajustar estos valores)
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

        return tarifas.getOrDefault(tipoPuesto, tarifas.get("REGULAR"))
                     .getOrDefault(turno, 5.0);
    }
}