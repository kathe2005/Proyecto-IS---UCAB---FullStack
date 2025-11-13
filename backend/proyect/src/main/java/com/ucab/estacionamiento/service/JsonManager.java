package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Importante para LocalDateTime
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JsonManager {
    // Archivo externo donde se guardan/leen los datos (directorio de trabajo al ejecutar la app)
    private static final String EXTERNAL_DATA_FILE = System.getProperty("user.dir") + File.separator + "puestos.json";
    // Recurso embebido en el classpath (src/main/resources/puestos.json)
    private static final String CLASSPATH_RESOURCE = "/puestos.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void guardarPuestos(List<Puesto> puestos) {
        if (puestos == null) {
            puestos = new ArrayList<>();
        }

        try {
            File out = new File(EXTERNAL_DATA_FILE);
            mapper.writeValue(out, puestos);

            System.out.println("💾 Datos guardados en JSON: " + out.getAbsolutePath() + " (" + puestos.size() + " puestos)");
        } catch (IOException e) {
            System.err.println("❌ Error guardando datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Puesto> cargarPuestos() {
        File external = new File(EXTERNAL_DATA_FILE);
        try {
            // 1) Intentar cargar desde archivo externo (directorio de trabajo)
            if (external.exists() && external.length() > 0) {
                System.out.println("� Cargando desde JSON externo: " + external.getAbsolutePath());
                List<Puesto> puestos = mapper.readValue(external, new TypeReference<List<Puesto>>() {});
                System.out.println("✅ Datos cargados desde externo: " + puestos.size() + " puestos");
                return puestos;
            }

            // 2) Si no existe, intentar cargar desde recurso en classpath (src/main/resources/puestos.json)
            InputStream is = JsonManager.class.getResourceAsStream(CLASSPATH_RESOURCE);
            if (is != null) {
                System.out.println("📂 Archivo externo no encontrado. Cargando desde recurso en classpath: " + CLASSPATH_RESOURCE);
                List<Puesto> puestos = mapper.readValue(is, new TypeReference<List<Puesto>>() {});
                // Persistir una copia en el archivo externo para futuras ejecuciones
                guardarPuestos(puestos);
                System.out.println("✅ Datos cargados desde classpath y guardados en externo: " + puestos.size() + " puestos");
                return puestos;
            }

            // 3) Si tampoco está en classpath, crear datos iniciales y guardarlos
            System.out.println("📁 Archivo JSON no encontrado en externo ni classpath, creando datos iniciales...");
            return crearDatosIniciales();

        } catch (IOException e) {
            System.err.println("❌ Error cargando datos: " + e.getMessage());
            e.printStackTrace(); // Ver el error de parseo si ocurre
            System.out.println("🔄 Creando datos iniciales debido al error...");
            return crearDatosIniciales();
        }
    }

    public List<Puesto> crearDatosIniciales() {
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

    public void mostrarArchivoJSON() {
        try {
            File file = new File(EXTERNAL_DATA_FILE);
            if (!file.exists()) {
                System.out.println("❌ El archivo JSON externo no existe aún: " + file.getAbsolutePath());
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