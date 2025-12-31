package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Incidencia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JsonManagerIncidencias {
    private static final String INCIDENCIAS_FILE = ConfigurationManager.getDataFilePath("incidencias.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    public List<Incidencia> cargarIncidencias() {
        File archivo = new File(INCIDENCIAS_FILE);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(archivo, new TypeReference<List<Incidencia>>() {});
        } catch (IOException e) {
            System.err.println("❌ Error al leer incidencias.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public Optional<Incidencia> buscarIncidenciaPorId(int id) {
        return cargarIncidencias().stream()
                .filter(inc -> inc.getId() == id)
                .findFirst();
    }
    private void guardarIncidenciasEnArchivo(List<Incidencia> incidencias) {
        try {
            objectMapper.writeValue(new File(INCIDENCIAS_FILE), incidencias);
        } catch (IOException e) {
            System.err.println("❌ Error escribiendo en incidencias.json: " + e.getMessage());
        }
    }
    public Incidencia guardarIncidencia(Incidencia incidenciaNueva) {
        List<Incidencia> incidencias = cargarIncidencias();

        if (incidenciaNueva.getId() == 0) {
            int siguienteId = incidencias.stream()
                    .mapToInt(Incidencia::getId)
                    .max()
                    .orElse(0) + 1;
            incidenciaNueva.setId(siguienteId);
            incidencias.add(incidenciaNueva);
        } else {
            for (int i = 0; i < incidencias.size(); i++) {
                if (incidencias.get(i).getId() == incidenciaNueva.getId()) {
                    incidencias.set(i, incidenciaNueva);
                    break;
                }
            }
        }

        guardarIncidenciasEnArchivo(incidencias);
        System.out.println("✅ Incidencia guardada/actualizada: ID " + incidenciaNueva.getId());
        return incidenciaNueva;
    }
    public boolean eliminarIncidencia(int id) {
        List<Incidencia> incidencias = cargarIncidencias();
        boolean fueEliminado = incidencias.removeIf(inc -> inc.getId() == id);
        if (fueEliminado) {
            guardarIncidenciasEnArchivo(incidencias);
            System.out.println("🗑️ Incidencia con ID " + id + " eliminada permanentemente.");
        } else {
            System.err.println("⚠️ No se encontró la incidencia ID " + id + " para eliminar.");
        }
        return fueEliminado;
    }
}