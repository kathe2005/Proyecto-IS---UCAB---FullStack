package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PuestoServiceImpl implements PuestoService {
    private List<Puesto> puestos;
    private final JsonManager jsonManager;

    @Autowired
    public PuestoServiceImpl(JsonManager jsonManager) {
        this.jsonManager = jsonManager; // Guarda la instancia
        this.puestos = jsonManager.cargarPuestos(); // Usa la instancia (¡sin 'static'!)
        
        if (this.puestos == null) {
            System.err.println("❌ ERROR: Lista de puestos es null, inicializando lista vacía");
            this.puestos = new ArrayList<>();
        }
        System.out.println("🚀 Servicio Spring inicializado con " + puestos.size() + " puestos");
    }

    // --- MODIFICADO ---
    private void guardarCambios() {
        jsonManager.guardarPuestos(puestos); // Usa la instancia (¡sin 'static'!)
    }


    @Override
    public List<Puesto> obtenerPuestos() {
        if (puestos == null) {
            puestos = new ArrayList<>();
        }
        return new ArrayList<>(puestos);
    }

    @Override
    public Optional<Puesto> obtenerPuestoPorId(String idPuesto) {
        return puestos.stream()
                .filter(p -> p.getId().equals(idPuesto))
                .findFirst();
    }

@Override
    public Puesto crearPuesto(Puesto puesto) throws IllegalArgumentException {
        boolean numeroExiste = puestos.stream()
                .anyMatch(p -> p.getNumero().equals(puesto.getNumero()));
        if (numeroExiste) {
            throw new IllegalArgumentException("El número de puesto '" + puesto.getNumero() + "' ya existe.");
        }
        puesto.setId(UUID.randomUUID().toString());
        puesto.setFechaCreacion(LocalDateTime.now());
        puesto.agregarRegistroHistorial("Puesto creado en " + puesto.getFechaCreacion());
        this.puestos.add(puesto);
        guardarCambios();
        return puesto;
    }

    private String generarNuevoId() {
        int maxId = puestos.stream()
                .map(p -> p.getId().substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return "P" + (maxId + 1);
    }

    @Override
    public Puesto actualizarPuesto(Puesto puesto) {
        Optional<Puesto> puestoExistente = obtenerPuestoPorId(puesto.getId());
        if (puestoExistente.isEmpty()) {
            throw new IllegalArgumentException("Puesto no encontrado: " + puesto.getId());
        }

        int index = puestos.indexOf(puestoExistente.get());
        puestos.set(index, puesto);
        guardarCambios();
        return puesto;
    }

    @Override
    public boolean eliminarPuesto(String id) {
        Optional<Puesto> puesto = obtenerPuestoPorId(id);
        if (puesto.isPresent()) {
            puestos.remove(puesto.get());
            guardarCambios();
            return true;
        }
        return false;
    }

    @Override
    public ResultadoOcupacion ocuparPuesto(String puestoId, String usuario) {
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            return new ResultadoOcupacion(false, "Puesto no encontrado", null, "PUESTO_NO_ENCONTRADO");
        }

        Puesto puesto = puestoOpt.get();

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            return new ResultadoOcupacion(false, mensaje, puesto, "PUESTO_NO_DISPONIBLE");
        }

        puesto.setEstadoPuesto(EstadoPuesto.OCUPADO);
        puesto.setUsuarioOcupante(usuario);
        puesto.setFechaOcupacion(LocalDateTime.now());

        String registroHistorial = String.format("Ocupado por %s en %s", usuario, LocalDateTime.now());
        puesto.agregarRegistroHistorial(registroHistorial);

        guardarCambios();

        String mensaje = String.format("Puesto %s ocupado por %s", puesto.getNumero(), usuario);
        return new ResultadoOcupacion(true, mensaje, puesto);
    }

    @Override
    public boolean liberarPuesto(String puestoId) {
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isPresent() && puestoOpt.get().getEstadoPuesto() == EstadoPuesto.OCUPADO) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            puesto.setUsuarioOcupante(null);
            puesto.setFechaOcupacion(null);

            String registroHistorial = String.format("Liberado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);

            guardarCambios();
            return true;
        }
        return false;
    }

    // Resto de métodos implementados (similar a tu versión original)
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
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.BLOQUEADO);
            puesto.setUsuarioOcupante(null);
            
            String registroHistorial = String.format("Bloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            return true;
        }
        return false;
    }

    @Override
    public boolean desbloquearPuesto(String puestoId) {
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setEstadoPuesto(EstadoPuesto.DISPONIBLE);
            
            String registroHistorial = String.format("Desbloqueado en %s", LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            return true;
        }
        return false;
    }

    @Override
    public ResultadoOcupacion asignarPuestoManual(String puestoId, String usuario) {
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
        return new ResultadoOcupacion(true, mensaje, puesto);
    }

    @Override
    public Puesto reasignarPuesto(String puestoId, String nuevaUbicacion) {
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);
        if (puestoOpt.isPresent()) {
            Puesto puesto = puestoOpt.get();
            puesto.setUbicacion(nuevaUbicacion);
            
            String registroHistorial = String.format("Reasignado a ubicación %s en %s", 
                    nuevaUbicacion, LocalDateTime.now());
            puesto.agregarRegistroHistorial(registroHistorial);
            
            guardarCambios();
            return puesto;
        }
        throw new IllegalArgumentException("Puesto no encontrado: " + puestoId);
    }

    @Override
    public boolean ponerPuestoEnMantenimiento(String puestoId) {
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
            return true;
        }
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
        Optional<Puesto> puestoOpt = obtenerPuestoPorId(puestoId);

        if (puestoOpt.isEmpty()) {
            return new ResultadoOcupacion(false, "Puesto no encontrado", null, "PUESTO_NO_ENCONTRADO");
        }

        Puesto puesto = puestoOpt.get();

        // Validar tipo de cliente vs tipo de puesto
        if (!validarTipoClientePuesto(tipoCliente, puesto.getTipoPuesto())) {
            String mensaje = String.format("El tipo de cliente '%s' no puede ocupar un puesto de tipo '%s'", 
                    tipoCliente, puesto.getTipoPuesto().getDescripcion());
            return new ResultadoOcupacion(false, mensaje, puesto, "TIPO_CLIENTE_NO_VALIDO");
        }

        if (puesto.getEstadoPuesto() != EstadoPuesto.DISPONIBLE) {
            String mensaje = String.format("Puesto no disponible. Estado actual: %s",
                    puesto.getEstadoPuesto().getDescripcion());
            return new ResultadoOcupacion(false, mensaje, puesto, "PUESTO_NO_DISPONIBLE");
        }

        // Verificar si el cliente ya tiene un puesto activo
        boolean clienteConPuesto = puestos.stream()
                .anyMatch(p -> p.getUsuarioOcupante() != null && 
                            p.getUsuarioOcupante().equals(usuario) && 
                            p.getEstadoPuesto() == EstadoPuesto.OCUPADO);
        
        if (clienteConPuesto) {
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