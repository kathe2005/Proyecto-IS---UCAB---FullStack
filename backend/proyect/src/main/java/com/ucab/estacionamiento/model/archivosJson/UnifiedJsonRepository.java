/*
package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UnifiedJsonRepository implements ClienteRepositoryPort {
    
    // Rutas de archivos
    private static final String CLIENTES_FILE = "../../data/clientes.json";
    private static final String PUESTOS_FILE = "../../data/puestos.json";
    private static final String RESERVAS_FILE = "../../data/reservas.json";
    private static final String PAGOS_FILE = "../../data/pagos.json";
    
    private final ObjectMapper objectMapper;
    
    // Almacenamiento en memoria
    private List<Cliente> BD_clientes;
    private List<Puesto> BD_puestos;
    private List<Reserva> BD_reservas;
    private List<Pago> BD_pagos;

    public UnifiedJsonRepository() {
        System.out.println("🔧 ===== INICIANDO UNIFIED JSON REPOSITORY =====");
        
        // Configurar ObjectMapper
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Mostrar directorio actual
        File currentDir = new File(".");
        System.out.println("📂 Directorio actual: " + currentDir.getAbsolutePath());
        
        // Cargar todos los datos
        this.BD_clientes = cargarClientesDesdeArchivo();
        this.BD_puestos = cargarPuestosDesdeArchivo();
        this.BD_reservas = cargarReservasDesdeArchivo();
        this.BD_pagos = cargarPagosDesdeArchivo();
        
        System.out.println("✅ Repository inicializado:");
        System.out.println("   👥 Clientes: " + BD_clientes.size());
        System.out.println("   🅿️  Puestos: " + BD_puestos.size());
        System.out.println("   📅 Reservas: " + BD_reservas.size());
        System.out.println("   💰 Pagos: " + BD_pagos.size());
        System.out.println("🔧 ===== FIN INICIALIZACIÓN =====");
    }

    // ========== MÉTODOS PARA CLIENTES ==========

    @Override
    public Cliente save(Cliente cliente) {
        System.out.println("💾 === GUARDANDO CLIENTE ===");
        System.out.println("👤 Cliente a guardar: " + cliente.getUsuario());
        
        try {
            // Asignar ID si no tiene
            if (cliente.getId() == null) {
                cliente.setId(UUID.randomUUID());
                System.out.println("🆕 ID asignado: " + cliente.getId());
            }

            // Verificar si ya existe por usuario
            Optional<Cliente> clienteExistente = findByUsuario(cliente.getUsuario());
            
            if (clienteExistente.isPresent()) {
                System.out.println("🔄 Cliente existe, actualizando...");
                BD_clientes.remove(clienteExistente.get());
            }

            // Agregar nuevo cliente
            BD_clientes.add(cliente);
            System.out.println("📊 Total clientes en memoria: " + BD_clientes.size());

            // Guardar en archivo
            guardarClientesEnArchivo();
            System.out.println("✅ CLIENTE GUARDADO EXITOSAMENTE");
            
        } catch (Exception e) {
            System.err.println("💥 ERROR guardando cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cliente;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return save(cliente);
    }

    @Override
    public List<Cliente> findAll() {
        return new ArrayList<>(BD_clientes);
    }

    @Override
    public Optional<Cliente> findByUsuario(String usuarioBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(usuarioBuscado))
                .findFirst();
    }

    @Override
    public Optional<Cliente> findByCedula(String cedulaBuscada) {
        return BD_clientes.stream()
                .filter(u -> u.getCedula().equalsIgnoreCase(cedulaBuscada))
                .findFirst();
    }

    @Override
    public Optional<Cliente> findByEmail(String emailBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailBuscado))
                .findFirst();
    }

    @Override
    public Optional<Cliente> findByTelefono(String telefonoBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getTelefono().equalsIgnoreCase(telefonoBuscado))
                .findFirst();
    }

    @Override
    public void clearAll() {
        BD_clientes.clear();
        guardarClientesEnArchivo();
    }

    @Override
    public void diagnostico() {
        System.out.println("🩺 === DIAGNÓSTICO DEL REPOSITORY ===");
        System.out.println("👥 Clientes: " + BD_clientes.size());
        System.out.println("🅿️  Puestos: " + BD_puestos.size());
        System.out.println("📅 Reservas: " + BD_reservas.size());
        System.out.println("💰 Pagos: " + BD_pagos.size());
        System.out.println("💻 Directorio actual: " + System.getProperty("user.dir"));
        System.out.println("🩺 === FIN DIAGNÓSTICO ===");
    }

    // ========== MÉTODOS PARA PUESTOS ==========

    public List<Puesto> obtenerTodosLosPuestos() {
        return new ArrayList<>(BD_puestos);
    }

    public Optional<Puesto> obtenerPuestoPorId(String id) {
        return BD_puestos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Puesto> obtenerPuestosPorEstado(EstadoPuesto estado) {
        return BD_puestos.stream()
                .filter(p -> p.getEstadoPuesto() == estado)
                .collect(Collectors.toList());
    }

    public List<Puesto> obtenerPuestosPorTipo(TipoPuesto tipo) {
        return BD_puestos.stream()
                .filter(p -> p.getTipoPuesto() == tipo)
                .collect(Collectors.toList());
    }

    public List<Puesto> filtrarPuestosPorUbicacion(String ubicacion) {
        return BD_puestos.stream()
                .filter(p -> p.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Puesto guardarPuesto(Puesto puesto) {
        System.out.println("💾 === GUARDANDO PUESTO ===");
        
        try {
            // Verificar si ya existe
            Optional<Puesto> puestoExistente = obtenerPuestoPorId(puesto.getId());
            
            if (puestoExistente.isPresent()) {
                System.out.println("🔄 Puesto existe, actualizando...");
                BD_puestos.remove(puestoExistente.get());
            }

            BD_puestos.add(puesto);
            guardarPuestosEnArchivo();
            System.out.println("✅ PUESTO GUARDADO EXITOSAMENTE: " + puesto.getId());
            
        } catch (Exception e) {
            System.err.println("💥 ERROR guardando puesto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return puesto;
    }

    public void actualizarEstadoPuesto(String puestoId, EstadoPuesto nuevoEstado) {
        obtenerPuestoPorId(puestoId).ifPresent(puesto -> {
            puesto.setEstadoPuesto(nuevoEstado);
            guardarPuestosEnArchivo();
        });
    }

    // ========== MÉTODOS PARA RESERVAS ==========

    public List<Reserva> obtenerTodasLasReservas() {
        return new ArrayList<>(BD_reservas);
    }

    public Optional<Reserva> obtenerReservaPorId(String id) {
        return BD_reservas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Reserva> obtenerReservasPorCliente(String clienteId) {
        return BD_reservas.stream()
                .filter(r -> r.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        return BD_reservas.stream()
                .filter(r -> r.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Reserva> obtenerReservasPendientes() {
        return BD_reservas.stream()
                .filter(r -> "PENDIENTE".equalsIgnoreCase(r.getEstado().name()))
                .collect(Collectors.toList());
    }

    public Reserva guardarReserva(Reserva reserva) {
        System.out.println("💾 === GUARDANDO RESERVA ===");
        
        try {
            // Asignar ID si no tiene
            if (reserva.getId() == null) {
                reserva.setId(UUID.randomUUID().toString());
            }

            // Verificar si ya existe
            Optional<Reserva> reservaExistente = obtenerReservaPorId(reserva.getId());
            
            if (reservaExistente.isPresent()) {
                System.out.println("🔄 Reserva existe, actualizando...");
                BD_reservas.remove(reservaExistente.get());
            }

            BD_reservas.add(reserva);
            guardarReservasEnArchivo();
            System.out.println("✅ RESERVA GUARDADA EXITOSAMENTE: " + reserva.getId());
            
        } catch (Exception e) {
            System.err.println("💥 ERROR guardando reserva: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reserva;
    }

    // ========== MÉTODOS PARA PAGOS ==========

    public List<Pago> obtenerTodosLosPagos() {
        return new ArrayList<>(BD_pagos);
    }

    public Optional<Pago> obtenerPagoPorId(String id) {
        return BD_pagos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Pago> obtenerPagosPorCliente(String clienteId) {
        return BD_pagos.stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    public Pago guardarPago(Pago pago) {
        System.out.println("💾 === GUARDANDO PAGO ===");
        
        try {
            // Asignar ID si no tiene
            if (pago.getId() == null) {
                pago.setId(UUID.randomUUID().toString());
            }

            // Verificar si ya existe
            Optional<Pago> pagoExistente = obtenerPagoPorId(pago.getId());
            
            if (pagoExistente.isPresent()) {
                System.out.println("🔄 Pago existe, actualizando...");
                BD_pagos.remove(pagoExistente.get());
            }

            BD_pagos.add(pago);
            guardarPagosEnArchivo();
            System.out.println("✅ PAGO GUARDADO EXITOSAMENTE: " + pago.getId());
            
        } catch (Exception e) {
            System.err.println("💥 ERROR guardando pago: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pago;
    }

    // ========== MÉTODOS DE CARGA DESDE ARCHIVOS ==========

    private List<Cliente> cargarClientesDesdeArchivo() {
        System.out.println("📥 === CARGANDO CLIENTES DESDE ARCHIVO ===");
        try {
            File archivo = new File(CLIENTES_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Archivo existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📝 Creando nuevo archivo de clientes...");
                archivo.getParentFile().mkdirs();
                boolean creado = archivo.createNewFile();
                System.out.println("📝 Archivo creado: " + creado);
                
                if (creado) {
                    objectMapper.writeValue(archivo, new ArrayList<Cliente>());
                    System.out.println("✅ Archivo inicializado con array vacío");
                }
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo de clientes vacío");
                return new ArrayList<>();
            }

            List<Cliente> clientes = objectMapper.readValue(archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Cliente.class));
            System.out.println("📥 " + clientes.size() + " clientes cargados desde archivo");
            return clientes;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR cargando clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Puesto> cargarPuestosDesdeArchivo() {
        System.out.println("📥 === CARGANDO PUESTOS DESDE ARCHIVO ===");
        try {
            File archivo = new File(PUESTOS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Archivo existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📁 Archivo de puestos no encontrado, creando datos iniciales...");
                return crearDatosInicialesPuestos();
            }

            if (archivo.length() == 0) {
                System.out.println("📁 Archivo de puestos vacío, creando datos iniciales...");
                return crearDatosInicialesPuestos();
            }

            List<Puesto> puestos = objectMapper.readValue(archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Puesto.class));
            System.out.println("📥 " + puestos.size() + " puestos cargados desde archivo");
            return puestos;

        } catch (Exception e) {
            System.err.println("❌ ERROR cargando puestos: " + e.getMessage());
            System.out.println("🔄 Creando datos iniciales debido al error...");
            return crearDatosInicialesPuestos();
        }
    }

    private List<Reserva> cargarReservasDesdeArchivo() {
        System.out.println("📥 === CARGANDO RESERVAS DESDE ARCHIVO ===");
        try {
            File archivo = new File(RESERVAS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Archivo existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📁 Archivo de reservas no encontrado, creando uno nuevo...");
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📁 Archivo de reservas vacío");
                return new ArrayList<>();
            }

            List<Reserva> reservas = objectMapper.readValue(archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Reserva.class));
            System.out.println("📥 " + reservas.size() + " reservas cargadas desde archivo");
            return reservas;

        } catch (Exception e) {
            System.err.println("❌ ERROR cargando reservas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Pago> cargarPagosDesdeArchivo() {
        System.out.println("📥 === CARGANDO PAGOS DESDE ARCHIVO ===");
        try {
            File archivo = new File(PAGOS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Archivo existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📁 Archivo de pagos no encontrado, creando uno nuevo...");
                archivo.getParentFile().mkdirs();
                archivo.createNewFile();
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📁 Archivo de pagos vacío");
                return new ArrayList<>();
            }

            List<Pago> pagos = objectMapper.readValue(archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Pago.class));
            System.out.println("📥 " + pagos.size() + " pagos cargados desde archivo");
            return pagos;

        } catch (Exception e) {
            System.err.println("❌ ERROR cargando pagos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ========== MÉTODOS DE GUARDADO EN ARCHIVOS ==========

    private void guardarClientesEnArchivo() {
        System.out.println("💾 === GUARDANDO CLIENTES EN ARCHIVO ===");
        try {
            File archivo = new File(CLIENTES_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, BD_clientes);
            System.out.println("✅ " + BD_clientes.size() + " clientes guardados");
        } catch (Exception e) {
            System.err.println("❌ ERROR guardando clientes: " + e.getMessage());
        }
    }

    private void guardarPuestosEnArchivo() {
        System.out.println("💾 === GUARDANDO PUESTOS EN ARCHIVO ===");
        try {
            File archivo = new File(PUESTOS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, BD_puestos);
            System.out.println("✅ " + BD_puestos.size() + " puestos guardados");
        } catch (Exception e) {
            System.err.println("❌ ERROR guardando puestos: " + e.getMessage());
        }
    }

    private void guardarReservasEnArchivo() {
        System.out.println("💾 === GUARDANDO RESERVAS EN ARCHIVO ===");
        try {
            File archivo = new File(RESERVAS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, BD_reservas);
            System.out.println("✅ " + BD_reservas.size() + " reservas guardadas");
        } catch (Exception e) {
            System.err.println("❌ ERROR guardando reservas: " + e.getMessage());
        }
    }

    private void guardarPagosEnArchivo() {
        System.out.println("💾 === GUARDANDO PAGOS EN ARCHIVO ===");
        try {
            File archivo = new File(PAGOS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, BD_pagos);
            System.out.println("✅ " + BD_pagos.size() + " pagos guardados");
        } catch (Exception e) {
            System.err.println("❌ ERROR guardando pagos: " + e.getMessage());
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private List<Puesto> crearDatosInicialesPuestos() {
        System.out.println("📝 Creando datos iniciales de puestos...");

        List<Puesto> puestosIniciales = Arrays.asList(
            new Puesto("P1", "A-01", TipoPuesto.REGULAR, EstadoPuesto.DISPONIBLE, "Zona A"),
            new Puesto("P2", "A-02", TipoPuesto.DOCENTE, EstadoPuesto.OCUPADO, "Zona A"),
            new Puesto("P3", "B-01", TipoPuesto.DISCAPACITADO, EstadoPuesto.DISPONIBLE, "Zona B"),
            new Puesto("P4", "C-01", TipoPuesto.VISITANTE, EstadoPuesto.BLOQUEADO, "Zona C"),
            new Puesto("P5", "M-01", TipoPuesto.MOTOCICLETA, EstadoPuesto.DISPONIBLE, "Zona Motos")
        );

        // Configurar puesto ocupado
        Puesto puestoOcupado = puestosIniciales.get(1);
        puestoOcupado.setUsuarioOcupante("profesor_garcia");
        puestoOcupado.setFechaOcupacion(LocalDateTime.now().minusHours(2));
        puestoOcupado.agregarRegistroHistorial("Ocupado por profesor_garcia en " + LocalDateTime.now().minusHours(2));

        guardarPuestosEnArchivo();
        System.out.println("✅ Datos iniciales de puestos creados: " + puestosIniciales.size() + " puestos");

        return new ArrayList<>(puestosIniciales);
    }

    public void mostrarArchivoJSON(String tipo) {
        try {
            String filePath;
            switch (tipo.toUpperCase()) {
                case "PUESTOS":
                    filePath = PUESTOS_FILE;
                    break;
                case "CLIENTES":
                    filePath = CLIENTES_FILE;
                    break;
                case "RESERVAS":
                    filePath = RESERVAS_FILE;
                    break;
                case "PAGOS":
                    filePath = PAGOS_FILE;
                    break;
                default:
                    System.err.println("❌ Tipo de archivo no válido: " + tipo);
                    return;
            }
            
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("❌ El archivo " + tipo + " no existe aún");
                return;
            }
            
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            System.out.println("\n📄 CONTENIDO DEL ARCHIVO " + tipo.toUpperCase() + ":");
            System.out.println("==================================");
            System.out.println(content);
            System.out.println("==================================");
            
        } catch (Exception e) {
            System.err.println("❌ Error leyendo archivo: " + e.getMessage());
        }
    }
}
*/