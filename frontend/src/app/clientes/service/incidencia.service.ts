import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Incidencia } from '../models/incidencia.model';

@Injectable({
  providedIn: 'root'
})
export class IncidenciaService {

  private apiUrl = 'http://localhost:8080/incidencias'; 
  constructor(private http: HttpClient) { }
  // METODO 1: Obtener todas las incidencias
  obtenerIncidencias(): Observable<Incidencia[]> {
    return this.http.get<Incidencia[]>(`${this.apiUrl}`);
  }
  // METODO 2: Eliminar
  eliminarIncidencia(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  // METODO 3: Crear
  crearIncidencia(incidencia: any): Observable<Incidencia> {
    return this.http.post<Incidencia>(`${this.apiUrl}`, incidencia);
  }
  // METODO 4: Obtener por ID
  obtenerIncidenciaPorId(id: number): Observable<Incidencia> {
    return this.http.get<Incidencia>(`${this.apiUrl}/${id}`);
  }
  // METODO 5: Actualizar
  actualizarIncidencia(id: number, incidencia: any): Observable<Incidencia> {
    return this.http.put<Incidencia>(`${this.apiUrl}/${id}`, incidencia);
  }
}