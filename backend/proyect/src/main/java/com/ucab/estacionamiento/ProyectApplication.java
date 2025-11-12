package com.ucab.estacionamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; 
import org.springframework.context.annotation.ComponentScan; // Importa esta clase

@ComponentScan(basePackages = {
    "com.ucab.estacionamiento", // Tu paquete principal
    "com.ucab.estacionamiento.exepciones" // <--- ¡Asegura este paquete!
})

@SpringBootApplication (exclude = {SecurityAutoConfiguration.class})
public class ProyectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectApplication.class, args);
	}

}
