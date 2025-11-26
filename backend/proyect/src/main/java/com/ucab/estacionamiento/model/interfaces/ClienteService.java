package com.ucab.estacionamiento.model.interfaces;

import java.util.List;
import java.util.Optional;

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
    public Optional<Cliente> obtenerPorUsuario(String usuario);
    public Optional<Cliente> obtenerPorCedula(String cedula);
    public Optional<Cliente> obtenerPorEmail(String email);
    public Optional<Cliente> obtenerPorTelefono(String telefono);
     
}