import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  id?: string;
  usuario: string;
  contrasena: string;
  confirmarContrasena?: string | null;
  cedula: string;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  tipoPersona?: string;
  direccion?: string;
  fechaRegistro?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ClienteService {
  // Base URL del backend (ajustar si es necesario)
  private baseUrl = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient) {}

  // Registrar nuevo cliente
  registrarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.baseUrl}`, cliente);
  }

  // Obtener todos los clientes
  consultarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.baseUrl}`);
  }

  // Obtener cliente(s) por cédula (query param 'cedula')
  obtenerClientePorCedula(cedula: string): Observable<Cliente[]> {
    const params = new HttpParams().set('cedula', cedula);
    return this.http.get<Cliente[]>(`${this.baseUrl}`, { params });
  }

  // Obtener cliente por usuario
  obtenerClientePorUsuario(usuario: string): Observable<Cliente | null> {
    return this.http.get<Cliente | null>(`${this.baseUrl}/usuario/${encodeURIComponent(usuario)}`);
  }

  // Actualizar cliente por usuario
  actualizarCliente(usuario: string, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/actualizar/${encodeURIComponent(usuario)}`, cliente);
  }

  // Login (si el backend expone /login o /auth/login)
  login(creds: { usuario: string; contrasena: string }): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/login`, creds);
  }
}
