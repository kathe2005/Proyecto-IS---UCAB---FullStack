package com.proyectIS.proyect.Pruebas.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest 
@AutoConfigureMockMvc 
// 💡 Spring levanta todo el contexto (Controller, Service, DB simulada)
public class ClienteControllerTest {

    @Autowired // 💡 Inyecta el objeto que simula el entorno HTTP
    private MockMvc mockMvc; 
    
    // Función para CARGAR el JSON de forma reutilizable
    private String cargarJsonValido() throws Exception {
    // Usamos el ClassLoader de esta CLASE (ClienteControllerTest) para buscar el archivo.
    // Esto es más seguro.
    return new String(FileCopyUtils.copyToByteArray(
        // Cambiamos el .getInputStream() a una sintaxis más corta.
        new ClassPathResource("cliente_valido.json").getInputStream()
    ));
}
    // ====================================================================
    // CASO 1: REGISTRO EXITOSO (201 Created)
    // ====================================================================
    @Test
    void registrarCliente_datosValidos_retorna201() throws Exception {
        String jsonRequest = cargarJsonValido();
        
        mockMvc.perform(post("/api/clientes") 
                // Usamos MediaType.APPLICATION_JSON_VALUE para evitar problemas de Null Safety en versiones recientes de Spring.
                .contentType(MediaType.APPLICATION_JSON_VALUE) 
                .content(jsonRequest))
                
                .andExpect(status().isCreated()) // Espera código 201
                .andExpect(jsonPath("$.nombre").value("Estudiante")); // Verifica un campo de la respuesta
    }

    // ====================================================================
    // CASO 2: EMAIL DUPLICADO (409 Conflict)
    // ====================================================================
    @Test
    void registrarCliente_emailDuplicado_retorna409Conflict() throws Exception {
        String jsonRequest = cargarJsonValido();

        // 1. PRE-CONDICIÓN: Primer Registro Exitoso (simula la inserción)
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonRequest))
                .andExpect(status().isCreated()); 

        // 2. ACCIÓN & ASERCIÓN: Segundo intento (Duplicado)
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonRequest))
                
                .andExpect(status().isConflict()) // Espera 409 Conflict
                .andExpect(jsonPath("$.mensaje").value("Su correo se encuentra registrado debe ingresar otro para continuar"));
    }
    
    // ====================================================================
    // CASO 3: CÉDULA INVÁLIDA (400 Bad Request)
    // ====================================================================
    @Test
    void registrarCliente_cedulaInvalida_retorna400BadRequest() throws Exception {
        String jsonBase = cargarJsonValido();
        
        // REUTILIZACIÓN: Modifica el JSON en la memoria (String.replace)
        String jsonInvalido = jsonBase.replace("V-12345678", "12345"); 
        
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonInvalido))
                
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request
                .andExpect(jsonPath("$.mensaje").value("El formato de la cédula debe ser V - 12345678"));
    }
}