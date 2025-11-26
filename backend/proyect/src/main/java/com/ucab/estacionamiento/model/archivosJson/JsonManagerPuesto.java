package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonManagerPuesto {
    private static final String PUESTOS_FILE = "../../data/puestos.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ========== OPERACIONES CRUD PUESTOS ==========

    public Puesto guardarPuesto(Puesto puesto) {
        System.out.println("💾 Guardando puesto: " + puesto.getNumero());
        
        try {
            List<Puesto> puestos = cargarPuestos();
            
            // Verificar si ya existe
            Optional<Puesto> puestoExistente = puestos.stream()
                    .filter(p -> p.getId().equals(puesto.getId()))
                    .findFirst();
            
            if (puestoExistente.isPresent()) {
                System.out.println("🔄 Puesto existe, actualizando...");
                puestos.remove(puestoExistente.get());
            }

            puestos.add(puesto);
            guardarPuestosEnArchivo(puestos);
            System.out.println("✅ Puesto guardado exitosamente: " + puesto.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Error guardando puesto: " + e.getMessage());
            e.printStackTrace();
        }
        
        return puesto;
    }

    public List<Puesto> cargarPuestos() {
        System.out.println("📥 Cargando puestos desde archivo...");
        try {
            File archivo = new File(PUESTOS_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            
            if (!archivo.exists()) {
                System.out.println("📝 Archivo no encontrado, creando datos iniciales...");
                return crearDatosInicialesPuestos();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo vacío, creando datos iniciales...");
                return crearDatosInicialesPuestos();
            }

            List<Puesto> puestos = objectMapper.readValue(
                archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Puesto.class)
            );
            
            System.out.println("✅ " + puestos.size() + " puestos cargados");
            return puestos;
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando puestos: " + e.getMessage());
            System.out.println("🔄 Creando datos iniciales debido al error...");
            return crearDatosInicialesPuestos();
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA Y FILTRADO ==========

    public Optional<Puesto> buscarPuestoPorId(String id) {
        List<Puesto> puestos = cargarPuestos();
        return puestos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Puesto> buscarPuestosPorEstado(EstadoPuesto estado) {
        List<Puesto> puestos = cargarPuestos();
        return puestos.stream()
                .filter(p -> p.getEstadoPuesto() == estado)
                .collect(Collectors.toList());
    }

    public List<Puesto> buscarPuestosPorTipo(TipoPuesto tipo) {
        List<Puesto> puestos = cargarPuestos();
        return puestos.stream()
                .filter(p -> p.getTipoPuesto() == tipo)
                .collect(Collectors.toList());
    }

    public List<Puesto> filtrarPuestosPorUbicacion(String ubicacion) {
        List<Puesto> puestos = cargarPuestos();
        return puestos.stream()
                .filter(p -> p.getUbicacion().toLowerCase().contains(ubicacion.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Puesto> obtenerTodosPuestos() {
        return cargarPuestos();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void guardarPuestosEnArchivo(List<Puesto> puestos) {
        try {
            File archivo = new File(PUESTOS_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, puestos);
            System.out.println("💾 " + puestos.size() + " puestos guardados en archivo");
        } catch (Exception e) {
            System.err.println("❌ Error guardando puestos en archivo: " + e.getMessage());
        }
    }

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

        guardarPuestosEnArchivo(puestosIniciales);
        System.out.println("✅ Datos iniciales creados: " + puestosIniciales.size() + " puestos");

        return new ArrayList<>(puestosIniciales);
    }

    public boolean eliminarPuesto(String id) {
        try {
            List<Puesto> puestos = cargarPuestos();
            boolean eliminado = puestos.removeIf(p -> p.getId().equals(id));
            if (eliminado) {
                guardarPuestosEnArchivo(puestos);
                System.out.println("✅ Puesto eliminado: " + id);
            } else {
                System.out.println("❌ Puesto no encontrado: " + id);
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("❌ Error eliminando puesto: " + e.getMessage());
            return false;
        }
    }

    public void diagnostico() {
        File archivo = new File(PUESTOS_FILE);
        List<Puesto> puestos = cargarPuestos();
        System.out.println("🩺 DIAGNÓSTICO PUESTOS:");
        System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
        System.out.println("🔍 Existe: " + archivo.exists());
        System.out.println("📏 Tamaño: " + (archivo.exists() ? archivo.length() + " bytes" : "N/A"));
        System.out.println("🅿️  Puestos en archivo: " + puestos.size());
        
        // Estadísticas por estado
        System.out.println("📊 Estadísticas por estado:");
        for (EstadoPuesto estado : EstadoPuesto.values()) {
            long count = puestos.stream().filter(p -> p.getEstadoPuesto() == estado).count();
            System.out.println("   " + estado + ": " + count);
        }
    }
}