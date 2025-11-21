package com.ucab.estacionamiento.model.interfaces;

import java.time.LocalDate;
import java.util.Map;

public interface ReporteOcupacion {
    LocalDate getFecha();
    String getTurno();
    int getTotalPuestos();
    int getPuestosOcupados();
    int getPuestosDisponibles();
    double getPorcentajeOcupacion();
    Map<String, Integer> getOcupacionPorTipo();
    Map<String, Integer> getOcupacionPorUbicacion();
}