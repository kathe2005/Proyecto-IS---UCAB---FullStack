import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Puesto, TipoPuesto, EstadoPuesto } from '../models/puestos.model'; // Importar desde models
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

  ocuparPuesto(datosOcupacion: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/ocupar`, datosOcupacion);
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

  obtenerPuestosPorEstado(estado: string): Observable<Puesto[]> {
    return this.http.get<Puesto[]>(`${this.apiUrl}/estado`, {
      params: new HttpParams().set('estado', estado)
    });
  }

  obtenerPuestosPorTipo(tipo: string): Observable<Puesto[]> {
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

  crearPuesto(puesto: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, puesto);
  }
}
