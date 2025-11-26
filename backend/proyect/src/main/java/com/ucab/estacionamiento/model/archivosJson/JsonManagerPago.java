/* 
package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Pago;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManagerPago {
    private static final String PAGOS_FILE = "pagos.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void guardarPagos(List<Pago> pagos) {
        if (pagos == null) {
            pagos = new ArrayList<>();
        }
        
        try {
            File file = new File(PAGOS_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, pagos);
            System.out.println("💾 Pagos guardados en JSON: " + file.getAbsolutePath() + " (" + pagos.size() + " pagos)");
        } catch (IOException e) {
            System.err.println("❌ Error guardando pagos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Pago> cargarPagos() {
        try {
            File file = new File(PAGOS_FILE);
            
            if (!file.exists()) {
                System.out.println("📁 Archivo de pagos no encontrado, creando uno nuevo...");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                System.out.println("📁 Archivo de pagos vacío");
                return new ArrayList<>();
            }

            List<Pago> pagos = objectMapper.readValue(
                file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Pago.class)
            );
            
            System.out.println("✅ Pagos cargados exitosamente: " + pagos.size() + " pagos");
            return pagos;

        } catch (IOException e) {
            System.err.println("❌ Error cargando pagos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
*/