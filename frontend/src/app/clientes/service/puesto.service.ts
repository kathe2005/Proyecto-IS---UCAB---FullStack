import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Puesto {
  id: string;
  numero: string;
  ubicacion: string;
  usuarioOcupante: string | null;
  tipoPuesto: string;
  estadoPuesto: string;
  fechaOcupacion: string | null;
  fechaCreacion: string;
  historialOcupacion: string[];
}

export interface OcuparPuestoRequest {
  puestoId: string;    // NOMBRE EXACTO que espera el backend
  usuario: string;     // NOMBRE EXACTO que espera el backend
}

@Injectable({
  providedIn: 'root'
})
export class PuestoService {
  private apiUrl = 'http://localhost:8080/puestos/api'; // ✅ AGREGADO /api

  constructor(private http: HttpClient) {}

  obtenerTodosLosPuestos(): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}`);
  }

  obtenerPuestoPorId(id: string): Observable<Puesto> {
    return this.http.get<Puesto>(`${this.apiUrl}/${id}`);
  }

  // OCUPAR PUESTO - CORREGIDO
  ocuparPuesto(request: OcuparPuestoRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/ocupar`, request);
  }

  // LIBERAR PUESTO - CORREGIDO
  liberarPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/liberar/${id}`, {});
  }

  // BLOQUEAR PUESTO - CORREGIDO
  bloquearPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/bloquear/${id}`, {});
  }

  // DESBLOQUEAR PUESTO - CORREGIDO
  desbloquearPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/desbloquear/${id}`, {});
  }

  // MANTENIMIENTO - CORREGIDO
  ponerEnMantenimiento(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/mantenimiento/${id}`, {});
  }

  // OBTENER POR ESTADO - CORREGIDO
  obtenerPuestosPorEstado(estado: string): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/estado`, {
      params: new HttpParams().set('estado', estado)
    });
  }

  // OBTENER POR TIPO - CORREGIDO
  obtenerPuestosPorTipo(tipo: string): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/tipo`, {
      params: new HttpParams().set('tipo', tipo)
    });
  }

  obtenerEstadisticas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/estadisticas`);
  }

  obtenerHistorial(id: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/historial/${id}`);
  }

  crearPuesto(puesto: any): Observable<Puesto> {
    return this.http.post<Puesto>(`${this.apiUrl}`, puesto);
  }

  actualizarPuesto(id: string, puesto: Puesto): Observable<Puesto> {
    return this.http.put<Puesto>(`${this.apiUrl}/${id}`, puesto);
  }

  eliminarPuesto(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
