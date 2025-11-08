//Comunicacion con la Base de Datos para ejecutar operaciones básicas
package com.ucab.estacionamiento.repository;

import com.ucab.estacionamiento.model.Cliente;

public interface  ClienteRepository {

    //Metodo para guardar 
    Cliente save(Cliente cliente); 

    //Metodo de existe cedula
    boolean existsByCedula(String cedula); 
}
