package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.Cliente;
import java.util.List;

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