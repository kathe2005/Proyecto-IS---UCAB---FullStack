package com.proyectIS.proyect.Pruebas.vehiculo;


import com.ucab.estacionamiento.application.ProyectApplication;
import com.ucab.estacionamiento.model.clases.Vehiculo;
import com.ucab.estacionamiento.service.VehiculoServiceImpl;
import org.junit.jupiter.api.Test;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

//--------    DEFINIR EL ENTORNO SPRING BOOT    --------
@SpringBootTest(classes = ProyectApplication.class)
public class VehiculoControllerTest
{
    private static final Logger log = LoggerFactory.getLogger(VehiculoControllerTest.class);


    //--------    INYECTAR EL SERVICIO    --------
    @Autowired
    private VehiculoServiceImpl vehiculoService;

    //--------    CREAR EL METODO DE PLAY    --------
    @Test
    void testRegistrarVehiculo() throws Exception
    {
        Vehiculo v = new Vehiculo();
        v.setIdCliente(UUID.fromString("e58428ef-4dc5-454b-aeb9-ca5b000a0421"));
        v.setPlaca("AF048SG");
        v.setMarca("Toyota");
        v.setModelo("Corolla");
        v.setColor("ROJO");

        //--------    EJECUTA LA LOGICA PURA    --------
        vehiculoService.registrarVehiculo(v);
        log.info("--------    \uD83C\uDFAF Vehículo registrado exitosamente    --------");
        log.info("\uD83D\uDCCB Detalles: {}", v);
    }

}
