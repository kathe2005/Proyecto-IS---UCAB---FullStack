package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.model.interfaces.PuestoService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PuestoServiceImpl implements PuestoService {
    private List<Puesto> puestos;

    public PuestoServiceImpl() {
        System.out.println("🚀 Inicializando PuestoServiceImpl...");
        try {
            this.puestos = JsonManager.cargarPuestos();
            if (this.puestos == null) {
                System.err.println("❌ ERROR: Lista de puestos es null, inicializando lista vacía");
                this.puestos = new ArrayList<>();
            }
            System.out.println("✅ Servicio Spring inicializado con " + puestos.size() + " puestos");
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico en inicialización: " + e.getMessage());
            e.printStackTrace();
            this.puestos = new ArrayList<>();
        }
    }

    private void guardarCambios() {
        System.out.println("💾 Ejecutando guardarCambios() - " + puestos.size() + " puestos");
        JsonManager.guardarPuestos(puestos);
    }

    @Override
    public List<Puesto> obtenerPuestos() {
        if (puestos == null) {
            puestos = new ArrayList<>();
        }
        System.out.println("📋 Obteniendo " + puestos.size() + " puestos");
        return new ArrayList<>(puestos);
    }

    @Override
    public Optional<Puesto> obtenerPuestoPorId(String idPuesto) {
        System.out.println("🔍 Buscando puesto por ID: " + idPuesto);
        Optional<Puesto> resultado = puestos.stream()
                .filter(p -> p.getId().equals(idPuesto))
                .findFirst();
        System.out.println("🔍 Resultado búsqueda: " + (resultado.isPresent() ? "Encontrado" : "No encontrado"));
        return resultado;
    }

    @Override
    public Puesto crearPuesto(Puesto puesto) {
        System.out.println("🔧 Creando puesto: " + puesto.getNumero());
        
        // Validar número único
        boolean numeroExiste = puestos.stream()
                .anyMatch(p -> p.getNumero().equals(puesto.getNumero()));

        if (numeroExiste) {
            System.err.println("❌ Número de puesto ya existe: " + puesto.getNumero());
            throw new IllegalArgumentException("El número de puesto ya existe: " + puesto.getNumero());
        }

        // Generar ID si no existe
        if (puesto.getId() == null || puesto.getId().trim().isEmpty()) {
            String nuevoId = generarNuevoId();
            puesto.setId(nuevoId);
            System.out.println("🆔 ID generado: " + nuevoId);
        }

        // Configurar fechas
        if (puesto.getFechaCreacion() == null) {
            puesto.setFechaCreacion(LocalDateTime.now());
        }

        // Inicializar historial si es necesario
        if (puesto.getHistorialOcupacion() == null) {
            puesto.setHistorialOcupacion(new ArrayList<>());
        }

        // Agregar registro inicial al historial
        puesto.agregarRegistroHistorial("Creado en " + LocalDateTime.now());

        puestos.add(puesto);
        
        System.out.println("💾 Guardando cambios...");
        guardarCambios();
        
        System.out.println("✅ Puesto creado exitosamente: " + puesto.getId());
        return puesto;
    }

    private String generarNuevoId() {
        // CORREGIDO: Usar Comparator.naturalOrder() para evitar problemas de type safety
        Optional<Integer> maxId = puestos.stream()
                .map(p -> {
                    if (p.getId() != null && p.getId().startsWith("P")) {
                        try {
                            return Integer.parseInt(p.getId().substring(1));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Comparator.naturalOrder()); // CORREGIDO AQUÍ
        
        return "P" + (maxId.orElse(0) + 1);
    }

    // ... (el resto de los métodos se mantienen igual)

    @Override
    public Puesto actualizarPuesto(Puesto puesto) {
        System.out.println("🔄 Actualizando puesto: " + puesto.getId());
        
        Optional<Puesto> puestoExistente = obtenerPuestoPorId(puesto.getId());
        if (puestoExistente.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + puesto.getId());
        }

        int index = puestos.indexOf(puestoExistente.get());
        puestos.set(index, puesto);
        
        // Agregar al historial
        puesto.agregarRegistroHistorial("Actualizado en " + LocalDateTime.now());
        
        guardarCambios();
        System.out.println("✅ Puesto actualizado: " + puesto.getId());
        return puesto;
    }

    @Override
    public boolean eliminarPuesto(String id) {
        System.out.println("🗑️ Eliminando puesto: " + id);
        
        Optional<Puesto> puesto = obtenerPuestoPorId(id);
        if (puesto.isPresent()) {
            puestos.remove(puesto.get());
            guardarCambios();
            System.out.println("✅ Puesto eliminado: " + id);
            return true;
        }
        
        System.out.println("❌ Puesto no encontrado para eliminar: " + id);
        return false;
    }

    @Override
    public ResultadoOcupacion ocuparPuesto(String puestoId, String usuario) {
        System.out.println("🚗 Ocupando puesto: " + puestoId + " por usuario: " + usuario);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            System.err.println("❌ Puesto no encontrado: " + puestoId);
            return new ResultadoOcupacion(false, "Puesto no encontrado", null, "PUESTO_NO_ENCONTRADO");
        }

        Puesto puesto = puestoOpt.get();

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            return new ResultadoOcupacion(false, mensaje, puesto, "PUESTO_NO_DISPONIBLE");
        }

        puesto.setEstadoPuesto(EstadoPuesto.OCUPADO);
        puesto.setUsuarioOcupante(usuario);
        puesto.setFechaOcupacion(LocalDateTime.now());

        String registroHistorial = String.format("Ocupado por %s en %s", usuario, LocalDateTime.now());
        puesto.agregarRegistroHistorial(registroHistorial);

        guardarCambios();

        String mensaje = String.format("Puesto %s ocupado por %s", puesto.getNumero(), usuario);
        System.out.println("✅ " + mensaje);
        return new ResultadoOcupacion(true, mensaje, puesto);
    }

    @Override
    public boolean liberarPuesto(String puestoId) {
        System.out.println("🔄 Liberando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isPresent() && puestoOpt.get().getEstadoPuesto() == EstadoPuesto.OCUPADO) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            puesto.setUsuarioOcupante(null);
            puesto.setFechaOcupacion(null);

            String registroHistorial = String.format("Liberado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);

            guardarCambios();
            System.out.println("✅ Puesto liberado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo liberar puesto: " + puestoId);
        return false;
    }

    // Resto de métodos implementados...
    @Override
    public List<Puesto> obtenerPuestosPorEstado(EstadoPuesto estado) {
        return puestos.stream()
                .filter(p -> p.getEstadoPuesto() == estado)
                .collect(Collectors.toList());
    }

    @Override
    public List<Puesto> obtenerPuestosPorTipo(TipoPuesto tipo) {
        return puestos.stream()
                .filter(p -> p.getTipoPuesto() == tipo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Puesto> filtrarPuestosPorUbicacion(String ubicacion) {
        return puestos.stream()
                .filter(p -> p.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public int contarPuestosDisponibles() {
        return (int) puestos.stream()
                .filter(p -> p.getEstadoPuesto() == EstadoPuesto.DISPONIBLE)
                .count();
    }

    @Override
    public int contarPuestosOcupados() {
        return (int) puestos.stream()
                .filter(p -> p.getEstadoPuesto() == EstadoPuesto.OCUPADO)
                .count();
    }

    @Override
    public int contarPuestosReservados() {
        return (int) puestos.stream()
                .filter(p -> p.getEstadoPuesto() == EstadoPuesto.RESERVADO)
                .count();
    }

    @Override
    public int contarPuestosBloqueados() {
        return (int) puestos.stream()
                .filter(p -> p.getEstadoPuesto() == EstadoPuesto.BLOQUEADO)
                .count();
    }

    @Override
    public List<Puesto> obtenerPuestosBloqueados() {
        return obtenerPuestosPorEstado(EstadoPuesto.BLOQUEADO);
    }

    @Override
    public boolean bloquearPuesto(String puestoId) {
        System.out.println("🔒 Bloqueando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.BLOQUEADO);
            puesto.setUsuarioOcupante(null);
            
            String registroHistorial = String.format("Bloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            System.out.println("✅ Puesto bloqueado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo bloquear puesto: " + puestoId);
        return false;
    }

    @Override
    public boolean desbloquearPuesto(String puestoId) {
        System.out.println("🔓 Desbloqueando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            
            String registroHistorial = String.format("Desbloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            System.out.println("✅ Puesto desbloqueado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo desbloquear puesto: " + puestoId);
        return false;
    }

    @Override
    public ResultadoOcupacion asignarPuestoManual(String puestoId, String usuario) {
        System.out.println("👨‍💼 Asignando manualmente puesto: " + puestoId + " a: " + usuario);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        
        if (puestoOpt.isEmpty()) {
            return new ResultadoOcupacion(false, "Puesto no encontrado", null, "PUESTO_NO_ENCONTRADO");
        }
        
        Puesto puesto = puestoOpt.get();
        
        if (puesto.getEstadoPuesto() == EstadoPuesto.BLOQUEADO || 
            puesto.getEstadoPuesto() == EstadoPuesto.MANTENIMIENTO) {
            String mensaje = String.format("Puesto no disponible para asignación. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            return new ResultadoOcupacion(false, mensaje, puesto, "PUESTO_NO_DISPONIBLE");
        }
        
        puesto.setEstadoPuesto(EstadoPuesto.OCUPADO);
        puesto.setUsuarioOcupante(usuario);
        puesto.setFechaOcupacion(LocalDateTime.now());
        
        String registroHistorial = String.format("Asignado manualmente a %s en %s", 
                usuario, LocalDateTime.now());
        puesto.agregarRegistroHistorial(registroHistorial);
        
        guardarCambios();
        
        String mensaje = String.format("Puesto %s asignado manualmente a %s", puesto.getNumero(), usuario);
        System.out.println("✅ " + mensaje);
        return new ResultadoOcupacion(true, mensaje, puesto);
    }

    @Override
    public Puesto reasignarPuesto(String puestoId, String nuevaUbicacion) {
        System.out.println("📍 Reasignando puesto: " + puestoId + " a: " + nuevaUbicacion);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            String ubicacionAnterior = puesto.getUbicacion();
            puesto.setUbicacion(nuevaUbicacion);
            
            String registroHistorial = String.format("Reasignado de '%s' a '%s' en %s", 
                    ubicacionAnterior, nuevaUbicacion, LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            System.out.println("✅ Puesto reasignado: " + puestoId);
            return puesto;
        }
        throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
    }

    @Override
    public boolean ponerPuestoEnMantenimiento(String puestoId) {
        System.out.println("🔧 Poniendo en mantenimiento puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.MANTENIMIENTO);
            puesto.setUsuarioOcupante(null);
            puesto.setFechaOcupacion(null);
            
            String registroHistorial = String.format("Puesto en mantenimiento en %s", 
                    LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            System.out.println("✅ Puesto en mantenimiento: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo poner en mantenimiento: " + puestoId);
        return false;
    }

    @Override
    public List<String> obtenerHistorial(String puestoId) {
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            return puesto.getHistorialOcupacion() != null ? puesto.getHistorialOcupacion() : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public ResultadoOcupacion ocuparPuesto(String puestoId, String usuario, String clienteId, String tipoCliente) {
        System.out.println("🚗 Ocupando puesto: " + puestoId + " por usuario: " + usuario + " (Cliente: " + clienteId + ", Tipo: " + tipoCliente + ")");
        
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            System.err.println("❌ Puesto no encontrado: " + puestoId);
            return new ResultadoOcupacion(false, "Puesto no encontrado", null, "PUESTO_NO_ENCONTRADO");
        }

        Puesto puesto = puestoOpt.get();

        // Validar tipo de cliente vs tipo de puesto
        if (!validarTipoClientePuesto(tipoCliente, puesto.getTipoPuesto())) {
            String mensaje = String.format("El tipo de cliente '%s' no puede ocupar un puesto de tipo '%s'", 
                    tipoCliente, puesto.getTipoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            return new ResultadoOcupacion(false, mensaje, puesto, "TIPO_CLIENTE_NO_VALIDO");
        }

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            return new ResultadoOcupacion(false, mensaje, puesto, "PUESTO_NO_DISPONIBLE");
        }

        // Verificar si el cliente ya tiene un puesto activo
        boolean clienteConPuesto = puestos.stream()
                .anyMatch(p -> p.getUsuarioOcupante() != null && 
                            p.getUsuarioOcupante().equals(usuario) && 
                            p.getEstadoPuesto() == EstadoPuesto.OCUPADO);
        
        if (clienteConPuesto) {
            System.err.println("❌ Cliente ya tiene puesto activo: " + usuario);
            return new ResultadoOcupacion(false, "El cliente ya tiene un puesto activo", null, "CLIENTE_CON_PUESTO_ACTIVO");
        }

        // Ocupar el puesto
        puesto.setEstadoPuesto(EstadoPuesto.OCUPADO);
        puesto.setUsuarioOcupante(usuario);
        puesto.setFechaOcupacion(LocalDateTime.now());

        String registroHistorial = String.format("Ocupado por %s (Cliente ID: %s, Tipo: %s) en %s", 
                usuario, clienteId, tipoCliente, LocalDateTime.now());
        puesto.agregarRegistroHistorial(registroHistorial);

        guardarCambios();

        String mensaje = String.format("Puesto %s ocupado por %s", puesto.getNumero(), usuario);
        System.out.println("✅ " + mensaje);
        return new ResultadoOcupacion(true, mensaje, puesto);
    }

    // Método para validar compatibilidad entre tipo de cliente y tipo de puesto
    private boolean validarTipoClientePuesto(String tipoCliente, TipoPuesto tipoPuesto) {
        if ("UCAB".equalsIgnoreCase(tipoCliente)) {
            // UCAB puede ocupar REGULAR, DOCENTE, DISCAPACITADO
            return tipoPuesto == TipoPuesto.REGULAR || 
                tipoPuesto == TipoPuesto.DOCENTE || 
                tipoPuesto == TipoPuesto.DISCAPACITADO;
        } else if ("VISITANTE".equalsIgnoreCase(tipoCliente)) {
            // Visitante solo puede ocupar REGULAR y VISITANTE
            return tipoPuesto == TipoPuesto.REGULAR || 
                tipoPuesto == TipoPuesto.VISITANTE;
        }
        return false;
    }
}