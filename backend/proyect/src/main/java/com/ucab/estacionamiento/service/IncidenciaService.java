package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.clases.Incidencia;
import com.ucab.estacionamiento.model.enums.EstadoIncidencia;
import java.util.List;
import java.util.Optional;

public interface IncidenciaService {
    
    // Crear una nueva incidencia
    Incidencia reportarIncidencia(Incidencia incidencia);

    // Obtener todas para el panel de administración
    List<Incidencia> obtenerTodasLasIncidencias();

    // Buscar una específica
    Optional<Incidencia> buscarIncidenciaPorId(int id);

    // Cambiar el estado (Ej: de NO_ATENDIDA a ATENDIDA)
    Incidencia cambiarEstadoIncidencia(int id, EstadoIncidencia nuevoEstado);
    
    // Eliminar (Opcional, pero útil para limpiar pruebas)
    boolean eliminarIncidencia(int id);
}