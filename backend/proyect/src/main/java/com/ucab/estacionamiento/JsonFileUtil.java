package com.ucab.estacionamiento;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ucab.estacionamiento.model.Cliente;
import java.io.File;
import java.util.List;

public class JsonFileUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT); // Para que el JSON sea legible

    // Nombre del archivo de clientes (a nivel del repositorio raíz)
    private static final String CLIENTE_FILE_NAME = "clientes.json";

    // Resuelve la ruta al archivo en la raíz del repositorio (dos niveles arriba de user.dir)
    private static File resolveClienteFile() {
        File cwd = new File(System.getProperty("user.dir"));
        // Buscar ascendentemente desde el directorio actual hasta encontrar el archivo
        File current = cwd;
        File lastFound = null;
        while (current != null) {
            File candidate = new File(current, CLIENTE_FILE_NAME);
            if (candidate.exists()) {
                lastFound = candidate; // keep searching to prefer topmost occurrence
            }
            current = current.getParentFile();
        }
        // Si no se encuentra, devolvemos null para indicar que no existe en el árbol
        // y evitar crear archivos automáticamente en el directorio de trabajo.
        return lastFound;
    }

    // Metodo para cargar la lista de Clientes desde el archivo JSON
    public static List<Cliente> loadClientes() {
        try {
            File file = resolveClienteFile();
            if (file == null || !file.exists()) {
                System.err.println("! Archivo '" + CLIENTE_FILE_NAME + "' no encontrado en el árbol del repositorio. No se crearán datos por defecto. Coloque el archivo en la raíz del proyecto.");
                return null;
            }

            // Lectura del archivo JSON en una lista de Clientes
            List<Cliente> clientes = MAPPER.readValue(file,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Cliente.class));

            System.out.println("? Clientes cargados desde JSON: " + file.getAbsolutePath() + " (" + clientes.size() + " clientes)");
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
            File target = resolveClienteFile();
            if (target == null) {
                System.err.println("! No se encontró una ruta válida para '" + CLIENTE_FILE_NAME + "'. No se escribirá ningún archivo.");
                return;
            }
            MAPPER.writeValue(target, clientes);
            System.out.println("? Datos guardados en JSON: " + target.getAbsolutePath() + " (" + clientes.size() + " clientes)");
        } catch (Exception e) {
            System.err.println("?? Error al guardar clientes en JSON: " + e.getMessage());
        }
    }
}