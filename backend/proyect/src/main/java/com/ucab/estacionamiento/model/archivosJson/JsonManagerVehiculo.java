package com.ucab.estacionamiento.model.archivosJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ucab.estacionamiento.model.clases.Vehiculo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class JsonManagerVehiculo
{
    private static final String VEHICULOS_FILE = ConfigurationManager.getDataFilePath("vehiculos.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static
    {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    //--------    OPERACIONES CRUD VEHICULOS   --------
    //--------    METODO PARA REGISTRAR VEHICULOS   --------
    public Vehiculo guardarVehiculo(Vehiculo nuevoVehiculo)
    {
        try {

            List<Vehiculo> vehiculos = cargarVehiculos();

            if(nuevoVehiculo.getIdVehiculo()==null)
            {
                nuevoVehiculo.setIdVehiculo(UUID.randomUUID());
            }

            vehiculos.removeIf(v->v.getIdVehiculo().equals(nuevoVehiculo.getIdVehiculo()));

            vehiculos.add(nuevoVehiculo);
            guardarVehiculosEnArchivo(vehiculos);
        } catch (Exception e) {

            e.printStackTrace();

        }
        return nuevoVehiculo;
    }


    //--------    LISTAR VEHICULOS   --------
    public List<Vehiculo>cargarVehiculos()
    {
        try{

            File archivo = new File(VEHICULOS_FILE);
            if (!archivo.exists())
            {
                archivo.getParentFile().mkdirs();
                guardarVehiculosEnArchivo(new ArrayList<>());
                return new ArrayList<>();
            }

            if(archivo.length()==0) return new ArrayList<>();

            return objectMapper.readValue(archivo,objectMapper.getTypeFactory().constructCollectionType(List.class, Vehiculo.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    //--------    MODIFICAR LOS DATOS DE UN VEHICULO  --------
    public void modificar(Vehiculo actualizado)
    {
        List<Vehiculo> lista = cargarVehiculos();
        boolean encontrado = false;


        for(int i = 0; i < lista.size(); i++)
        {
            if(lista.get(i).getPlaca().equalsIgnoreCase(actualizado.getPlaca()))
            {
                lista.set(i, actualizado);
                encontrado = true;
                break;
            }
        }

        if(encontrado)
        {
            guardarVehiculosEnArchivo(lista);
            System.out.println("✅ Vehículo con placa " + actualizado.getPlaca() + " actualizado.");
        }
        else
        {
            System.out.println("⚠️ No se encontró el vehículo para modificar.");
        }
    }

    //--------    ELIMINAR UN VEHICULO DEL PERFIL DE USUARIO  --------
    public void eliminar(String placa)
    {
        List<Vehiculo> lista = cargarVehiculos();
        boolean eliminado = lista.removeIf(v -> v.getPlaca().equalsIgnoreCase(placa));

        if(eliminado)
        {
            guardarVehiculosEnArchivo(lista);
            System.out.println("🗑️ Activo con placa " + placa + " eliminado.");
        }
        else
        {
            System.out.println("❌ Error: Placa " + placa + " no existe en los registros.");
        }
    }




    //-------    METODO DE BUSQUEDA  Y DIAGNOSTICO  --------
    //Placa
    public Optional<Vehiculo> buscarPorPlaca(String placa)
    {
        return cargarVehiculos().stream().filter(v -> v.getPlaca().equalsIgnoreCase(placa)).findFirst();
    }

    //idCliente
    public List<Vehiculo> buscarPorIdCliente(UUID idCliente)
    {
        return cargarVehiculos().stream().filter(v -> v.getIdCliente().equals(idCliente)).toList();
    }

    public void diagnostico()
    {
        File archivo = new File(VEHICULOS_FILE);
        System.out.println("🩺 DIAGNÓSTICO VEHÍCULOS:");
        System.out.println("📁 Ruta Oficial: " + archivo.getAbsolutePath());
        System.out.println("🔍 Existe: " + archivo.exists());
        System.out.println("👥 Conteo: " + cargarVehiculos().size());
    }

    //--------    METODO PARA GUARDAR LOS VEHICULOS EN EL ARCHIVO   --------
    private void guardarVehiculosEnArchivo(List<Vehiculo> vehiculos)
    {

        String[] rutasDeseadas = {
                "data/vehiculos.json",
                "../../data/vehiculos.json"
        };

        for (String ruta : rutasDeseadas) {
            try {
                File archivoDestino = new File(ruta).getAbsoluteFile();


                objectMapper.writerWithDefaultPrettyPrinter().writeValue(archivoDestino, vehiculos);
                System.out.println("🚀 Copia guardada con éxito en: " + archivoDestino.getAbsolutePath());

            } catch (Exception e) {
                System.err.println("🚨 No se pudo guardar en la ruta: " + ruta);
                e.printStackTrace();
            }
        }
        // El diagnóstico lo dejamos al final para la ruta oficial
        diagnostico();
    }

}
