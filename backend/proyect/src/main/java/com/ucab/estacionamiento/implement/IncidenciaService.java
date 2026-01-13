package com.ucab.estacionamiento.implement;

import com.ucab.estacionamiento.model.clases.Incidencia;
import com.ucab.estacionamiento.model.enums.EstadoIncidencia;
import java.util.List;
import java.util.Optional;

public interface IncidenciaService {
    
    Incidencia reportarIncidencia(Incidencia incidencia);
    List<Incidencia> obtenerTodasLasIncidencias();
    Optional<Incidencia> buscarIncidenciaPorId(int id);
    Incidencia cambiarEstadoIncidencia(int id, EstadoIncidencia nuevoEstado);
    boolean eliminarIncidencia(int id);

    // --- AGREGA ESTA LÍNEA NUEVA ---
    Incidencia actualizarIncidenciaCompleta(int id, Incidencia nuevosDatos);
}