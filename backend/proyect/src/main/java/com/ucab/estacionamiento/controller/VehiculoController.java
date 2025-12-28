package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Vehiculo;
import com.ucab.estacionamiento.service.VehiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
@CrossOrigin(origins = "*") //-------    PERMITE QUE EL FRONTEND SE CONECTE    --------

public class VehiculoController
{
    @Autowired
    private VehiculoServiceImpl vehiculoService;

    //--------    REGISTRAR VEHICULO    --------
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVehiculo(@RequestBody Vehiculo vehiculo)
    {
        try{
            //--------    ENVIA EL PAQUETE AL SERVICE    --------
            Vehiculo nuevoVehiculo = vehiculoService.registrarVehiculo(vehiculo);

            //--------    RETORNA EL VEHICULO Y UN STATUS 201 (CREATE)    --------
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVehiculo);

        }
        catch (Exception e)
        {
            //--------    RETORNA EL MENSAJE DE ERROR Y UN STATUS 400 (BAD REQUEST)
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    //--------    LISTA DE LOS VEHICULOS    --------
    @GetMapping("/listar")
    public ResponseEntity<List<Vehiculo>> obtenerTodos()
    {
        return ResponseEntity.ok(vehiculoService.obtenerTodosVehiculos());
    }

}
