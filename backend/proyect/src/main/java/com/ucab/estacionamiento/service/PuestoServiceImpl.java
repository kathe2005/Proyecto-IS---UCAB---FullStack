package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PuestoServiceImpl {
    
    private final JsonManagerPuesto jsonManagerPuesto;

    public PuestoServiceImpl() {
        this.jsonManagerPuesto = new JsonManagerPuesto();
        System.out.println("🚀 PuestoServiceImpl inicializado con JsonManagerPuesto");
        System.out.println("✅ Puestos cargados: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        mostrarEstadisticasIniciales();
    }

    private void mostrarEstadisticasIniciales() {
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
        System.out.println("📊 Estadísticas iniciales de puestos:");
        for (EstadoPuesto estado : EstadoPuesto.values()) {
            long count = puestos.stream().filter(p -> p.getEstadoPuesto() == estado).count();
            System.out.println("   " + estado.getDescripcion() + ": " + count);
        }
        for (TipoPuesto tipo : TipoPuesto.values()) {
            long count = puestos.stream().filter(p -> p.getTipoPuesto() == tipo).count();
            System.out.println("   " + tipo.getDescripcion() + ": " + count);
        }
    }

    public List<Puesto> obtenerPuestos() {
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
        System.out.println("📋 Obteniendo " + puestos.size() + " puestos");
        return new ArrayList<>(puestos);
    }

    public Optional<Puesto> obtenerPuestoPorId(String idPuesto) {
        System.out.println("🔍 Buscando puesto por ID: " + idPuesto);
        Optional<Puesto> resultado = jsonManagerPuesto.buscarPuestoPorId(idPuesto);
        System.out.println("🔍 Resultado búsqueda: " + (resultado.isPresent() ? "Encontrado" : "No encontrado"));
        return resultado;
    }

    public Puesto crearPuesto(Puesto puesto) {
        System.out.println("🔧 Creando puesto: " + puesto.getNumero());
        
        // Validar número único
        boolean numeroExiste = jsonManagerPuesto.obtenerTodosPuestos().stream()
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

        Puesto puestoGuardado = jsonManagerPuesto.guardarPuesto(puesto);
        System.out.println("✅ Puesto creado exitosamente: " + puestoGuardado.getId());
        System.out.println("📍 Ubicación: " + puestoGuardado.getUbicacion());
        System.out.println("🎯 Tipo: " + puestoGuardado.getTipoPuesto().getDescripcion());
        return puestoGuardado;
    }

    private String generarNuevoId() {
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
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
                .max(Comparator.naturalOrder());
        
        return "P" + (maxId.orElse(0) + 1);
    }

    public Puesto actualizarPuesto(Puesto puesto) {
        System.out.println("🔄 Actualizando puesto: " + puesto.getId());
        
        Optional<Puesto> puestoExistente = jsonManagerPuesto.buscarPuestoPorId(puesto.getId());
        if (puestoExistente.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + puesto.getId());
        }

        // Agregar al historial
        puesto.agregarRegistroHistorial("Actualizado en " + LocalDateTime.now());
        
        Puesto puestoActualizado = jsonManagerPuesto.guardarPuesto(puesto);
        System.out.println("✅ Puesto actualizado: " + puestoActualizado.getId());
        return puestoActualizado;
    }

    public boolean eliminarPuesto(String id) {
        System.out.println("🗑️ Eliminando puesto: " + id);
        
        boolean eliminado = jsonManagerPuesto.eliminarPuesto(id);
        if (eliminado) {
            System.out.println("✅ Puesto eliminado: " + id);
        } else {
            System.out.println("❌ Puesto no encontrado para eliminar: " + id);
        }
        return eliminado;
    }

    public Puesto ocuparPuesto(String puestoId, String usuario) {
        System.out.println("🚗 Ocupando puesto: " + puestoId + " por usuario: " + usuario);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            System.err.println("❌ Puesto no encontrado: " + puestoId);
            throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
        }

        Puesto puesto = puestoOpt.get();

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            throw new IllegalArgumentException(mensaje);
        }

        // Usar el método de la clase fusionada
        puesto.ocuparPuesto(usuario, null, null);
        jsonManagerPuesto.guardarPuesto(puesto);

        String mensaje = String.format("Puesto %s ocupado por %s", puesto.getNumero(), usuario);
        System.out.println("✅ " + mensaje);
        return puesto;
    }

    public boolean liberarPuesto(String puestoId) {
        System.out.println("🔄 Liberando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);

        if (puestoOpt.isPresent() && puestoOpt.get().getEstadoPuesto() == EstadoPuesto.OCUPADO) {
            Puesto puesto = puestoOpt.get();
            puesto.liberarPuesto();
            jsonManagerPuesto.guardarPuesto(puesto);
            System.out.println("✅ Puesto liberado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo liberar puesto: " + puestoId);
        return false;
    }

    public List<Puesto> obtenerPuestosPorEstado(EstadoPuesto estado) {
        return jsonManagerPuesto.buscarPuestosPorEstado(estado);
    }

    public List<Puesto> obtenerPuestosPorTipo(TipoPuesto tipo) {
        return jsonManagerPuesto.buscarPuestosPorTipo(tipo);
    }

    public List<Puesto> filtrarPuestosPorUbicacion(String ubicacion) {
        return jsonManagerPuesto.filtrarPuestosPorUbicacion(ubicacion);
    }

    public int contarPuestosDisponibles() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.DISPONIBLE).size();
    }

    public int contarPuestosOcupados() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.OCUPADO).size();
    }

    public int contarPuestosReservados() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.RESERVADO).size();
    }

    public int contarPuestosBloqueados() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.BLOQUEADO).size();
    }

    public int contarPuestosMantenimiento() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.MANTENIMIENTO).size();
    }

    public List<Puesto> obtenerPuestosBloqueados() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.BLOQUEADO);
    }

    public List<Puesto> obtenerPuestosMantenimiento() {
        return jsonManagerPuesto.buscarPuestosPorEstado(EstadoPuesto.MANTENIMIENTO);
    }

    public boolean bloquearPuesto(String puestoId) {
        System.out.println("🔒 Bloqueando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.BLOQUEADO);
            puesto.setUsuarioOcupante("SISTEMA");
            
            String registroHistorial = String.format("Bloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            jsonManagerPuesto.guardarPuesto(puesto);
            System.out.println("✅ Puesto bloqueado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo bloquear puesto: " + puestoId);
        return false;
    }

    public boolean desbloquearPuesto(String puestoId) {
        System.out.println("🔓 Desbloqueando puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            puesto.setUsuarioOcupante(null);
            
            String registroHistorial = String.format("Desbloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            jsonManagerPuesto.guardarPuesto(puesto);
            System.out.println("✅ Puesto desbloqueado: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo desbloquear puesto: " + puestoId);
        return false;
    }

    public Puesto asignarPuestoManual(String puestoId, String usuario) {
        System.out.println("👨‍💼 Asignando manualmente puesto: " + puestoId + " a: " + usuario);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        
        if (puestoOpt.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
        }
        
        Puesto puesto = puestoOpt.get();
        
        if (puesto.getEstadoPuesto() == EstadoPuesto.BLOQUEADO || 
            puesto.getEstadoPuesto() == EstadoPuesto.MANTENIMIENTO) {
            String mensaje = String.format("Puesto no disponible para asignación. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            throw new IllegalArgumentException(mensaje);
        }
        
        // Usar el método de ocupación normal
        return ocuparPuesto(puestoId, usuario);
    }

    public Puesto reasignarPuesto(String puestoId, String nuevaUbicacion) {
        System.out.println("📍 Reasignando puesto: " + puestoId + " a: " + nuevaUbicacion);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            String ubicacionAnterior = puesto.getUbicacion();
            puesto.setUbicacion(nuevaUbicacion);
            
            String registroHistorial = String.format("Reasignado de '%s' a '%s' en %s", 
                    ubicacionAnterior, nuevaUbicacion, LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            Puesto puestoActualizado = jsonManagerPuesto.guardarPuesto(puesto);
            System.out.println("✅ Puesto reasignado: " + puestoId);
            System.out.println("📍 Nueva ubicación: " + nuevaUbicacion);
            return puestoActualizado;
        }
        throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
    }

    public boolean ponerPuestoEnMantenimiento(String puestoId) {
        System.out.println("🔧 Poniendo en mantenimiento puesto: " + puestoId);
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.MANTENIMIENTO);
            puesto.setUsuarioOcupante(null);
            puesto.setFechaOcupacion(null);
            
            String registroHistorial = String.format("Puesto en mantenimiento en %s", 
                    LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            jsonManagerPuesto.guardarPuesto(puesto);
            System.out.println("✅ Puesto en mantenimiento: " + puestoId);
            return true;
        }
        
        System.out.println("❌ No se pudo poner en mantenimiento: " + puestoId);
        return false;
    }

    public List<String> obtenerHistorial(String puestoId) {
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);
        
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            return puesto.getHistorialOcupacion() != null ? puesto.getHistorialOcupacion() : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public Puesto ocuparPuestoConCliente(String puestoId, String usuario, String clienteId, String tipoCliente) {
        System.out.println("🚗 Ocupando puesto: " + puestoId + " por usuario: " + usuario + " (Cliente: " + clienteId + ", Tipo: " + tipoCliente + ")");
        
        Optional<Puesto> puestoOpt = jsonManagerPuesto.buscarPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            System.err.println("❌ Puesto no encontrado: " + puestoId);
            throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
        }

        Puesto puesto = puestoOpt.get();

        // Validar tipo de cliente vs tipo de puesto
        if (!validarTipoClientePuesto(tipoCliente, puesto.getTipoPuesto())) {
            String mensaje = String.format("El tipo de cliente '%s' no puede ocupar un puesto de tipo '%s'", 
                    tipoCliente, puesto.getTipoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            throw new IllegalArgumentException(mensaje);
        }

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            System.err.println("❌ " + mensaje);
            throw new IllegalArgumentException(mensaje);
        }

        // Verificar si el cliente ya tiene un puesto activo
        boolean clienteConPuesto = jsonManagerPuesto.obtenerTodosPuestos().stream()
                .anyMatch(p -> p.getUsuarioOcupante() != null && 
                            p.getUsuarioOcupante().equals(usuario) && 
                            p.getEstadoPuesto() == EstadoPuesto.OCUPADO);
        
        if (clienteConPuesto) {
            System.err.println("❌ Cliente ya tiene puesto activo: " + usuario);
            throw new IllegalArgumentException("El cliente ya tiene un puesto activo");
        }

        // Usar el método de ocupación de la clase fusionada
        puesto.ocuparPuesto(usuario, clienteId, tipoCliente);
        jsonManagerPuesto.guardarPuesto(puesto);

        System.out.println("✅ Puesto ocupado: " + puesto.getNumero());
        System.out.println("👤 Cliente ID: " + clienteId);
        System.out.println("🎯 Tipo Cliente: " + tipoCliente);
        return puesto;
    }

    // Método para validar compatibilidad entre tipo de cliente y tipo de puesto
    private boolean validarTipoClientePuesto(String tipoCliente, TipoPuesto tipoPuesto) {
        if ("UCAB".equalsIgnoreCase(tipoCliente)) {
            // UCAB puede ocupar REGULAR, DOCENTE, DISCAPACITADO, MOTOCICLETA
            return tipoPuesto == TipoPuesto.REGULAR || 
                   tipoPuesto == TipoPuesto.DOCENTE || 
                   tipoPuesto == TipoPuesto.DISCAPACITADO ||
                   tipoPuesto == TipoPuesto.MOTOCICLETA;
        } else if ("VISITANTE".equalsIgnoreCase(tipoCliente)) {
            // Visitante solo puede ocupar REGULAR y VISITANTE
            return tipoPuesto == TipoPuesto.REGULAR || 
                   tipoPuesto == TipoPuesto.VISITANTE;
        }
        return false;
    }

    // Métodos adicionales para estadísticas y reporting

    public Map<String, Object> obtenerEstadisticasCompletas() {
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalPuestos", puestos.size());
        
        // Estadísticas por estado
        Map<String, Integer> porEstado = new HashMap<>();
        for (EstadoPuesto estado : EstadoPuesto.values()) {
            int count = (int) puestos.stream().filter(p -> p.getEstadoPuesto() == estado).count();
            porEstado.put(estado.name(), count);
        }
        estadisticas.put("puestosPorEstado", porEstado);
        
        // Estadísticas por tipo
        Map<String, Integer> porTipo = new HashMap<>();
        for (TipoPuesto tipo : TipoPuesto.values()) {
            int count = (int) puestos.stream().filter(p -> p.getTipoPuesto() == tipo).count();
            porTipo.put(tipo.name(), count);
        }
        estadisticas.put("puestosPorTipo", porTipo);
        
        // Estadísticas por ubicación
        Map<String, Long> porUbicacion = puestos.stream()
                .collect(Collectors.groupingBy(Puesto::getUbicacion, Collectors.counting()));
        estadisticas.put("puestosPorUbicacion", porUbicacion);
        
        estadisticas.put("fechaGeneracion", LocalDateTime.now());
        
        return estadisticas;
    }

    public void diagnostico() {
        System.out.println("🩺 DIAGNÓSTICO DEL SERVICIO PUESTOS");
        
        Map<String, Object> estadisticas = obtenerEstadisticasCompletas();
        System.out.println("📊 Estadísticas completas:");
        System.out.println("   Total puestos: " + estadisticas.get("totalPuestos"));
        System.out.println("   Por estado: " + estadisticas.get("puestosPorEstado"));
        System.out.println("   Por tipo: " + estadisticas.get("puestosPorTipo"));
    }
}