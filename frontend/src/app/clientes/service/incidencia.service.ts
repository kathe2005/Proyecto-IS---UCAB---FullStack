import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Incidencia } from '../models/incidencia.model';

@Injectable({
  providedIn: 'root'
})
export class IncidenciaService {

  // URL Base: Apunta a la clase Controller (@RequestMapping("/incidencias"))
  private apiUrl = 'http://localhost:8080/incidencias'; 

  constructor(private http: HttpClient) { }

  // 1. OBTENER TODAS (GET /incidencias/api)
  obtenerIncidencias(): Observable<Incidencia[]> {
    // CORREGIDO: Agregado "/api" para coincidir con tu Java
    return this.http.get<Incidencia[]>(`${this.apiUrl}/api`);
  }

  // 2. REPORTAR / CREAR (POST /incidencias/api/reportar)
  crearIncidencia(incidencia: any): Observable<Incidencia> {
    // CORREGIDO: Agregado "/api/reportar" (Aquí estaba tu error de conexión)
    return this.http.post<Incidencia>(`${this.apiUrl}/api/reportar`, incidencia);
  }

  // 3. ELIMINAR (DELETE /incidencias/api/eliminar/{id})
  eliminarIncidencia(id: number): Observable<void> {
    // CORREGIDO: Agregado "/api/eliminar/"
    return this.http.delete<void>(`${this.apiUrl}/api/eliminar/${id}`);
  }

  // 4. ACTUALIZAR (PUT /incidencias/api/{id})
  actualizarIncidencia(id: number, incidencia: any): Observable<Incidencia> {
    // CORREGIDO: Agregado "/api/"
    return this.http.put<Incidencia>(`${this.apiUrl}/api/${id}`, incidencia);
  }

  // 5. OBTENER POR ID (Necesario para Editar)
  // NOTA: Para que esto funcione, necesitarás agregar este método en tu Controller Java
  // (Ver paso 2 abajo si te da error al editar)
  obtenerIncidenciaPorId(id: number): Observable<Incidencia> {
    return this.http.get<Incidencia>(`${this.apiUrl}/api/${id}`);
  }
}