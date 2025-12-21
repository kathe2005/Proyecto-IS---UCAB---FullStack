package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Cliente;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JsonManagerCliente {
    private static final String CLIENTES_FILE = ConfigurationManager.getDataFilePath("clientes.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // ========== OPERACIONES CRUD CLIENTES ==========

    public Cliente guardarCliente(Cliente cliente) {
        System.out.println("💾 Guardando cliente: " + cliente.getUsuario());
        
        try {
            List<Cliente> clientes = cargarClientes();
            
            // Asignar ID si no tiene
            if (cliente.getId() == null) {
                cliente.setId(UUID.randomUUID());
                System.out.println("🆕 ID asignado: " + cliente.getId());
            }

            // Verificar si ya existe por usuario
            Optional<Cliente> clienteExistente = clientes.stream()
                    .filter(c -> c.getId().equals(cliente.getId()))
                    .findFirst();
            
            if (clienteExistente.isPresent()) {
                System.out.println("🔄 Cliente existe, actualizando...");
                clientes.remove(clienteExistente.get());
            }

            clientes.add(cliente);
            guardarClientesEnArchivo(clientes);
            System.out.println("✅ Cliente guardado exitosamente: " + cliente.getUsuario());
            
        } catch (Exception e) {
            System.err.println("❌ Error guardando cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cliente;
    }

    public List<Cliente> cargarClientes() {
        System.out.println("📥 Cargando clientes desde archivo...");
        try {
            File archivo = new File(CLIENTES_FILE);
            System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
            System.out.println("🔍 Existe: " + archivo.exists());
            
            if (!archivo.exists()) {
                System.out.println("📝 Archivo no encontrado, creando uno nuevo...");
                archivo.getParentFile().mkdirs();
                guardarClientesEnArchivo(new ArrayList<>());
                return new ArrayList<>();
            }

            if (archivo.length() == 0) {
                System.out.println("📝 Archivo vacío");
                return new ArrayList<>();
            }

            List<Cliente> clientes = objectMapper.readValue(
                archivo, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Cliente.class)
            );
            
            System.out.println("✅ " + clientes.size() + " clientes cargados");
            return clientes;
            
        } catch (Exception e) {
            System.err.println("❌ Error cargando clientes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ========== MÉTODOS DE BÚSQUEDA ==========

    public Optional<Cliente> buscarPorUsuario(String usuario) {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getUsuario().equalsIgnoreCase(usuario))
                .findFirst();
    }

    public Optional<Cliente> buscarPorCedula(String cedula) {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getCedula().equalsIgnoreCase(cedula))
                .findFirst();
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Cliente> buscarPorTelefono(String telefono) {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getTelefono().equalsIgnoreCase(telefono))
                .findFirst();
    }

    public Optional<Cliente> buscarPorId(String id) {
        List<Cliente> clientes = cargarClientes();
        return clientes.stream()
                .filter(c -> c.getId().toString().equals(id))
                .findFirst();
    }

    public List<Cliente> obtenerTodosClientes() {
        return cargarClientes();
    }

    // ========== MÉTODOS AUXILIARES ==========

    private void guardarClientesEnArchivo(List<Cliente> clientes) {
        try {
            File archivo = new File(CLIENTES_FILE);
            archivo.getParentFile().mkdirs();
            objectMapper.writeValue(archivo, clientes);
            System.out.println("💾 " + clientes.size() + " clientes guardados en archivo: " + archivo.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Error guardando clientes en archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean eliminarCliente(String id) {
        try {
            List<Cliente> clientes = cargarClientes();
            boolean eliminado = clientes.removeIf(c -> c.getId().toString().equals(id));
            if (eliminado) {
                guardarClientesEnArchivo(clientes);
                System.out.println("✅ Cliente eliminado: " + id);
            } else {
                System.out.println("❌ Cliente no encontrado: " + id);
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("❌ Error eliminando cliente: " + e.getMessage());
            return false;
        }
    }

    public void limpiarClientes() {
        guardarClientesEnArchivo(new ArrayList<>());
        System.out.println("🗑️ Todos los clientes eliminados");
    }

    public void diagnostico() {
        File archivo = new File(CLIENTES_FILE);
        System.out.println("🩺 DIAGNÓSTICO CLIENTES:");
        System.out.println("📁 Ruta: " + archivo.getAbsolutePath());
        System.out.println("🔍 Existe: " + archivo.exists());
        System.out.println("📏 Tamaño: " + (archivo.exists() ? archivo.length() + " bytes" : "N/A"));
        System.out.println("👥 Clientes en archivo: " + cargarClientes().size());
    }
}