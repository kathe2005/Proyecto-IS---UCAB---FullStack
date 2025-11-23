package com.ucab.estacionamiento.model.archivosJson;

import java.util.List;
import java.util.Optional;

import com.ucab.estacionamiento.model.clases.Cliente;

/**
 * Port interface for Cliente repository to allow DI by interface and decouple
 * services from the concrete JSON-backed implementation.
 */
public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);
    Cliente guardar(Cliente cliente);
    List<Cliente> findAll();
    Optional<Cliente> findByUsuario(String usuario);
    Optional<Cliente> findByCedula(String cedula);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
    void clearAll();
    void diagnostico();
}
