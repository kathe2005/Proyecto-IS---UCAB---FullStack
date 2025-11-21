import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteOcupacion, ReporteDetallado, EstadisticasRapidas } from '../models/reporte.model';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = 'http://localhost:8080/api/reportes';

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

  // Reporte diario para fecha específica
  obtenerReporteDiario(fecha: string): Observable<ReporteOcupacion> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<ReporteOcupacion>(`${this.apiUrl}/ocupacion/diario`, { params });
  }

  // Tendencia de los últimos 7 días
  obtenerTendenciaOcupacion(): Observable<ReporteOcupacion[]> {
    return this.http.get<ReporteOcupacion[]>(`${this.apiUrl}/ocupacion/tendencia`);
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
}
