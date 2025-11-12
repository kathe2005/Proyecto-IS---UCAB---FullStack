package src.main.resources.cliente; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 👈 Importante para leer el archivo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource; // 👈 Importante
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Configura MockMvc automáticamente
class ProyectApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:Pruebas/cliente/cliente_valido.json") 
    private Resource clienteValidoJson;

    @Test
    void contextLoads() 
    {

    }

    @Test
    void testRegistroClienteValido() throws Exception 
    {
        
        String jsonContent = new String(Files.readAllBytes(clienteValidoJson.getFile().toPath()));

        // Ejecutar la petición POST con el JSON de prueba
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cliente/registrar")
                .contentType("application/json")
                .content(jsonContent))
                .andExpect(status().isOk()); 
    }
}
