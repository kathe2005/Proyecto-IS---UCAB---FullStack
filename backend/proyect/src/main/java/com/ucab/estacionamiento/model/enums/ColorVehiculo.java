package com.ucab.estacionamiento.model.enums;

public enum ColorVehiculo
{
    BLANCO, NEGRO, GRIS, ROJO, AZUL, PLATA, VERDE, AMARILLO;
    public static boolean esValido(String color)
    {
        for (ColorVehiculo c: ColorVehiculo.values())
        {
            if(c.name().equalsIgnoreCase(color.trim())) return true;
        }
        return false;
    }
}
