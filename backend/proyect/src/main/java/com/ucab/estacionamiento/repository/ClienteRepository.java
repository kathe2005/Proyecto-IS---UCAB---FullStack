package com.ucab.estacionamiento.repository;

import org.springframework.stereotype.Repository;
import com.ucab.estacionamiento.model.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteRepository {

    private static final String JSON_FILE_PATH = "clientes.json";
    private final ObjectMapper objectMapper;
    private List<Cliente> BD_clientes;

    public ClienteRepository() {
        System.out.println("🔧 ===== INICIANDO CLIENTE REPOSITORY =====");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        File currentDir = new File(".");
        System.out.println("📂 Directorio actual: " + currentDir.getAbsolutePath());
        
        this.BD_clientes = cargarClientesDesdeArchivo();
        System.out.println("✅ Repository inicializado. Clientes en memoria: " + BD_clientes.size());
        System.out.println("🔧 ===== FIN INICIALIZACIÓN =====");
    }

    public Cliente guardar(Cliente cliente) {
        System.out.println("💾 === INICIANDO GUARDADO ===");
        System.out.println("👤 Cliente a guardar: " + cliente.getUsuario());
        
        try {
            // Asignar ID si no tiene
            if (cliente.getId() == null) {
                cliente.setId(UUID.randomUUID());
                System.out.println("🆕 ID asignado: " + cliente.getId());
            }

            // Verificar si ya existe por ID
            Optional<Cliente> clienteExistente = BD_clientes.stream()
                    .filter(c -> c.getId().equals(cliente.getId()))
                    .findFirst();
            
            if (clienteExistente.isPresent()) {
                System.out.println("🔄 Cliente existe, actualizando...");
                // Actualizar el existente
                BD_clientes.remove(clienteExistente.get());
            }

            // Agregar nuevo cliente
            BD_clientes.add(cliente);
            System.out.println("📊 Total clientes en memoria: " + BD_clientes.size());

            // Guardar en archivo
            System.out.println("💾 Intentando guardar en archivo...");
            boolean exito = guardarClientesEnArchivo();
            
            if (exito) {
                System.out.println("✅ GUARDADO EXITOSO EN JSON");
            } else {
                System.err.println("❌ FALLÓ EL GUARDADO EN JSON");
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("💾 === FIN GUARDADO ===");
        return cliente;
    }

    private List<Cliente> cargarClientesDesdeArchivo() {
        System.out.println("📥 === CARGANDO DESDE ARCHIVO ===");
        try {
            File archivo = new File(JSON_FILE_PATH);
            System.out.println("📁 Ruta completa: " + archivo.getAbsolutePath());
            System.out.println("🔍 Archivo existe: " + archivo.exists());
            
            if (archivo.exists()) {
                System.out.println("📏 Tamaño del archivo: " + archivo.length() + " bytes");
            }

            if (!archivo.exists()) {
                System.out.println("📝 Creando nuevo archivo...");
                boolean creado = archivo.createNewFile();
                System.out.println("📝 Archivo creado: " + creado);
                
                if (creado) {
                    objectMapper.writeValue(archivo, new ArrayList<Cliente>());
                    System.out.println("✅ Archivo inicializado con array vacío");
                } else {
                    System.err.println("❌ No se pudo crear el archivo");
                }
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo vacío detectado");
                return new ArrayList<>();
            }

            List<Cliente> clientes = objectMapper.readValue(archivo, new TypeReference<List<Cliente>>() {});
            System.out.println("📥 " + clientes.size() + " clientes cargados desde archivo");
            return clientes;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR cargando archivo: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean guardarClientesEnArchivo() {
        System.out.println("💾 === GUARDANDO EN ARCHIVO ===");
        try {
            File archivo = new File(JSON_FILE_PATH);
            System.out.println("📁 Guardando en: " + archivo.getAbsolutePath());
            System.out.println("📊 Guardando " + BD_clientes.size() + " clientes");
            
            objectMapper.writeValue(archivo, BD_clientes);
            System.out.println("✅ Archivo guardado exitosamente");
            
            System.out.println("📏 Tamaño después de guardar: " + archivo.length() + " bytes");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR guardando archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> findAll() {
        return new ArrayList<>(BD_clientes);
    }

    public Optional<Cliente> findByUsuario(String usuarioBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getUsuario().equalsIgnoreCase(usuarioBuscado))
                .findFirst();
    }

    public Optional<Cliente> findByCedula(String cedulaBuscada) {
        return BD_clientes.stream()
                .filter(u -> u.getCedula().equalsIgnoreCase(cedulaBuscada))
                .findFirst();
    }

    public Optional<Cliente> findByEmail(String emailBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailBuscado))
                .findFirst();
    }

    public Optional<Cliente> findByTelefono(String telefonoBuscado) {
        return BD_clientes.stream()
                .filter(u -> u.getTelefono().equalsIgnoreCase(telefonoBuscado))
                .findFirst();
    }

    // Método para diagnóstico
    public void diagnostico() {
        System.out.println("🩺 === DIAGNÓSTICO DEL REPOSITORY ===");
        File archivo = new File(JSON_FILE_PATH);
        System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
        System.out.println("📂 Existe: " + archivo.exists());
        System.out.println("🔐 Puede escribir: " + archivo.canWrite());
        System.out.println("👥 Clientes en memoria: " + BD_clientes.size());
        System.out.println("💻 Directorio actual: " + System.getProperty("user.dir"));
        System.out.println("🩺 === FIN DIAGNÓSTICO ===");
    }
}