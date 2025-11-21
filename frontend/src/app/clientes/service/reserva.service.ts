import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva, ReservaRequest, PuestosDisponiblesResponse, EstadoReserva } from '../models/reserva.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private apiUrl = 'http://localhost:8080/api/reservas';

  constructor(private http: HttpClient) {}

  // Consultar puestos disponibles por fecha y turno
  consultarPuestosDisponibles(fecha: string, turno: string): Observable<PuestosDisponiblesResponse> {
    const params = new HttpParams()
      .set('fecha', fecha)
      .set('turno', turno);

    return this.http.get<PuestosDisponiblesResponse>(`${this.apiUrl}/disponibles`, { params });
  }

  // Crear nueva reserva
  crearReserva(request: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.apiUrl, request);
  }

  // Cancelar reserva
  cancelarReserva(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/cancelar`, {});
  }

  // Confirmar reserva
  confirmarReserva(id: string): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.apiUrl}/${id}/confirmar`, {});
  }

  // Obtener reservas por cliente
  obtenerReservasPorCliente(clienteId: string): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  // Obtener reservas por fecha
  obtenerReservasPorFecha(fecha: string): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}/fecha/${fecha}`);
  }

  // Obtener reserva por ID
  obtenerReservaPorId(id: string): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.apiUrl}/${id}`);
  }

  // Obtener reservas pendientes
  obtenerReservasPendientes(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.apiUrl}/pendientes`);
  }

  // Obtener opciones de turno
  obtenerTurnos(): { value: string; label: string }[] {
    return [
      { value: 'MAÑANA', label: 'Mañana (6:00 - 14:00)' },
      { value: 'TARDE', label: 'Tarde (14:00 - 22:00)' },
      { value: 'NOCHE', label: 'Noche (22:00 - 6:00)' }
    ];
  }
}
