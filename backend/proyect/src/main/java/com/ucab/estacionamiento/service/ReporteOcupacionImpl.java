package com.ucab.estacionamiento.service;



import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class ReporteOcupacionImpl {
    private LocalDate fecha;
    private String turno;
    private int totalPuestos;
    private int puestosOcupados;
    private int puestosDisponibles;
    private double porcentajeOcupacion;
    private Map<String, Integer> ocupacionPorTipo;
    private Map<String, Integer> ocupacionPorUbicacion;

    // Constructor
    public ReporteOcupacionImpl(LocalDate fecha, String turno) {
        this.fecha = fecha;
        this.turno = turno;
        this.ocupacionPorTipo = new HashMap<>();
        this.ocupacionPorUbicacion = new HashMap<>();
    }

    // Builder pattern para construcción fluida
    public static class Builder {
        private LocalDate fecha;
        private String turno;
        private int totalPuestos;
        private int puestosOcupados;
        private Map<String, Integer> ocupacionPorTipo = new HashMap<>();
        private Map<String, Integer> ocupacionPorUbicacion = new HashMap<>();

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder turno(String turno) {
            this.turno = turno;
            return this;
        }

        public Builder totalPuestos(int totalPuestos) {
            this.totalPuestos = totalPuestos;
            return this;
        }

        public Builder puestosOcupados(int puestosOcupados) {
            this.puestosOcupados = puestosOcupados;
            return this;
        }

        public Builder ocupacionPorTipo(Map<String, Integer> ocupacionPorTipo) {
            this.ocupacionPorTipo = ocupacionPorTipo;
            return this;
        }

        public Builder ocupacionPorUbicacion(Map<String, Integer> ocupacionPorUbicacion) {
            this.ocupacionPorUbicacion = ocupacionPorUbicacion;
            return this;
        }

        public ReporteOcupacionImpl build() {
            ReporteOcupacionImpl reporte = new ReporteOcupacionImpl(fecha, turno);
            reporte.setTotalPuestos(totalPuestos);
            reporte.setPuestosOcupados(puestosOcupados);
            reporte.setPuestosDisponibles(totalPuestos - puestosOcupados);
            reporte.setOcupacionPorTipo(ocupacionPorTipo);
            reporte.setOcupacionPorUbicacion(ocupacionPorUbicacion);
            reporte.calcularPorcentaje();
            return reporte;
        }
    }

    // Getters
    public LocalDate getFecha() { return fecha; }

    public String getTurno() { return turno; }

    public int getTotalPuestos() { return totalPuestos; }

    public int getPuestosOcupados() { return puestosOcupados; }

    public int getPuestosDisponibles() { return puestosDisponibles; }

    public double getPorcentajeOcupacion() { return porcentajeOcupacion; }

    public Map<String, Integer> getOcupacionPorTipo() { return ocupacionPorTipo; }

    public Map<String, Integer> getOcupacionPorUbicacion() { return ocupacionPorUbicacion; }

    // Setters
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public void setTurno(String turno) { this.turno = turno; }

    public void setTotalPuestos(int totalPuestos) { 
        this.totalPuestos = totalPuestos; 
        this.calcularPorcentaje();
    }

    public void setPuestosOcupados(int puestosOcupados) { 
        this.puestosOcupados = puestosOcupados; 
        this.calcularPorcentaje();
    }

    public void setPuestosDisponibles(int puestosDisponibles) { 
        this.puestosDisponibles = puestosDisponibles; 
    }

    public void setPorcentajeOcupacion(double porcentajeOcupacion) { 
        this.porcentajeOcupacion = porcentajeOcupacion; 
    }

    public void setOcupacionPorTipo(Map<String, Integer> ocupacionPorTipo) { 
        this.ocupacionPorTipo = ocupacionPorTipo; 
    }

    public void setOcupacionPorUbicacion(Map<String, Integer> ocupacionPorUbicacion) { 
        this.ocupacionPorUbicacion = ocupacionPorUbicacion; 
    }

    private void calcularPorcentaje() {
        if (totalPuestos > 0) {
            this.porcentajeOcupacion = (double) puestosOcupados / totalPuestos * 100;
            // Redondear a 2 decimales
            this.porcentajeOcupacion = Math.round(porcentajeOcupacion * 100.0) / 100.0;
        } else {
            this.porcentajeOcupacion = 0.0;
        }
    }

    public String toString() {
        return String.format("ReporteOcupacion{fecha=%s, turno='%s', ocupados=%d/%d (%.1f%%)}", 
                fecha, turno, puestosOcupados, totalPuestos, porcentajeOcupacion);
    }
}
