package com.ucab.estacionamiento.model.archivosJson;

import java.io.File;
import java.nio.file.Paths;

public class ConfigurationManager {
    private static final String PROJECT_ROOT;
    
    static {
        // Obtener el directorio actual de trabajo
        String currentDir = System.getProperty("user.dir");
        System.out.println("📁 Directorio actual: " + currentDir);
        
        // Si estamos en un subdirectorio de proyecto (backend/proyect), subir a la raíz
        if (currentDir.contains("backend") || currentDir.contains("proyect")) {
            PROJECT_ROOT = Paths.get(currentDir, "..", "..").normalize().toString();
        } else {
            PROJECT_ROOT = currentDir;
        }
        
        System.out.println("🚀 Root del proyecto: " + PROJECT_ROOT);
    }
    
    public static String getDataFilePath(String filename) {
        String path = Paths.get(PROJECT_ROOT, "data", filename).toString();
        System.out.println("📄 Ruta completa para " + filename + ": " + path);
        return path;
    }
    
    public static void ensureDataDirectoryExists() {
        File dataDir = new File(Paths.get(PROJECT_ROOT, "data").toString());
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            System.out.println(created ? "✅ Directorio data creado" : "❌ No se pudo crear directorio data");
        } else {
            System.out.println("✅ Directorio data ya existe");
        }
    }
}