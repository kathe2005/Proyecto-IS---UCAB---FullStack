package com.ucab.estacionamiento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; // Importante para la anotación @NonNull
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configura los mapeos CORS para la aplicación.
     * Al añadir la anotación @NonNull al parámetro, se resuelve la advertencia
     * que indica que el método heredado de WebMvcConfigurer espera un parámetro no nulo.
     *
     * @param registry El registro CORS al que se añadirán las configuraciones.
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {

        registry.addMapping("/**") 
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") 
                .allowCredentials(true) 
                .maxAge(3600); 

    }
}
