package com.ucab.estacionamiento.model.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    ReporteOcupacion generarReporteOcupacion(LocalDate fecha, String turno);
    ReporteOcupacion generarReporteDiario(LocalDate fecha);
    List<ReporteOcupacion> generarReporteTendencia();
    ReporteOcupacion generarReporteHoy();
}
