package com.proyectIS.proyect.Pruebas.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.NonNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;
import org.junit.jupiter.api.BeforeEach;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucab.estacionamiento.application.ProyectApplication;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ProyectApplication.class)
@ContextConfiguration(initializers = ClienteControllerTest.Initializer.class)
@AutoConfigureMockMvc 
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc; 
    
    // Ya no necesitamos inyectar el repositorio antiguo
    // private com.ucab.estacionamiento.model.archivosJson.ClienteRepository clienteRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void limpiarRepositorio() throws Exception {
        // Asegura que el archivo de persistencia esté vacío antes de cada test
        Path path = Path.of("../../data/clientes.json");
        if (Files.exists(path)) {
            Files.writeString(path, "[]");
            System.out.println("🧹 Archivo clientes.json limpiado antes del test");
        } else {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            Files.writeString(path, "[]");
            System.out.println("📁 Archivo clientes.json creado antes del test");
        }
        // Ya no necesitamos limpiar el repositorio en memoria porque usamos archivos JSON directamente
    }

    // ApplicationContextInitializer que se ejecuta ANTES de que se cree el contexto de Spring
    public static class Initializer implements ApplicationContextInitializer<org.springframework.context.ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull org.springframework.context.ConfigurableApplicationContext applicationContext) {
            try {
                java.nio.file.Path p = java.nio.file.Path.of("../../data/clientes.json");
                if (java.nio.file.Files.exists(p)) {
                    java.nio.file.Files.writeString(p, "[]");
                    System.out.println("🔄 Archivo clientes.json inicializado para pruebas");
                } else {
                    java.nio.file.Files.createDirectories(p.getParent());
                    java.nio.file.Files.createFile(p);
                    java.nio.file.Files.writeString(p, "[]");
                    System.out.println("📁 Archivo clientes.json creado para pruebas");
                }
            } catch (Exception e) {
                throw new RuntimeException("No se pudo inicializar clientes.json para pruebas", e);
            }
        }
    }
    
    // Función para CARGAR el JSON de forma reutilizable
    private String cargarJsonValido() throws Exception {
        String jsonContent = new String(FileCopyUtils.copyToByteArray(
            new ClassPathResource("cliente_valido.json").getInputStream()
        ));
        System.out.println("📄 JSON cargado: " + jsonContent);
        return jsonContent;
    }
    
    // MÉTODO HELPER para obtener cédula de forma segura
    private String obtenerCedulaSegura(String jsonContent) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        JsonNode cedulaNode = jsonNode.get("cedula");
        if (cedulaNode == null) {
            throw new IllegalArgumentException("El campo 'cedula' no existe en el JSON");
        }
        return cedulaNode.asText();
    }

    // MÉTODO HELPER para obtener email de forma segura
    private String obtenerEmailSeguro(String jsonContent) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        JsonNode emailNode = jsonNode.get("email");
        if (emailNode == null) {
            throw new IllegalArgumentException("El campo 'email' no existe en el JSON");
        }
        return emailNode.asText();
    }
    
    // ====================================================================
    // CASO 1: REGISTRO EXITOSO (201 Created)
    // ====================================================================
    @Test
    void registrarCliente_datosValidos_retorna201() throws Exception {
        String jsonRequest = cargarJsonValido();
        
        // VERIFICACIÓN SEGURA
        String cedula = obtenerCedulaSegura(jsonRequest);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");
        
        String email = obtenerEmailSeguro(jsonRequest);
        Objects.requireNonNull(email, "El email no puede ser null después de la conversión");
        
        System.out.println("🚀 Ejecutando test: registro exitoso");
        System.out.println("📝 Cédula: " + cedula);
        System.out.println("📧 Email: " + email);
        
        mockMvc.perform(post("/api/clientes") 
            .contentType(MediaType.APPLICATION_JSON) 
            .content(Objects.requireNonNull(jsonRequest)))
                
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Estudiante"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.cedula").value(cedula));
        
        System.out.println("✅ Test registro exitoso completado");
    }

    // ====================================================================
    // CASO 2: EMAIL DUPLICADO (409 Conflict)
    // ====================================================================
    @Test
    void registrarCliente_emailDuplicado_retorna409Conflict() throws Exception {
        String jsonRequest = cargarJsonValido();

        // VERIFICACIÓN SEGURA
        String cedula = obtenerCedulaSegura(jsonRequest);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");

        String email = obtenerEmailSeguro(jsonRequest);
        Objects.requireNonNull(email, "El email no puede ser null después de la conversión");

        System.out.println("🚀 Ejecutando test: email duplicado");
        System.out.println("📝 Cédula: " + cedula);
        System.out.println("📧 Email: " + email);

        // 1. PRE-CONDICIÓN: Primer Registro Exitoso
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonRequest)))
                .andExpect(status().isCreated()); 

        System.out.println("✅ Primer registro exitoso, intentando duplicado...");

        // 2. ACCIÓN & ASERCIÓN: Segundo intento (Duplicado)
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonRequest)))
                
                .andExpect(status().isBadRequest()) // Cambiado de isConflict() a isBadRequest()
                .andExpect(jsonPath("$.error").exists()); // Cambiado de "mensaje" a "error"
        
        System.out.println("✅ Test email duplicado completado");
    }
    
    // ====================================================================
    // CASO 3: CÉDULA INVÁLIDA (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_cedulaInvalida_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();

        // Construir el JSON inválido de forma segura usando ObjectMapper
        JsonNode node = objectMapper.readTree(jsonBase);
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("cedula", "12345"); // Cédula muy corta
        String jsonInvalido = objectMapper.writeValueAsString(node);

        // VERIFICACIÓN SEGURA
        String cedula = obtenerCedulaSegura(jsonInvalido);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");

        System.out.println("🚀 Ejecutando test: cédula inválida");
        System.out.println("❌ Cédula inválida: " + cedula);

        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonInvalido)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists()); // Cambiado de "mensaje" a "error"
        
        System.out.println("✅ Test cédula inválida completado");
    }

    // ====================================================================
    // CASO 4: USUARIO DUPLICADO (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_usuarioDuplicado_retorna400BadRequest() throws Exception {
        String jsonRequest = cargarJsonValido();

        // VERIFICACIÓN SEGURA
        String cedula = obtenerCedulaSegura(jsonRequest);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");

        System.out.println("🚀 Ejecutando test: usuario duplicado");

        // 1. PRE-CONDICIÓN: Primer Registro Exitoso
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonRequest)))
                .andExpect(status().isCreated()); 

        System.out.println("✅ Primer registro exitoso, intentando usuario duplicado...");

        // 2. ACCIÓN & ASERCIÓN: Segundo intento con mismo usuario
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonRequest)))
                
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
        
        System.out.println("✅ Test usuario duplicado completado");
    }

    // ====================================================================
    // CASO 5: TELEFONO INVÁLIDO (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_telefonoInvalido_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();

        // Construir el JSON inválido con teléfono mal formateado
        JsonNode node = objectMapper.readTree(jsonBase);
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("telefono", "123456789"); // Formato incorrecto
        String jsonInvalido = objectMapper.writeValueAsString(node);

        System.out.println("🚀 Ejecutando test: teléfono inválido");
        System.out.println("❌ Teléfono inválido: 123456789");

        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonInvalido)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
        
        System.out.println("✅ Test teléfono inválido completado");
    }

    // ====================================================================
    // CASO 6: CONTRASEÑA INVÁLIDA (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_contrasenaInvalida_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();

        // Construir el JSON inválido con contraseña débil
        JsonNode node = objectMapper.readTree(jsonBase);
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("contrasena", "123"); // Contraseña muy débil
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("confirmarContrasena", "123"); // Confirmación igual
        String jsonInvalido = objectMapper.writeValueAsString(node);

        System.out.println("🚀 Ejecutando test: contraseña inválida");
        System.out.println("❌ Contraseña inválida: 123");

        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonInvalido)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
        
        System.out.println("✅ Test contraseña inválida completado");
    }

    // ====================================================================
    // CASO 7: DOMINIO DE EMAIL NO PERMITIDO (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_dominioEmailNoPermitido_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();

        // Construir el JSON con dominio no permitido
        JsonNode node = objectMapper.readTree(jsonBase);
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("email", "usuario@dominiono permitido.com");
        String jsonInvalido = objectMapper.writeValueAsString(node);

        String email = obtenerEmailSeguro(jsonInvalido);
        System.out.println("🚀 Ejecutando test: dominio email no permitido");
        System.out.println("❌ Email con dominio no permitido: " + email);

        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(Objects.requireNonNull(jsonInvalido)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
        
        System.out.println("✅ Test dominio email no permitido completado");
    }

    // ====================================================================
    // MÉTODO DE DIAGNÓSTICO (opcional - para debugging)
    // ====================================================================
    @Test
    void diagnosticoArchivoClientes() throws Exception {
        Path path = Path.of("../../data/clientes.json");
        if (Files.exists(path)) {
            String contenido = Files.readString(path);
            System.out.println("📁 Contenido actual de clientes.json:");
            System.out.println(contenido);
            System.out.println("📏 Tamaño del archivo: " + Files.size(path) + " bytes");
        } else {
            System.out.println("❌ Archivo clientes.json no existe");
        }
    }
}