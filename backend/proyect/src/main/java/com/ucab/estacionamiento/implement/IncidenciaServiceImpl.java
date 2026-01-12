package com.ucab.estacionamiento.implement;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerIncidencias;
import com.ucab.estacionamiento.model.clases.Incidencia;
import com.ucab.estacionamiento.model.enums.EstadoIncidencia;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {

    private final JsonManagerIncidencias jsonManagerIncidencias;

    // Constructor: Inicializamos el JsonManager aquí, igual que en tu PuestoServiceImpl
    public IncidenciaServiceImpl() {
        this.jsonManagerIncidencias = new JsonManagerIncidencias();
    }

    @Override
    public Incidencia reportarIncidencia(Incidencia incidencia) {
        // Reglas de Negocio:
        // 1. Al crear, siempre debe estar NO_ATENDIDA
        incidencia.setEstado(EstadoIncidencia.NO_ATENDIDA);
        // 2. Asignamos la fecha y hora actual si no viene
        if (incidencia.getFechaRegistro() == null) {
            incidencia.setFechaRegistro(LocalDateTime.now());
        }
        
        System.out.println("📝 Registrando nueva incidencia de: " + incidencia.getClienteAfectado());
        return jsonManagerIncidencias.guardarIncidencia(incidencia);
    }

    @Override
    public List<Incidencia> obtenerTodasLasIncidencias() {
        return jsonManagerIncidencias.cargarIncidencias();
    }

    @Override
    public Optional<Incidencia> buscarIncidenciaPorId(int id) {
        return jsonManagerIncidencias.buscarIncidenciaPorId(id);
    }

    @Override
    public Incidencia cambiarEstadoIncidencia(int id, EstadoIncidencia nuevoEstado) {
        Optional<Incidencia> incidenciaOpt = jsonManagerIncidencias.buscarIncidenciaPorId(id);
        
        if (incidenciaOpt.isPresent()) {
            Incidencia incidencia = incidenciaOpt.get();
            incidencia.setEstado(nuevoEstado);
            System.out.println("🔄 Estado de incidencia " + id + " cambiado a " + nuevoEstado);
            return jsonManagerIncidencias.guardarIncidencia(incidencia);
        } else {
            throw new RuntimeException("Incidencia no encontrada con ID: " + id);
        }
    }

// ... tus otros métodos ...

    @Override
    public Incidencia actualizarIncidenciaCompleta(int id, Incidencia nuevosDatos) {
        Optional<Incidencia> incidenciaOpt = jsonManagerIncidencias.buscarIncidenciaPorId(id);

        if (incidenciaOpt.isPresent()) {
            Incidencia incidenciaExistente = incidenciaOpt.get();
            incidenciaExistente.setClienteAfectado(nuevosDatos.getClienteAfectado());
            incidenciaExistente.setTipoProblema(nuevosDatos.getTipoProblema());
            incidenciaExistente.setDescripcion(nuevosDatos.getDescripcion());
            incidenciaExistente.setEstado(nuevosDatos.getEstado());
            incidenciaExistente.setFechaRegistro(nuevosDatos.getFechaRegistro());
            System.out.println("🔄 Actualizando incidencia ID: " + id);
            return jsonManagerIncidencias.guardarIncidencia(incidenciaExistente);
        } else {
            throw new RuntimeException("No se encontró la incidencia con ID: " + id);
        }
    }
    @Override
    public boolean eliminarIncidencia(int id) {
        return jsonManagerIncidencias.eliminarIncidencia(id);
    }
}