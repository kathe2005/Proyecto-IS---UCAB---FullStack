package com.ucab.estacionamiento.model.interfaces;

import java.util.List;

import com.ucab.estacionamiento.model.clases.Cliente;

public interface ClienteService {
    // Métodos 
    Cliente registrarCliente(Cliente nuevoCliente); 
    void validarFormatoContrasena(String contrasena); 
    void validarFormatoTelefono(String telefono); 
    void validarFormatoCedula(String cedula); 
    void validarEmailPorTipoPersona(String tipoPersona, String email); 
    String clasificarDominio(String email); 
    void validarSinEspacios(String valor, String nombreCampo); 
    List<Cliente> obtenerTodos(); 
    Cliente actualizarCliente(Cliente clienteActualizado); 
}