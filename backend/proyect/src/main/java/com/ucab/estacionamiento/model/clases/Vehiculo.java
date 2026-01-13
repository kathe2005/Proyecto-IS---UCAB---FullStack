package com.ucab.estacionamiento.model.clases;

import java.util.UUID;

public class Vehiculo
{
    //--------    ATRIBUTOS DEL VEHICULO    --------
    private UUID idVehiculo; //ID propio del Vehiculo
    private String placa; //Placa del Vehiculo
    private String marca; //Marca del Vehiculo
    private String modelo; //Modelo del Vehiculo
    private String color; //Color del Vehículo



    //--------    ATRIBUTO CLAVE PARA RELACIÓN CON EL USUARIO    --------
    private UUID idCliente;


    //--------    GETTERS Y SETTERS    --------
    //idVehiculo
    public UUID getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(UUID idVehiculo) {
        this.idVehiculo = idVehiculo;
    }


    //Placa
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }


    //Marca
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


    //Modelo
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }


    //Color
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    //idCliente
    public UUID getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(UUID idCliente) {
        this.idCliente = idCliente;
    }
}
