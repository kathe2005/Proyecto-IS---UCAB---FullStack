package com.ucab.estacionamiento.model.clases;

import java.time.LocalDate;
import java.util.List;

public class PuestosDisponiblesResponse {
    private LocalDate fecha;
    private String turno;
    private int totalPuestos;
    private int puestosDisponibles;
    private List<Puesto> puestos;
    private String mensaje;

    public PuestosDisponiblesResponse(LocalDate fecha, String turno, 
                                    int totalPuestos, int puestosDisponibles, 
                                    List<Puesto> puestos) {
        this.fecha = fecha;
        this.turno = turno;
        this.totalPuestos = totalPuestos;
        this.puestosDisponibles = puestosDisponibles;
        this.puestos = puestos;
        this.mensaje = String.format("Se encontraron %d puestos disponibles de %d totales para el %s", 
                                   puestosDisponibles, totalPuestos, turno);
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public int getTotalPuestos() { return totalPuestos; }
    public void setTotalPuestos(int totalPuestos) { this.totalPuestos = totalPuestos; }

    public int getPuestosDisponibles() { return puestosDisponibles; }
    public void setPuestosDisponibles(int puestosDisponibles) { this.puestosDisponibles = puestosDisponibles; }

    public List<Puesto> getPuestos() { return puestos; }
    public void setPuestos(List<Puesto> puestos) { this.puestos = puestos; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}