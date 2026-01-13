package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Reserva;
import com.ucab.estacionamiento.model.clases.Pago;
import com.ucab.estacionamiento.model.enums.EstadoReserva;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonManagerReservaPago {
    private static final String RESERVAS_FILE = ConfigurationManager.getDataFilePath("reservas.json");
    private static final String PAGOS_FILE = ConfigurationManager.getDataFilePath("pagos.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ========== OPERACIONES RESERVAS ==========

    public Reserva guardarReserva(Reserva reserva) {
        System.out.println("💾 Guardando reserva: " + reserva.getId());
        
        try {
            List<Reserva> reservas = cargarReservas();
            
            // Asignar ID si no existe
            if (reserva.getId() == null || reserva.getId().trim().isEmpty()) {
                String nuevoId = "R" + (reservas.size() + 1);
                reserva.setId(nuevoId);
            }
            
            // Verificar si ya existe
            Optional<Reserva> reservaExistente = reservas.stream()
                    .filter(r -> r.getId().equals(reserva.getId()))
                    .findFirst();
            
            if (reservaExistente.isPresent()) {
                System.out.println("🔄 Reserva existe, actualizando...");
                reservas.remove(reservaExistente.get());
            }

            reservas.add(reserva);
            guardarReservasEnArchivo(reservas);
            System.out.println("✅ Reserva guardada exitosamente: " + reserva.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Error guardando reserva: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reserva;
    }

    public List<Reserva> cargarReservas() {
        System.out.println("📥 Cargando reservas desde archivo...");
        try {
            File archivo = new File(RESERVAS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📝 Archivo no encontrado, creando uno nuevo...");
                archivo.getParentFile().mkdirs();
                guardarReservasEnArchivo(new ArrayList<>());
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo vacío");
                return new ArrayList<>();
            }

            List<Reserva> reservas = objectMapper.readValue(
                archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Reserva.class)
            );
            
            System.out.println("✅ " + reservas.size() + " reservas cargadas");
            return reservas;
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando reservas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA RESERVAS ==========

    public Optional<Reserva> buscarReservaPorId(String id) {
        List<Reserva> reservas = cargarReservas();
        return reservas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Reserva> buscarReservasPorCliente(String clienteId) {
        List<Reserva> reservas = cargarReservas();
        return reservas.stream()
                .filter(r -> r.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public List<Reserva> buscarReservasPorFecha(LocalDate fecha) {
        List<Reserva> reservas = cargarReservas();
        return reservas.stream()
                .filter(r -> r.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Reserva> buscarReservasPendientes() {
        List<Reserva> reservas = cargarReservas();
        return reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.PENDIENTE)
                .collect(Collectors.toList());
    }

    public List<Reserva> obtenerTodasReservas() {
        return cargarReservas();
    }

    public boolean eliminarReserva(String id) {
        try {
            List<Reserva> reservas = cargarReservas();
            boolean eliminado = reservas.removeIf(r -> r.getId().equals(id));
            if (eliminado) {
                guardarReservasEnArchivo(reservas);
                System.out.println("✅ Reserva eliminada: " + id);
            } else {
                System.out.println("❌ Reserva no encontrada: " + id);
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("❌ Error eliminando reserva: " + e.getMessage());
            return false;
        }
    }

    // ========== OPERACIONES PAGOS ==========

    public Pago guardarPago(Pago pago) {
        System.out.println("💾 Guardando pago: " + pago.getId());
        
        try {
            List<Pago> pagos = cargarPagos();
            
            // Asignar ID si no existe
            if (pago.getId() == null || pago.getId().trim().isEmpty()) {
                String nuevoId = "PAY" + (pagos.size() + 1);
                pago.setId(nuevoId);
            }
            
            // Verificar si ya existe
            Optional<Pago> pagoExistente = pagos.stream()
                    .filter(p -> p.getId().equals(pago.getId()))
                    .findFirst();
            
            if (pagoExistente.isPresent()) {
                System.out.println("🔄 Pago existe, actualizando...");
                pagos.remove(pagoExistente.get());
            }

            pagos.add(pago);
            guardarPagosEnArchivo(pagos);
            System.out.println("✅ Pago guardado exitosamente: " + pago.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Error guardando pago: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pago;
    }

    public List<Pago> cargarPagos() {
        System.out.println("📥 Cargando pagos desde archivo...");
        try {
            File archivo = new File(PAGOS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📝 Archivo no encontrado, creando uno nuevo...");
                archivo.getParentFile().mkdirs();
                guardarPagosEnArchivo(new ArrayList<>());
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo vacío");
                return new ArrayList<>();
            }

            List<Pago> pagos = objectMapper.readValue(
                archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Pago.class)
            );
            
            System.out.println("✅ " + pagos.size() + " pagos cargados");
            return pagos;
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando pagos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA PAGOS ==========

    public Optional<Pago> buscarPagoPorId(String id) {
        List<Pago> pagos = cargarPagos();
        return pagos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Pago> buscarPagosPorCliente(String clienteId) {
        List<Pago> pagos = cargarPagos();
        return pagos.stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public List<Pago> buscarPagosPorReserva(String reservaId) {
        List<Pago> pagos = cargarPagos();
        return pagos.stream()
                .filter(p -> p.getReservaId().equals(reservaId))
                .collect(Collectors.toList());
    }

    public List<Pago> obtenerTodosPagos() {
        return cargarPagos();
    }

    public boolean eliminarPago(String id) {
        try {
            List<Pago> pagos = cargarPagos();
            boolean eliminado = pagos.removeIf(p -> p.getId().equals(id));
            if (eliminado) {
                guardarPagosEnArchivo(pagos);
                System.out.println("✅ Pago eliminado: " + id);
            } else {
                System.out.println("❌ Pago no encontrado: " + id);
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("❌ Error eliminando pago: " + e.getMessage());
            return false;
        }
    }

    public boolean existePagoParaReserva(String reservaId) {
        List<Pago> pagos = cargarPagos();
        return pagos.stream()
                .anyMatch(p -> p.getReservaId().equals(reservaId));
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void guardarReservasEnArchivo(List<Reserva> reservas) {
        try {
            File archivo = new File(RESERVAS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, reservas);
            System.out.println("💾 " + reservas.size() + " reservas guardadas en archivo: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Error guardando reservas en archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardarPagosEnArchivo(List<Pago> pagos) {
        try {
            File archivo = new File(PAGOS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, pagos);
            System.out.println("💾 " + pagos.size() + " pagos guardados en archivo: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Error guardando pagos en archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void diagnostico() {
        File archivoReservas = new File(RESERVAS_FILE);
        File archivoPagos = new File(PAGOS_FILE);
        
        System.out.println("🩺 DIAGNÓSTICO RESERVAS Y PAGOS:");
        System.out.println("📁 Ruta reservas: " + archivoReservas.getAbsolutePath());
        System.out.println("🔍 Existe reservas: " + archivoReservas.exists());
        System.out.println("📏 Tamaño reservas: " + (archivoReservas.exists() ? archivoReservas.length() + " bytes" : "N/A"));
        System.out.println("📅 Reservas en archivo: " + cargarReservas().size());
        
        System.out.println("📁 Ruta pagos: " + archivoPagos.getAbsolutePath());
        System.out.println("🔍 Existe pagos: " + archivoPagos.exists());
        System.out.println("📏 Tamaño pagos: " + (archivoPagos.exists() ? archivoPagos.length() + " bytes" : "N/A"));
        System.out.println("💰 Pagos en archivo: " + cargarPagos().size());
    }
}