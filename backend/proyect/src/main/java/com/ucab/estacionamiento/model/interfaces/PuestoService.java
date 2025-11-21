package com.ucab.estacionamiento.model.interfaces;

import com.ucab.estacionamiento.model.*;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;

import java.util.List;
import java.util.Optional;

public interface PuestoService {
    List<Puesto> obtenerPuestos();
    Optional<Puesto> obtenerPuestoPorId(String idPuesto);
    Puesto crearPuesto(Puesto puesto);
    Puesto actualizarPuesto(Puesto puesto);
    boolean eliminarPuesto(String id);

    ResultadoOcupacion ocuparPuesto(String puestoId, String usuario);
    boolean liberarPuesto(String puestoId);

    List<Puesto> obtenerPuestosPorEstado(EstadoPuesto estado);
    List<Puesto> obtenerPuestosPorTipo(TipoPuesto tipo);
    List<Puesto> filtrarPuestosPorUbicacion(String ubicacion);
    int contarPuestosDisponibles();
    int contarPuestosOcupados();
    int contarPuestosReservados();
    int contarPuestosBloqueados();
    List<Puesto> obtenerPuestosBloqueados();

    boolean bloquearPuesto(String puestoId);
    boolean desbloquearPuesto(String puestoId);
    ResultadoOcupacion asignarPuestoManual(String puestoId, String usuario);
    Puesto reasignarPuesto(String puestoId, String nuevaUbicacion);

    boolean ponerPuestoEnMantenimiento(String puestoId);
    List<String> obtenerHistorial(String puestoId);

    ResultadoOcupacion ocuparPuesto(String puestoId, String usuario, String clienteId, String tipoCliente);
}
