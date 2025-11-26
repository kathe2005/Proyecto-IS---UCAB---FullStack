/*package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ucab.estacionamiento.model.clases.Cliente;

import java.io.File;
import java.util.List;

public class JsonFileUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT); // Para que el JSON sea legible

    // Nombre del archivo de clientes
    private static final String CLIENTE_FILE_NAME = "../../data/clientes.json";

    // Metodo para cargar la lista de Clientes desde el archivo JSON
    public static List<Cliente> loadClientes() {
        try {
            File file = new File(CLIENTE_FILE_NAME);
            if (!file.exists()) {
                System.out.println("? Archivo " + CLIENTE_FILE_NAME + " no encontrado. Se usará la lista inicial.");
                return null; // Retorna null para usar la lista predeterminada del repositorio
            }

            // Lectura del archivo JSON en una lista de Clientes
            List<Cliente> clientes = MAPPER.readValue(file, 
                MAPPER.getTypeFactory().constructCollectionType(List.class, Cliente.class));
            
            System.out.println("? Clientes cargados desde JSON: " + CLIENTE_FILE_NAME + " (" + clientes.size() + " clientes)");
            return clientes;
            
        } catch (Exception e) {
            System.err.println("?? Error al cargar clientes desde JSON: " + e.getMessage());
            // Manejo de error: puede ser útil retornar una lista vacía o null
            return null;
        }
    }

    // Metodo para guardar la lista de Clientes en el archivo JSON
    public static void saveClientes(List<Cliente> clientes) {
        try {
            MAPPER.writeValue(new File(CLIENTE_FILE_NAME), clientes);
            System.out.println("? Datos guardados en JSON: " + CLIENTE_FILE_NAME + " (" + clientes.size() + " clientes)");
        } catch (Exception e) {
            System.err.println("?? Error al guardar clientes en JSON: " + e.getMessage());
        }
    }
}
*/