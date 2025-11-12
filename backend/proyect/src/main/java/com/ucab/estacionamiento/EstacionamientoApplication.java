package com.ucab.estacionamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; 

@SpringBootApplication
@ComponentScan(basePackages = {"com.ucab.estacionamiento", "com.ucab.estacionamiento.exepciones"}) // <--- Añade explícitamente el paquete 'exepciones'
public class EstacionamientoApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstacionamientoApplication.class, args);
    }
}
