//Comunicacion con la Base de Datos para ejecutar operaciones básicas
package com.ucab.estacionamiento.repository;

import com.ucab.estacionamiento.JsonFileUtil;
import org.springframework.stereotype.Repository;
import com.ucab.estacionamiento.model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository //Esta clase gestiona el almacenamiento de datos 
public class ClienteRepository {

    // Base de datos JSON simulada 
    private static final List<Cliente> BD_clientes; 

    // Bloque estático para inicializar la lista cargando desde JSON o usando datos de prueba
    static {
        // La clase JsonFileUtil está en un paquete diferente, asegúrate que el import sea correcto.
        List<Cliente> loadedClientes = JsonFileUtil.loadClientes(); // Intenta cargar
        
        if (loadedClientes != null) {
            // Si la carga fue exitosa, usa los datos cargados
            BD_clientes = loadedClientes;
        } else {
            // Si la carga falló o el archivo no existe, usa datos iniciales
            System.out.println("? Creando datos iniciales de clientes...");
            BD_clientes = new ArrayList<>(List.of(
                // Cliente de prueba 1: UCAB
                new Cliente() {{ 
                    setUsuario("ucabtest");
                    setTelefono("0414-1112233");
                    setEmail("prueba@est.ucab.edu.ve");
                    setCedula("V-12345678");
                    setContrasena("Password123");
                    setNombre("Juan");
                    setApellido("Perez"); // Inicial con datos base
                    setTipoPersona("UCAB");
                    setDireccion("Av. Principal");
                }}, 
                // Cliente de prueba 2: VISITANTE
                new Cliente() {{ 
                    setUsuario("hola");
                    setTelefono("0416-9310940");
                    setEmail("prueba@gmail.com");
                    setCedula("V-1569310");
                    setContrasena("Consta1234");
                    setNombre("Pedro");
                    setApellido("Gomez");
                    setTipoPersona("VISITANTE");
                    setDireccion("Av. Principal La Hacienda");
                }}
            ));
            // Guardar estos datos iniciales si no había archivo
            JsonFileUtil.saveClientes(BD_clientes);
        }
        System.out.println("? Servicio de Clientes inicializado con " + BD_clientes.size() + " clientes.");
    }


    
    //Metodo para guardar un nuevo usuario
    public Cliente guardar(Cliente cliente) {

        //Aquí se agrega un nuevo usuario a la lista 
        BD_clientes.add(cliente); 
        
        // Muestra un mensaje para confirmar que se guardó en la simulación
        System.out.println("El nuevo usuario se guardo correctamente");

        return cliente; 
    }


    public Cliente save(Cliente cliente) 
    {
    
        // Verificar si el cliente ya existe (Búsqueda por Usuario, que es la clave)
        Optional<Cliente> existenteOpt = findByUsuario(cliente.getUsuario());

        if (existenteOpt.isPresent()) 
        {
        // --- ACTUALIZACIÓN ---
            Cliente clienteExistente = existenteOpt.get();
        
            // Encontrar el índice del objeto existente en la lista
            int index = BD_clientes.indexOf(clienteExistente);
        
            // Reemplazar el objeto existente con el objeto modificado
            if (index != -1) 
            {
                BD_clientes.set(index, cliente);
                System.out.println("El usuario " + cliente.getUsuario() + " se ha actualizado correctamente.");
            } else
            {
                // Esto no debería pasar si findByUsuario funciona bien, pero es una seguridad
                System.err.println("Error: El cliente existente no se encontró en la lista para actualizar.");
            }
        
        } else 
        {
            // --- NUEVO REGISTRO ---
            BD_clientes.add(cliente); 
            System.out.println("El nuevo usuario se ha guardado correctamente.");
        }

        // 2. Después de modificar BD_clientes (ya sea ADD o SET), ¡GUARDAMOS!
        JsonFileUtil.saveClientes(BD_clientes); 

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
