package com.ucab.estacionamiento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/**").permitAll() // Permitir todas las rutas de API
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir preflight requests
                .anyRequest().authenticated()
            )
            .cors(withDefaults()); // Use Spring's default CORS support (reads `CorsConfig`)
        
        return http.build();
    }

    // CORS está configurado globalmente en `CorsConfig`.
}