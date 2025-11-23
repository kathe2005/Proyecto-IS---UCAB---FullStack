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

import java.util.Objects; // IMPORT AÑADIDO

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ProyectApplication.class)
@ContextConfiguration(initializers = ClienteControllerTest.Initializer.class)
@AutoConfigureMockMvc 
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc; 
    
    @Autowired
    private com.ucab.estacionamiento.model.archivosJson.ClienteRepository clienteRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper(); // AÑADIDO
    
    @BeforeEach
    void limpiarRepositorio() throws Exception {
        // Asegura que el archivo de persistencia esté vacío antes de cada test
        Path path = Path.of("../../data/clientes.json");
        if (Files.exists(path)) {
            Files.writeString(path, "[]");
        } else {
            Files.createFile(path);
            Files.writeString(path, "[]");
        }
        // Limpiar también el repositorio en memoria
        clienteRepository.clearAll();
    }

    // ApplicationContextInitializer that runs BEFORE the Spring context is created.
    public static class Initializer implements ApplicationContextInitializer<org.springframework.context.ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull org.springframework.context.ConfigurableApplicationContext applicationContext) {
            try {
                java.nio.file.Path p = java.nio.file.Path.of("../../data/clientes.json");
                if (java.nio.file.Files.exists(p)) {
                    java.nio.file.Files.writeString(p, "[]");
                } else {
                    java.nio.file.Files.createFile(p);
                    java.nio.file.Files.writeString(p, "[]");
                }
            } catch (Exception e) {
                throw new RuntimeException("No se pudo inicializar clientes.json para pruebas", e);
            }
        }
    }
    
    // Función para CARGAR el JSON de forma reutilizable
    private String cargarJsonValido() throws Exception {
        return new String(FileCopyUtils.copyToByteArray(
            new ClassPathResource("cliente_valido.json").getInputStream()
        ));
    }
    
    // MÉTODO HELPER AÑADIDO para obtener cédula de forma segura
    private String obtenerCedulaSegura(String jsonContent) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        return Objects.requireNonNull(jsonNode.get("cedula"), "cedula no puede ser null").asText();
    }
    
    // ====================================================================
    // CASO 1: REGISTRO EXITOSO (201 Created)
    // ====================================================================
    @Test
    void registrarCliente_datosValidos_retorna201() throws Exception {
        String jsonRequest = cargarJsonValido();
        
        // VERIFICACIÓN SEGURA AÑADIDA
        String cedula = obtenerCedulaSegura(jsonRequest);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");
        
        mockMvc.perform(post("/api/clientes") 
            .contentType(MediaType.APPLICATION_JSON_VALUE) 
            .content(Objects.requireNonNull(jsonRequest)))
                
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Estudiante"));
    }

    // ====================================================================
    // CASO 2: EMAIL DUPLICADO (409 Conflict)
    // ====================================================================
    @Test
    void registrarCliente_emailDuplicado_retorna409Conflict() throws Exception {
        String jsonRequest = cargarJsonValido();

        // VERIFICACIÓN SEGURA AÑADIDA
        String cedula = obtenerCedulaSegura(jsonRequest);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");

        // 1. PRE-CONDICIÓN: Primer Registro Exitoso
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(Objects.requireNonNull(jsonRequest)))
                .andExpect(status().isCreated()); 

        // 2. ACCIÓN & ASERCIÓN: Segundo intento (Duplicado)
        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(Objects.requireNonNull(jsonRequest)))
                
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El correo ingresado se encuentra registrado debe ingresar otro para continuar"));
    }
    
    // ====================================================================
    // CASO 3: CÉDULA INVÁLIDA (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_cedulaInvalida_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();

        // Construir el JSON inválido de forma segura usando ObjectMapper
        JsonNode node = objectMapper.readTree(jsonBase);
        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("cedula", "12345");
        String jsonInvalido = objectMapper.writeValueAsString(node);

        // VERIFICACIÓN SEGURA AÑADIDA
        String cedula = obtenerCedulaSegura(jsonInvalido);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");

        mockMvc.perform(post("/api/clientes")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(Objects.requireNonNull(jsonInvalido)))

            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensaje").value("El formato de la cédula debe contener solo números (ej. 12345678)"));
    }
}