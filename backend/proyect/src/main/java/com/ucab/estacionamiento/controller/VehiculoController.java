package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.clases.Vehiculo;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.implement.ClienteServiceImpl;
import com.ucab.estacionamiento.implement.VehiculoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            return ResponseEntity.ok(nuevoVehiculo);

        }
        catch (Exception e)
        {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", e.getMessage());

            //--------    RETORNA EL MENSAJE DE ERROR Y UN STATUS 400 (BAD REQUEST)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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

    //--------    MODIFICAR LOS DATOS DE UN VEHICULO    --------
    @PutMapping("/modificar")
    public ResponseEntity<?> modificarVehiculo(@RequestBody Vehiculo vehiculo)
    {
        try{
            System.out.println("🔄 ACTUALIZACIÓN : " + vehiculo.getPlaca());

            vehiculoService.modificar(vehiculo);

            return ResponseEntity.ok("✅ El vehículo " + vehiculo.getPlaca() + " ha sido actualizado con éxito.");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error al modificar el vehículo: " + e.getMessage());

        }
    }

    //--------    ELIMINAR VEHICULO POR PLACA    --------
    @DeleteMapping("/eliminar/{placa}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable String placa)
    {
        try{
            System.out.println("🧨 ORDEN DE ELIMINACIÓN RECIBIDA: " + placa);

            vehiculoService.eliminar(placa);

            return ResponseEntity.ok("🗑️ El vehículo con placa [" + placa + "] fue removido.");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Fallo en la extracción: " + e.getMessage());
        }

    }


}
