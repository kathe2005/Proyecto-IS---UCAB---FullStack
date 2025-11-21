package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JsonManager {
    private static final String DATA_FILE = "puestos.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void guardarPuestos(List<Puesto> puestos) {
        if (puestos == null) {
            puestos = new ArrayList<>();
        }
        
        try {
            File file = new File(DATA_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, puestos);
            System.out.println("💾 Datos guardados en JSON: " + file.getAbsolutePath() + " (" + puestos.size() + " puestos)");
        } catch (IOException e) {
            System.err.println("❌ Error guardando datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Puesto> cargarPuestos() {
        try {
            File file = new File(DATA_FILE);
            
            // Debug: mostrar información del archivo
            System.out.println("📂 Intentando cargar desde: " + file.getAbsolutePath());
            System.out.println("📂 Archivo existe: " + file.exists());
            System.out.println("📂 Tamaño archivo: " + (file.exists() ? file.length() + " bytes" : "N/A"));
            
            if (!file.exists()) {
                System.out.println("📁 Archivo JSON no encontrado, creando datos iniciales...");
                return crearDatosIniciales();
            }

            if (file.length() == 0) {
                System.out.println("📁 Archivo JSON vacío, creando datos iniciales...");
                return crearDatosIniciales();
            }

            List<Puesto> puestos = objectMapper.readValue(
                file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Puesto.class)
            );
            
            System.out.println("✅ Datos cargados exitosamente: " + puestos.size() + " puestos");
            return puestos;

        } catch (IOException e) {
            System.err.println("❌ Error cargando datos: " + e.getMessage());
            e.printStackTrace();
            System.out.println("🔄 Creando datos iniciales debido al error...");
            return crearDatosIniciales();
        }
    }

    private static List<Puesto> crearDatosIniciales() {
        System.out.println("📝 Creando datos iniciales...");

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

        guardarPuestos(puestosIniciales);
        System.out.println("✅ Datos iniciales creados: " + puestosIniciales.size() + " puestos");

        return new ArrayList<>(puestosIniciales);
    }

    public static void mostrarArchivoJSON() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                System.out.println("❌ El archivo JSON no existe aún");
                return;
            }
            
            String content = Files.readString(file.toPath());
            System.out.println("\n📄 CONTENIDO DEL ARCHIVO JSON:");
            System.out.println("==================================");
            System.out.println(content);
            System.out.println("==================================");
            
        } catch (IOException e) {
            System.err.println("❌ Error leyendo archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para obtener la ruta del archivo (útil para debugging)
    public static String getFilePath() {
        return new File(DATA_FILE).getAbsolutePath();
    }
}