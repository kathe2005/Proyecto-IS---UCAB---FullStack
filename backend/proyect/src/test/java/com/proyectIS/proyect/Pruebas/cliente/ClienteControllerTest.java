package com.proyectIS.proyect.Pruebas.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects; // IMPORT AÑADIDO

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest 
@AutoConfigureMockMvc 
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc; 
    
    private final ObjectMapper objectMapper = new ObjectMapper(); // AÑADIDO
    
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
                .content(jsonRequest))
                
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
                .content(jsonRequest))
                .andExpect(status().isCreated()); 

        // 2. ACCIÓN & ASERCIÓN: Segundo intento (Duplicado)
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonRequest))
                
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Su correo se encuentra registrado debe ingresar otro para continuar"));
    }
    
    // ====================================================================
    // CASO 3: CÉDULA INVÁLIDA (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_cedulaInvalida_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();
        
        // REUTILIZACIÓN: Modifica el JSON en la memoria
        String jsonInvalido = jsonBase.replace("V-12345678", "12345"); 
        
        // VERIFICACIÓN SEGURA AÑADIDA
        String cedula = obtenerCedulaSegura(jsonInvalido);
        Objects.requireNonNull(cedula, "La cédula no puede ser null después de la conversión");
        
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonInvalido))
                
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El formato de la cédula debe ser V - 12345678"));
    }
}