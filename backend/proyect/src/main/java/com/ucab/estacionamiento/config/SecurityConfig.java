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
            .csrf(csrf -> csrf.disable()) // IMPORTANTE: Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(authorize -> authorize
                // ✅ PERMITIR TODAS LAS RUTAS NECESARIAS
                .requestMatchers("/reservas/api/**").permitAll()  // ✅ RESERVAS
                .requestMatchers("/clientes/api/**").permitAll()   // ✅ CLIENTES AGREGADO
                .requestMatchers("/puestos/api/**").permitAll()    // ✅ PUESTOS
                
                // Permisos para archivos estáticos si los tienes
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // Permisos para operaciones HTTP comunes
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/**").permitAll()    // ✅ GET para todo
                .requestMatchers(HttpMethod.POST, "/**").permitAll()   // ✅ POST para todo
                .requestMatchers(HttpMethod.PUT, "/**").permitAll()    // ✅ PUT para todo
                .requestMatchers(HttpMethod.DELETE, "/**").permitAll() // ✅ DELETE para todo
                
                .anyRequest().authenticated()
            )
            .cors(withDefaults());
        
        return http.build();
    }
}