package com.ucab.estacionamiento.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                
                // Aplica esta configuración a TODAS las rutas de la API
                registry.addMapping("/**") 
                    
                    // Permite que las peticiones vengan de tu aplicación Angular
                    .allowedOrigins("http://localhost:4200") 
                    
                    // Permite todos los métodos HTTP (GET, POST, PUT, DELETE, OPTIONS)
                    .allowedMethods("*") 
                    
                    // Permite todos los encabezados
                    .allowedHeaders("*") 
                    
                    // Permite credenciales (cookies, headers de autenticación, si fueran necesarios)
                    .allowCredentials(true); 
            }
        };
    }
}
