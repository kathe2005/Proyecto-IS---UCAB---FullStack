package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JsonManager {
    // Nombre del archivo de puestos (se busca en la raíz del repositorio)
    private static final String DATA_FILE = "puestos.json";

    private static File resolvePuestosFile() {
        File cwd = new File(System.getProperty("user.dir"));
        File current = cwd;
        File lastFound = null;
        while (current != null) {
            File candidate = new File(current, DATA_FILE);
            if (candidate.exists()) {
                lastFound = candidate; // keep searching so we can prefer topmost occurrence
            }
            current = current.getParentFile();
        }
        if (lastFound != null) return lastFound;
        return new File(DATA_FILE);
    }

    public static void guardarPuestos(List<Puesto> puestos) {
        if (puestos == null) {
            puestos = new ArrayList<>();
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println("[");
            for (int i = 0; i < puestos.size(); i++) {
                Puesto puesto = puestos.get(i);
                writer.println("  {");
                writer.println("    \"id\": \"" + puesto.getId() + "\",");
                writer.println("    \"numero\": \"" + puesto.getNumero() + "\",");
                writer.println("    \"ubicacion\": \"" + puesto.getUbicacion() + "\",");
                writer.println("    \"usuarioOcupante\": " + (puesto.getUsuarioOcupante() != null ? "\"" + puesto.getUsuarioOcupante() + "\"" : "null") + ",");
                writer.println("    \"tipoPuesto\": \"" + puesto.getTipoPuesto() + "\",");
                writer.println("    \"estadoPuesto\": \"" + puesto.getEstadoPuesto() + "\",");
                writer.println("    \"fechaOcupacion\": " + (puesto.getFechaOcupacion() != null ? "\"" + puesto.getFechaOcupacion() + "\"" : "null") + ",");
                writer.println("    \"fechaCreacion\": \"" + puesto.getFechaCreacion() + "\",");
                
                writer.println("    \"historialOcupacion\": [");
                List<String> historial = puesto.getHistorialOcupacion();
                for (int j = 0; j < historial.size(); j++) {
                    writer.println("      \"" + escapeJson(historial.get(j)) + "\"" + (j < historial.size() - 1 ? "," : ""));
                }
                writer.println("    ]");
                
                writer.println("  }" + (i < puestos.size() - 1 ? "," : ""));
            }
            writer.println("]");
            
            System.out.println("💾 Datos guardados en JSON: " + DATA_FILE + " (" + puestos.size() + " puestos)");
        } catch (IOException e) {
            System.err.println("❌ Error guardando datos: " + e.getMessage());
        }
    }

    public static List<Puesto> cargarPuestos() {
        File file = resolvePuestosFile();
        try {
            if (!file.exists()) {
                System.out.println("📁 Archivo JSON no encontrado, creando datos iniciales...");
                return crearDatosIniciales();
            }

            if (file.length() == 0) {
                System.out.println("📁 Archivo JSON vacío, creando datos iniciales...");
                return crearDatosIniciales();
            }

            System.out.println("📂 Cargando desde JSON: " + file.getAbsolutePath());
            String content = Files.readString(file.toPath());
            
            List<Puesto> puestos = parseJson(content);
            
            System.out.println("✅ Datos cargados: " + puestos.size() + " puestos");
            return puestos;

        } catch (IOException e) {
            System.err.println("❌ Error cargando datos: " + e.getMessage());
            System.out.println("🔄 Creando datos iniciales debido al error...");
            return crearDatosIniciales();
        }
    }

    private static List<Puesto> parseJson(String jsonContent) {
        List<Puesto> puestos = new ArrayList<>();
        
        try {
            String[] puestoBlocks = jsonContent.split("\\},\\s*\\{");
            
            for (String block : puestoBlocks) {
                block = block.replaceAll("[\\[\\]\\{\\}]", "").trim();
                
                if (block.isEmpty()) continue;
                
                Puesto puesto = new Puesto();
                String[] lines = block.split("\\n");
                
                for (String line : lines) {
                    line = line.trim();
                    if (line.contains("\"id\":")) {
                        puesto.setId(extractValue(line));
                    } else if (line.contains("\"numero\":")) {
                        puesto.setNumero(extractValue(line));
                    } else if (line.contains("\"ubicacion\":")) {
                        puesto.setUbicacion(extractValue(line));
                    } else if (line.contains("\"usuarioOcupante\":")) {
                        String value = extractValue(line);
                        puesto.setUsuarioOcupante("null".equals(value) ? null : value);
                    } else if (line.contains("\"tipoPuesto\":")) {
                        puesto.setTipoPuesto(TipoPuesto.valueOf(extractValue(line)));
                    } else if (line.contains("\"estadoPuesto\":")) {
                        puesto.setEstadoPuesto(EstadoPuesto.valueOf(extractValue(line)));
                    } else if (line.contains("\"fechaOcupacion\":")) {
                        String value = extractValue(line);
                        // Evitar NPE: comprobar que value no sea null antes de invocar métodos sobre él
                        if (value != null && !"null".equals(value)) {
                            puesto.setFechaOcupacion(LocalDateTime.parse(value.replace("T", "T")));
                        }
                    } else if (line.contains("\"fechaCreacion\":")) {
                        String value = extractValue(line);
                        if (value != null && !"null".equals(value)) {
                            puesto.setFechaCreacion(LocalDateTime.parse(value.replace("T", "T")));
                        }
                    }
                }
                
                puestos.add(puesto);
            }
        } catch (Exception e) {
            System.err.println("⚠️  Error parseando JSON, usando datos iniciales: " + e.getMessage());
            return crearDatosIniciales();
        }
        
        return puestos;
    }

    private static String extractValue(String line) {
        int idx = line.indexOf(":");
        if (idx >= 0 && idx + 1 < line.length()) {
            String value = line.substring(idx + 1).trim();
            // Remove surrounding quotes if present
            if (value.startsWith("\"") && value.endsWith(",")) {
                value = value.substring(1, value.length() - 2); // remove leading " and trailing ",
            } else if (value.startsWith("\"")) {
                value = value.substring(1).replaceAll(",?$", "");
            } else {
                value = value.replaceAll(",?$", "");
            }
            return "null".equals(value) ? null : value;
        }
        return null;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static List<Puesto> crearDatosIniciales() {
        System.out.println("📝 Creando datos iniciales...");

        Puesto puesto1 = new Puesto("P1", "A-01", TipoPuesto.REGULAR, EstadoPuesto.DISPONIBLE, "Zona A");
        Puesto puesto2 = new Puesto("P2", "A-02", TipoPuesto.DOCENTE, EstadoPuesto.OCUPADO, "Zona A");
        puesto2.setUsuarioOcupante("profesor_garcia");
        puesto2.setFechaOcupacion(LocalDateTime.now().minusHours(2));
        puesto2.agregarRegistroHistorial("Ocupado por profesor_garcia en " + LocalDateTime.now().minusHours(2));

        Puesto puesto3 = new Puesto("P3", "B-01", TipoPuesto.DISCAPACITADO, EstadoPuesto.DISPONIBLE, "Zona B");
        Puesto puesto4 = new Puesto("P4", "C-01", TipoPuesto.VISITANTE, EstadoPuesto.BLOQUEADO, "Zona C");
        Puesto puesto5 = new Puesto("P5", "M-01", TipoPuesto.MOTOCICLETA, EstadoPuesto.DISPONIBLE, "Zona Motos");

        List<Puesto> puestosIniciales = Arrays.asList(puesto1, puesto2, puesto3, puesto4, puesto5);
        guardarPuestos(puestosIniciales);

        return new ArrayList<>(puestosIniciales);
    }

    public static void mostrarArchivoJSON() {
        try {
            File file = resolvePuestosFile();
            if (!file.exists()) {
                System.out.println("❌ El archivo JSON no existe aún");
                return;
            }
            
            String content = Files.readString(file.toPath());
            System.out.println("\n📄 CONTENIDO DEL ARCHIVO JSON:");
            System.out.println("==================================");
            System.out.println(content);
            
        } catch (IOException e) {
            System.err.println("❌ Error leyendo archivo JSON: " + e.getMessage());
        }
    }
}

