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
    private static final List<Cliente> BD_clientes = new ArrayList<>();
    
    //Metodo para guardar un nuevo usuario
    public Cliente guardar(Cliente cliente) {

        //Aquí se agrega un nuevo usuario a la lista 
        BD_clientes.add(cliente); 
        
        // Muestra un mensaje para confirmar que se guardó en la simulación
        System.out.println("El nuevo usuario se guardo correctamente");

        return cliente; // Retornamos el objeto guardado
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
        System.out.println("Buscando usuario por email " + usuarioBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getusuario().equalsIgnoreCase(usuarioBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }

    //Contraseña
    public Optional<Cliente> findByContrasena(String contrasenaBuscado)
    {
        System.out.println("Buscando usuario por email " + contrasenaBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getcontrasena().equalsIgnoreCase(contrasenaBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Nombre 
    public Optional<Cliente> findByNombre(String nombreBuscado)
    {
        System.out.println("Buscando usuario por email " + nombreBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getnombre().equalsIgnoreCase(nombreBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Apellido 
    public Optional<Cliente> findByApellido(String apellidoBuscado)
    {
        System.out.println("Buscando usuario por email " + apellidoBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getapellido().equalsIgnoreCase(apellidoBuscado))
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
            if (u.getcedula().equalsIgnoreCase(cedulaBuscada))
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
            if (u.getemail().equalsIgnoreCase(emailBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }

    //Direccion 
    public Optional<Cliente> findByDireccion(String direccionBuscado)
    {
        System.out.println("Buscando usuario por email " + direccionBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.getdireccion().equalsIgnoreCase(direccionBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }


    //Telefono
    public Optional<Cliente> findByTelefono(String telefonoBuscado)
    {
        System.out.println("Buscando usuario por email " + telefonoBuscado);

        //Iteramos la lista para buscar email
        for (Cliente u: BD_clientes)
        {
            if (u.gettelefono().equalsIgnoreCase(telefonoBuscado))
            {
                return Optional.of(u); 
            }
        }

        return Optional.empty(); 
    }





}
