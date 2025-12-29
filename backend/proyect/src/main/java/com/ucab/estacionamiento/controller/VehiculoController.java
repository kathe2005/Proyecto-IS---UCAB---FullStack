package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Vehiculo;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.service.ClienteServiceImpl;
import com.ucab.estacionamiento.service.VehiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
public class VehiculoController
{
    @Autowired
    private VehiculoServiceImpl vehiculoService;

    //--------    REGISTRAR VEHICULO    --------
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVehiculo(@RequestBody Vehiculo vehiculo)
    {

        try{

            System.out.println("========================================");
            System.out.println("🚨 NUEVOS VEHICULOS REGISTRADOS 🚨");
            System.out.println("Placa: " + vehiculo.getPlaca());
            System.out.println("Marca: " + vehiculo.getMarca());
            System.out.println("Modelo: " + vehiculo.getModelo());
            System.out.println("Color: " + vehiculo.getColor());
            System.out.println("Asignado al Cliente ID: " + vehiculo.getIdCliente());
            System.out.println("========================================");


            //--------    ENVIA EL PAQUETE AL SERVICE    --------
            Vehiculo nuevoVehiculo = vehiculoService.registrarVehiculo(vehiculo);

            //--------    RETORNA EL VEHICULO Y UN STATUS 201 (CREATE)    --------
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVehiculo);

        }
        catch (Exception e)
        {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", e.getMessage());

            //--------    RETORNA EL MENSAJE DE ERROR Y UN STATUS 400 (BAD REQUEST)
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }
    }

    //--------    LISTA DE LOS VEHICULOS    --------
    @GetMapping("/listar")
    public ResponseEntity<List<Vehiculo>> obtenerTodos()
    {
        //--------    LLAMAMOS AL METODO CONCRETO    --------
        List<Vehiculo> listaReal = vehiculoService.obtenerTodosVehiculos();

        //--------    RETORNA LA LISTA    --------
        return ResponseEntity.ok(listaReal);
    }

}
