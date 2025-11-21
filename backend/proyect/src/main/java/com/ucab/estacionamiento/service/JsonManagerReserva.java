package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.Reserva;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManagerReserva {
    private static final String RESERVAS_FILE = "reservas.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void guardarReservas(List<Reserva> reservas) {
        if (reservas == null) {
            reservas = new ArrayList<>();
        }
        
        try {
            File file = new File(RESERVAS_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, reservas);
            System.out.println("💾 Reservas guardadas en JSON: " + file.getAbsolutePath() + " (" + reservas.size() + " reservas)");
        } catch (IOException e) {
            System.err.println("❌ Error guardando reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Reserva> cargarReservas() {
        try {
            File file = new File(RESERVAS_FILE);
            
            if (!file.exists()) {
                System.out.println("📁 Archivo de reservas no encontrado, creando uno nuevo...");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                System.out.println("📁 Archivo de reservas vacío");
                return new ArrayList<>();
            }

            List<Reserva> reservas = objectMapper.readValue(
                file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Reserva.class)
            );
            
            System.out.println("✅ Reservas cargadas exitosamente: " + reservas.size() + " reservas");
            return reservas;

        } catch (IOException e) {
            System.err.println("❌ Error cargando reservas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}