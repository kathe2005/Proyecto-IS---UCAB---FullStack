import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva, ReservaRequest, PuestosDisponiblesResponse } from '../models/reserva.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private apiUrl = 'http://localhost:8080/reservas/api';

  constructor(private http: HttpClient) {}

  consultarPuestosDisponibles(fecha: string, turno: string, clienteId?: string): Observable<any> {
    console.log(`🔍 Consultando puestos disponibles: ${fecha}, ${turno}, cliente: ${clienteId || 'todos'}`);

    let params = new HttpParams()
      .set('fecha', fecha)
      .set('turno', turno);

    if (clienteId) {
      params = params.set('clienteId', clienteId);
    }

    return this.http.get(`${this.apiUrl}/disponibles`, { params });
  }

  crearReserva(request: ReservaRequest): Observable<any> {
    console.log('📅 Creando reserva:', request);
    return this.http.post(this.apiUrl, request);
  }

  crearReservaSimple(puestoId: string, clienteId: string, usuario: string, fecha: string, turno: string): Observable<any> {
    const params = new HttpParams()
      .set('puestoId', puestoId)
      .set('clienteId', clienteId)
      .set('usuario', usuario)
      .set('fecha', fecha)
      .set('turno', turno);

    return this.http.post(`${this.apiUrl}/crear`, {}, { params });
  }

  cancelarReserva(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/cancelar`, {});
  }

  confirmarReserva(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/confirmar`, {});
  }

  activarReserva(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/activar`, {});
  }

  obtenerReservasPorCliente(clienteId: string): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  obtenerReservaPorId(id: string): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.apiUrl}/${id}`);
  }

  obtenerTodasLasReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}`);
  }

  obtenerTurnos(): { value: string; label: string }[] {
    return [
      { value: 'MAÑANA', label: 'Mañana (6:00 - 14:00)' },
      { value: 'TARDE', label: 'Tarde (14:00 - 22:00)' },
      { value: 'NOCHE', label: 'Noche (22:00 - 6:00)' }
    ];
  }

  diagnostico(): Observable<any> {
    return this.http.get(`${this.apiUrl}/diagnostico`);
  }
}
