package com.ucab.estacionamiento.application;

import com.ucab.estacionamiento.model.archivosJson.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@ComponentScan(basePackages = {
    "com.ucab.estacionamiento",
    "com.ucab.estacionamiento.exepciones"
})

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ProyectApplication {

    public static void main(String[] args) {
        mostrarLogo();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 INICIANDO SISTEMA DE ESTACIONAMIENTO UCAB");
        System.out.println("=".repeat(80));
        
        // Inicializar los gestores JSON antes de Spring
        inicializarSistema();
        
        // Iniciar Spring Boot
        var context = SpringApplication.run(ProyectApplication.class, args);
        
        mostrarResumenFinal();
    }

    private static void mostrarLogo() {
        System.out.println("\n");
        System.out.println("███████╗███████╗████████╗ █████╗  ██████╗██╗ █████╗ ███╗   ██╗ ██████╗ ███╗   ███╗███████╗███╗   ██╗████████╗ ██████╗ ");
        System.out.println("██╔════╝██╔════╝╚══██╔══╝██╔══██╗██╔════╝██║██╔══██╗████╗  ██║██╔═══██╗████╗ ████║██╔════╝████╗  ██║╚══██╔══╝██╔═══██╗");
        System.out.println("███████╗█████╗     ██║   ███████║██║     ██║███████║██╔██╗ ██║██║   ██║██╔████╔██║█████╗  ██╔██╗ ██║   ██║   ██║   ██║");
        System.out.println("╚════██║██╔══╝     ██║   ██╔══██║██║     ██║██╔══██║██║╚██╗██║██║   ██║██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║   ██║   ██║");
        System.out.println("███████║███████╗   ██║   ██║  ██║╚██████╗██║██║  ██║██║ ╚████║╚██████╔╝██║ ╚═╝ ██║███████╗██║ ╚████║   ██║   ╚██████╔╝");
        System.out.println("╚══════╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ");
        System.out.println("\n" + " ".repeat(25) + "Sistema de Gestión de Estacionamiento UCAB");
        System.out.println("-".repeat(80));
    }

    private static void inicializarSistema() {
        System.out.println("\n📁 INICIALIZANDO GESTORES DE DATOS...");
        System.out.println("-".repeat(80));
        
        try {
            // 1. Verificar carpeta data
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean creado = dataDir.mkdirs();
                System.out.println("📂 Carpeta 'data': " + (creado ? "CREADA ✓" : "ERROR al crear"));
            } else {
                System.out.println("📂 Carpeta 'data': EXISTE ✓");
            }
            
            // 2. Inicializar ConfigurationManager para asegurar rutas
            ConfigurationManager.ensureDataDirectoryExists();
            
            // 3. Cargar y mostrar estado de cada archivo
            mostrarEstadoArchivos();
            
            // 4. Mostrar información del sistema
            mostrarInfoSistema();
            
        } catch (Exception e) {
            System.err.println("❌ ERROR durante inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarEstadoArchivos() {
        System.out.println("\n📊 ESTADO DE ARCHIVOS JSON:");
        System.out.println("─".repeat(80));
        
        // 1. Clientes
        try {
            JsonManagerCliente clienteManager = new JsonManagerCliente();
            int totalClientes = clienteManager.obtenerTodosClientes().size();
            System.out.println("👥 CLIENTES:");
            System.out.println("   📄 Archivo: " + ConfigurationManager.getDataFilePath("clientes.json"));
            System.out.println("   📊 Registros: " + totalClientes);
            System.out.println("   ✅ Estado: " + (totalClientes > 0 ? "CARGADO ✓" : "VACIO (lista para usar)"));
        } catch (Exception e) {
            System.out.println("👥 CLIENTES:");
            System.out.println("   ❌ Error: " + e.getMessage());
        }

        // 2. Puestos
        try {
            JsonManagerPuesto puestoManager = new JsonManagerPuesto();
            int totalPuestos = puestoManager.obtenerTodosPuestos().size();
            System.out.println("\n🅿️  PUESTOS:");
            System.out.println("   📄 Archivo: " + ConfigurationManager.getDataFilePath("puestos.json"));
            System.out.println("   📊 Registros: " + totalPuestos);
            System.out.println("   ✅ Estado: " + (totalPuestos > 0 ? "CARGADO ✓" : "VACIO (lista para usar)"));
        } catch (Exception e) {
            System.out.println("\n🅿️  PUESTOS:");
            System.out.println("   ❌ Error: " + e.getMessage());
        }

        // 3. Reservas
        try {
            JsonManagerReservaPago reservaManager = new JsonManagerReservaPago();
            int totalReservas = reservaManager.obtenerTodasReservas().size();
            System.out.println("\n📅 RESERVAS:");
            System.out.println("   📄 Archivo: " + ConfigurationManager.getDataFilePath("reservas.json"));
            System.out.println("   📊 Registros: " + totalReservas);
            System.out.println("   ✅ Estado: " + (totalReservas > 0 ? "CARGADO ✓" : "VACIO (lista para usar)"));
        } catch (Exception e) {
            System.out.println("\n📅 RESERVAS:");
            System.out.println("   ❌ Error: " + e.getMessage());
        }

        // 4. Pagos
        try {
            JsonManagerReservaPago pagoManager = new JsonManagerReservaPago();
            int totalPagos = pagoManager.obtenerTodosPagos().size();
            System.out.println("\n💰 PAGOS:");
            System.out.println("   📄 Archivo: " + ConfigurationManager.getDataFilePath("pagos.json"));
            System.out.println("   📊 Registros: " + totalPagos);
            System.out.println("   ✅ Estado: " + (totalPagos > 0 ? "CARGADO ✓" : "VACIO (lista para usar)"));
        } catch (Exception e) {
            System.out.println("\n💰 PAGOS:");
            System.out.println("   ❌ Error: " + e.getMessage());
        }
    }

    private static void mostrarInfoSistema() {
        System.out.println("\n💻 INFORMACIÓN DEL SISTEMA:");
        System.out.println("─".repeat(80));
        System.out.println("   ⏰ Hora de inicio: " + java.time.LocalDateTime.now());
        System.out.println("   🎯 Java Version: " + System.getProperty("java.version"));
        System.out.println("   💾 Memoria disponible: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        System.out.println("   📍 Directorio de trabajo: " + System.getProperty("user.dir"));
        System.out.println("   🔧 Spring Boot: 3.x");
        System.out.println("   🚀 Puerto: 8080 (predeterminado)");
    }

    private static void mostrarResumenFinal() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ APLICACIÓN INICIALIZADA CORRECTAMENTE");
        System.out.println("=".repeat(80));
        
        System.out.println("\n🌐 ENDPOINTS DISPONIBLES:");
        System.out.println("─".repeat(80));
        System.out.println("   🔗 API Clientes:     http://localhost:8080/clientes/api");
        System.out.println("   🔗 API Puestos:      http://localhost:8080/puestos/api");
        System.out.println("   🔗 API Reservas:     http://localhost:8080/reservas/api");
        System.out.println("   🔗 API Pagos:        http://localhost:8080/reservas/api/pagos");
        System.out.println("   🔗 API Reportes:     http://localhost:8080/reservas/api/reportes");
        System.out.println("\n   📱 Web Clientes:     http://localhost:8080/clientes");
        System.out.println("   📱 Web Puestos:      http://localhost:8080/puestos");
        System.out.println("   📱 Web Reservas:     http://localhost:8080/reservas");
        
        System.out.println("\n🔍 HERRAMIENTAS DE DIAGNÓSTICO:");
        System.out.println("─".repeat(80));
        System.out.println("   📊 Clientes:         http://localhost:8080/clientes/api/diagnostico");
        System.out.println("   🅿️  Puestos:          http://localhost:8080/puestos/api/debug/info");
        System.out.println("   📅 Reservas/Pagos:   http://localhost:8080/reservas/api/diagnostico");
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 SISTEMA LISTO PARA USAR - ESPERANDO SOLICITUDES");
        System.out.println("=".repeat(80));
    }
}