package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerVehiculo;
import com.ucab.estacionamiento.model.clases.Vehiculo;
import com.ucab.estacionamiento.model.enums.ColorVehiculo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

public class VehiculoServiceImpl
{
    private final JsonManagerVehiculo jsonManagerVehiculo = new JsonManagerVehiculo();

    //--------    METODO REGISTRAR VEHICULO --------
    public Vehiculo registrarVehiculo(Vehiculo vehiculo) throws Exception
    {

        validarFormatoVehiculo(vehiculo);


        //--------    GUARDAR VEHICULO    --------
        return jsonManagerVehiculo.guardarVehiculo(vehiculo);

    }

    //--------    METODO PARA LISTAR LOS VEHICULOS    --------
    public List<Vehiculo>obtenerTodosVehiculos()
    {
        try {

            List<Vehiculo> lista = jsonManagerVehiculo.cargarVehiculos();

            if (lista == null)
            {
                return new ArrayList<>();
            }

            return lista;
        }
        catch (Exception e) {

            System.out.println("⚠️ Error en el búnker: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    //--------    METODO DE MODIFICAR LOS DATOS DE UN VEHICULO    --------
    public void modificar(Vehiculo vehiculoActualizado) throws Exception
    {
        validarFormatoVehiculo(vehiculoActualizado);

        jsonManagerVehiculo.modificar(vehiculoActualizado);
    }

    //--------    METODO DE ELIMINAR UN VEHICULO DEL PERFIL DE USUARIO    --------
    public void eliminar(String placa) throws Exception
    {
        if(placa == null || placa.isBlank())
        {
            throw new Exception("ERROR: No se puede eliminar un vehículo sin placa.");
        }

        jsonManagerVehiculo.eliminar(placa);
    }

    //--------    METODO PRIVADO DE SEGURIDAD    --------
    public void validarFormatoVehiculo(Vehiculo vehiculo) throws Exception
    {
        //--------    VALIDACIONES DE CAMPOS    --------

        //IdCliente
        if(vehiculo.getIdCliente() == null )
        {
            throw new Exception("VALIDACIÓN: No se puede registrar un vehiculo sin un cliente asociado. El ID del cliente es obligatorio");
        }

        //Placa
        if(vehiculo.getPlaca() == null || vehiculo.getPlaca().isBlank())
        {
            throw new Exception("VALIDACIÓN: La placa es obligatoria");
        }

        //Marca
        if (vehiculo.getMarca() == null || vehiculo.getMarca().isBlank())
        {
            throw new Exception("VALIDACIÓN: La marca es obligatoria");
        }

        //Modelo
        if (vehiculo.getModelo() == null || vehiculo.getModelo().isBlank())
        {
            throw new Exception("VALIDACIÓN: El modelo es obligatoria");
        }

        //Color
        if (vehiculo.getColor() == null || vehiculo.getColor().isBlank())
        {
            throw new Exception("VALIDACIÓN: El color es obligatoria");
        }




        //---------    VALIDACIÓN DE FORMATO (REGEX)    --------
        //Placa
        String placaLimpia = vehiculo.getPlaca().replace("-", "").toUpperCase().trim();
        if(!placaLimpia.matches("^[A-Z0-9]{6,7}$"))
        {
            throw new Exception("ERROR: Formato de placa invalido. La placa debe tener entre 6 y 7 caracteres alfanuméricos (Ej: AF048SG o DADO085)");
        }
        vehiculo.setPlaca(placaLimpia);

        //Marca
        String marcaLimpia = vehiculo.getMarca();
        if(!marcaLimpia.matches("^[a-zA-Z0-9\\s-]{2,20}$"))
        {
            throw new Exception("ERROR: Formato de marca invalida. La marca (Ej: Toyota) debe ser alfanumérica y tener entre 3 y 15 caracteres.");
        }
        vehiculo.setMarca(marcaLimpia);

        //Modelo
        String modeloLimpia = vehiculo.getModelo();
        if(!modeloLimpia.matches("^[a-zA-Z0-9\\s-]{2,20}$"))
        {
            throw new Exception("ERROR: Formato de modelo invalido. El modelo (Ej: Encava) debe ser alfanumérico y tener entre 3 y 15 caracteres.");
        }
        vehiculo.setModelo(modeloLimpia);

        //Color
        if (!ColorVehiculo.esValido(vehiculo.getColor()))
        {
            throw new Exception("ERROR: Color no permitido");
        }



        //--------    VALIDACIÓN DE LONGITUD    --------
        //Marca
        if (vehiculo.getMarca().length() < 3 || vehiculo.getMarca().length() > 15)
        {
            throw new Exception("VALIDACIÓN: La marca debe tener entre 3 y 15 caracteres");
        }

        //Modelo
        if (vehiculo.getModelo().length() < 3 || vehiculo.getModelo().length() > 15)
        {
            throw new Exception("VALIDACIÓN: El modelo debe tener entre 3 y 15 caracteres");
        }

        //Color
        if (vehiculo.getColor().length() < 3 || vehiculo.getColor().length() > 15)
        {
            throw new Exception("VALIDACIÓN: El nombre del color tiene una longitud inválida");
        }




        //--------    VERIFICACIÓN DE CAMPOS    --------

        List<Vehiculo>todosLosVehiculos = jsonManagerVehiculo.cargarVehiculos();

        if (todosLosVehiculos == null) todosLosVehiculos = new ArrayList<>();


        for(Vehiculo v:todosLosVehiculos)
        {
            if (v.getIdVehiculo() != null && vehiculo.getIdVehiculo() != null
                    && v.getIdVehiculo().equals(vehiculo.getIdVehiculo())) {
                continue;
            }

            //--------    VERIFICACIÓN DE IdCliente (Un cliente, un vehículo)    --------
            if(v.getIdCliente().equals(vehiculo.getIdCliente()))
            {
                throw new Exception("VEFIFICACIÓN: Este cliente ya tiene un vehículo asignado");
            }

            //--------    VERIFICACIÓN DE Placa    --------
            if(v.getPlaca().equalsIgnoreCase(vehiculo.getPlaca()))
            {
                throw new Exception("VEFIFICACIÓN: Esta placa ya esta registrada en el sistema");
            }
        }

    }

}
