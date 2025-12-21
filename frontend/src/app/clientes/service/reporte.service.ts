import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteOcupacion, ReporteDetallado, EstadisticasRapidas } from '../models/reporte.model';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  // CORREGIDO: El backend tiene /reservas/api/reportes, no /api/reportes
  private apiUrl = 'http://localhost:8080/reservas/api/reportes';

  constructor(private http: HttpClient) {}

  // Reporte por fecha y turno
  obtenerReporteOcupacion(fecha: string, turno: string): Observable<ReporteOcupacion> {
    const params = new HttpParams()
      .set('fecha', fecha)
      .set('turno', turno);

    return this.http.get<ReporteOcupacion>(`${this.apiUrl}/ocupacion`, { params });
  }

  // Reporte del día actual
  obtenerReporteHoy(): Observable<ReporteOcupacion> {
    return this.http.get<ReporteOcupacion>(`${this.apiUrl}/ocupacion/hoy`);
  }

  // Tendencia de los últimos 7 días
  obtenerTendenciaOcupacion(): Observable<ReporteOcupacion[]> {
    return this.http.get<ReporteOcupacion[]>(`${this.apiUrl}/tendencia`);
  }

  // Reporte detallado con información de clientes
  obtenerReporteDetallado(fecha: string, turno: string): Observable<ReporteDetallado> {
    const params = new HttpParams()
      .set('fecha', fecha)
      .set('turno', turno);

    return this.http.get<ReporteDetallado>(`${this.apiUrl}/ocupacion/detallado`, { params });
  }

  // Estadísticas rápidas
  obtenerEstadisticasRapidas(): Observable<EstadisticasRapidas> {
    return this.http.get<EstadisticasRapidas>(`${this.apiUrl}/estadisticas`);
  }

  // Reporte por tipo de puesto
  obtenerReportePorTipoPuesto(fecha: string): Observable<any> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get(`${this.apiUrl}/tipo-puesto`, { params });
  }

  // Reporte comparativo
  obtenerReporteComparativo(fecha1: string, fecha2: string): Observable<any> {
    const params = new HttpParams()
      .set('fecha1', fecha1)
      .set('fecha2', fecha2);

    return this.http.get(`${this.apiUrl}/comparativo`, { params });
  }

  // Diagnóstico
  diagnostico(): Observable<any> {
    return this.http.get(`${this.apiUrl}/diagnostico`);
  }
}
