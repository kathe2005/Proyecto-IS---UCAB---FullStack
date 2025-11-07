package com.proyectIS.proyect.controlador; 

import com.proyectIS.proyect.cliente.cliente.Cliente; 
import com.proyectIS.proyect.cliente.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 
import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/clientes") 
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST})
public class ClienteControlador
{
    @Autowired 
    private ClienteService clienteService; 

    /** * Registra un nuevo cliente
    * Usa el servicio para aplicar la lógica de hashing a la contraseña.
    */
    @PostMapping
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente cliente)
    {
        // 2. 🔑 CAMBIO CLAVE: Usamos el método guardarCliente del servicio
        Cliente nuevoCliente = clienteService.guardarCliente(cliente); 
        
        // Es buena práctica de seguridad NO devolver el hash de la contraseña en la respuesta HTTP
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED); 
    }

    /**
    * Obtiene todos los clientes registrados 
    */
    @GetMapping 
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes()
    {
        // Usamos el servicio para acceder a los datos
        List<Cliente> clientes = clienteService.obtenerTodosLosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
    
}