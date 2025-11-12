import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Puesto, TipoPuesto, EstadoPuesto } from '../models/puestos.model';
import { OcuparPuestoRequest } from '../models/ocupar-puesto-request.model';
import { ResultadoOcupacion } from '../models/resultado-ocupacion.model';

@Injectable({
  providedIn: 'root'
})
export class PuestoService {
  private apiUrl = 'http://localhost:8080/api/puestos';

  constructor(private http: HttpClient) {}

  obtenerTodosLosPuestos(): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(this.apiUrl);
  }

  obtenerPuestoPorId(id: string): Observable<Puesto> {
    return this.http.get<Puesto>(`${this.apiUrl}/${id}`);
  }

  ocuparPuesto(request: OcuparPuestoRequest): Observable<ResultadoOcupacion> {
    return this.http.post<ResultadoOcupacion>(`${this.apiUrl}/ocupar`, request);
  }

  liberarPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/liberar/${id}`, {});
  }

  bloquearPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/bloquear/${id}`, {});
  }

  desbloquearPuesto(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/desbloquear/${id}`, {});
  }

  ponerEnMantenimiento(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/mantenimiento/${id}`, {});
  }

  obtenerPuestosPorEstado(estado: EstadoPuesto): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/estado`, {
      params: new HttpParams().set('estado', estado)
    });
  }

  obtenerPuestosPorTipo(tipo: TipoPuesto): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/tipo`, {
      params: new HttpParams().set('tipo', tipo)
    });
  }

  filtrarPuestosPorUbicacion(ubicacion: string): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/ubicacion`, {
      params: new HttpParams().set('ubicacion', ubicacion)
    });
  }

  obtenerEstadisticas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/estadisticas`);
  }

  obtenerHistorial(id: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/historial/${id}`);
  }
}
