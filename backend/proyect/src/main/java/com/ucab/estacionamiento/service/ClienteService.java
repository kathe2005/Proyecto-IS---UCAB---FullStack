//Procesar y Validar 

package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.Cliente;


import java.util.List;


public interface ClienteService {

    //Metodos 
    //Registrar un usuario 
    Cliente registrarCliente(Cliente nuevoCliente); 

    //Validar Formato 
    void validarFormatoContrasena(String contrasena); 
    void validarFormatoTelefono(String telefono); 
    void validarFormatoCedula(String cedula); 
    void validarEmailPorTipoPersona(String tipoPersona, String email); 
    String clasificarDominio (String email); 
    void validarSinEspacios( String valor, String nombreCampo); 

    //Todos los clientes 
    List<Cliente> obtenerTodos(); 

    Cliente actualizarCliente(Cliente clienteActualizado); 

}
