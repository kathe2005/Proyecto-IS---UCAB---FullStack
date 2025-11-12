//Comunicacion con la Base de Datos para ejecutar operaciones básicas
package com.ucab.estacionamiento.repository;

import org.springframework.stereotype.Repository;
import com.ucab.estacionamiento.model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository //Esta clase gestiona el almacenamiento de datos 
public class ClienteRepository {

    // Base de datos JSON simulada 
    private static final List<Cliente> BD_clientes = new ArrayList<>(List.of(
    // Cliente de prueba 1: UCAB (con correo UCAB)
    new Cliente() {{
    setUsuario("ucabtest");
    setTelefono("0414-1112233");
    setEmail("prueba@est.ucab.edu.ve");
    setCedula("V-12345678");
    setContrasena("Password123");
    setNombre("Juan");
    setApellido("Perez");
    setTipoPersona("UCAB");
    setDireccion("Av. Principal");
}}, 
    new Cliente() {{
    setUsuario("hola");
    setTelefono("0416-9310940");
    setEmail("prueba@gmail.com");
    setCedula("V-1569310");
    setContrasena("Consta1234");
    setNombre("Juan");
    setApellido("Perez");
    setTipoPersona("VISITANTE");
    setDireccion("Av. Principal La Hacienda");
}}
));
    
    //Metodo para guardar un nuevo usuario
    public Cliente guardar(Cliente cliente) {

        //Aquí se agrega un nuevo usuario a la lista 
        BD_clientes.add(cliente); 
        
        // Muestra un mensaje para confirmar que se guardó en la simulación
        System.out.println("El nuevo usuario se guardo correctamente");

        return cliente; 
    }

    // Metodo para encontrar todos los usuarios 
    public List<Cliente> findAll()
    {
        System.out.println("Todos los usuarios");
        return BD_clientes;
    }

    //Buscar
    //Usuario 
    public Optional<Cliente> findByUsuario(String usuarioBuscado)
    {
        System.out.println("Buscando usuario por usuario " + usuarioBuscado);

        //Iteramos la lista para buscar usuario
        for (Cliente u: BD_clientes)
        {
            if (u.getUsuario().equalsIgnoreCase(usuarioBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }

    //Contraseña
    public Optional<Cliente> findByContrasena(String contrasenaBuscado)
    {
        System.out.println("Buscando usuario por contrasena " + contrasenaBuscado);

        //Iteramos la lista para buscar contrasena 
        for (Cliente u: BD_clientes)
        {
            if (u.getContrasena().equalsIgnoreCase(contrasenaBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Nombre 
    public Optional<Cliente> findByNombre(String nombreBuscado)
    {
        System.out.println("Buscando usuario por nombre " + nombreBuscado);

        //Iteramos la lista para buscar nombre
        for (Cliente u: BD_clientes)
        {
            if (u.getNombre().equalsIgnoreCase(nombreBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Apellido 
    public Optional<Cliente> findByApellido(String apellidoBuscado)
    {
        System.out.println("Buscando usuario por apellido " + apellidoBuscado);

        //Iteramos la lista para buscar apellido
        for (Cliente u: BD_clientes)
        {
            if (u.getApellido().equalsIgnoreCase(apellidoBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Cedula 
    public Optional<Cliente> findByCedula(String cedulaBuscada)
    {
        System.out.println("Buscando usuario por cedula " + cedulaBuscada);

        //Iteramos la lista para buscar cedula
        for (Cliente u: BD_clientes)
        {
            if (u.getCedula().equalsIgnoreCase(cedulaBuscada))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Email 
    public Optional<Cliente> findByEmail(String emailBuscado)
    {
        System.out.println("Buscando usuario por email " + emailBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getEmail().equalsIgnoreCase(emailBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }

    //Direccion 
    public Optional<Cliente> findByDireccion(String direccionBuscado)
    {
        System.out.println("Buscando usuario por direccion " + direccionBuscado);

        //Iteramos la lista para buscar direccion
        for (Cliente u: BD_clientes)
        {
            if (u.getDireccion().equalsIgnoreCase(direccionBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Telefono
    public Optional<Cliente> findByTelefono(String telefonoBuscado)
    {
        System.out.println("Buscando usuario por telefono " + telefonoBuscado);

        //Iteramos la lista para buscar telefono
        for (Cliente u: BD_clientes)
        {
            if (u.getTelefono().equalsIgnoreCase(telefonoBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }

}
