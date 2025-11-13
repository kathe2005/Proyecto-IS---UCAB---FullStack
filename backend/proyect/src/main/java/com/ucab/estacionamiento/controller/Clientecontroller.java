//Recibir el JSON 
package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.Cliente;
import com.ucab.estacionamiento.model.Puesto;
import com.ucab.estacionamiento.model.ResultadoOcupacion;
import com.ucab.estacionamiento.model.EstadoPuesto;
import com.ucab.estacionamiento.service.ClienteService;
import com.ucab.estacionamiento.service.PuestoService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
@RestController //Indica que esta clase maneja peticiones REST
@RequestMapping(value = "api/cliente", produces = "application/json") //URL base para todos los metodos 
@CrossOrigin(origins = "http://localhost:4200")
public class Clientecontroller {
    

    private final ClienteService clienteService; 
    private final PuestoService puestoService;

    
    public Clientecontroller(ClienteService clienteService, PuestoService puestoService)
    {
        this.clienteService = clienteService; 
        this.puestoService = puestoService;
    }

    @GetMapping("/obtenerTodo")
    public List<Cliente> obtenerTodosLosClientes() 
    {
        return clienteService.obtenerTodos(); 
    }

    //Endpoint para el registro con JSON 
    @PostMapping(value = "/registrar", consumes = "application/json")
    public Cliente registrarCliente( @RequestBody Cliente nuevoCliente)
    {
        return clienteService.registrarCliente(nuevoCliente);
    }


    @PutMapping(value = "/actualizar/{usuario}", consumes = "application/json")
    public ResponseEntity<Cliente> actualizarCliente(
        @PathVariable String usuario,  @RequestBody Cliente clienteActualizado) 
        {
                clienteActualizado.setUsuario(usuario); 
                
                Cliente clienteGuardado = clienteService.actualizarCliente(clienteActualizado);
                return new ResponseEntity<>(clienteGuardado, HttpStatus.OK);
        }

    // --- Endpoint: Obtener perfil (datos personales)
    @GetMapping("/perfil/{usuario}")
    public ResponseEntity<Cliente> obtenerPerfil(@PathVariable String usuario) {
        Cliente cliente = clienteService.obtenerPorUsuario(usuario);
        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    // --- Endpoint: Obtener reserva activa del usuario (si existe)
    @GetMapping("/reserva-activa/{usuario}")
    public ResponseEntity<ResultadoOcupacion> obtenerReservaActiva(@PathVariable String usuario) {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        for (Puesto p : puestos) {
            if (p.getUsuarioOcupante() != null && p.getUsuarioOcupante().equalsIgnoreCase(usuario)
                    && p.getEstadoPuesto() == EstadoPuesto.OCUPADO) {
                ResultadoOcupacion res = new ResultadoOcupacion(true, "Reserva activa encontrada", p);
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }
        ResultadoOcupacion sin = new ResultadoOcupacion(false, "No hay reserva activa para el usuario", null, "NO_RESERVA_ACTIVA");
        return new ResponseEntity<>(sin, HttpStatus.OK);
    }

    // --- Endpoint: Obtener zona de estacionamiento actual del usuario (ubicación)
    @GetMapping("/zona-actual/{usuario}")
    public ResponseEntity<Object> obtenerZonaActual(@PathVariable String usuario) {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        for (Puesto p : puestos) {
            if (p.getUsuarioOcupante() != null && p.getUsuarioOcupante().equalsIgnoreCase(usuario)
                    && p.getEstadoPuesto() == EstadoPuesto.OCUPADO) {
                // Devolver la ubicación y datos básicos del puesto
                return new ResponseEntity<>(p.getUbicacion(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("No hay zona de estacionamiento activa", HttpStatus.OK);
    }
}




