package com.ucab.estacionamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;


@SpringBootApplication
public class EstacionamientoUcabApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstacionamientoUcabApplication.class, args);
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.addAllowedOrigin("http://localhost:4200"); 
        
        // Permite encabezados y métodos comunes
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        
        source.registerCorsConfiguration("/api/clientes/registrar", config);
        return new CorsFilter(source);
    }
    
}
