import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  id?: string;
  usuario: string;
  contrasena: string;
  confirmarContrasena: string;
  cedula: string;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  tipoPersona: string;
  direccion: string;
  fechaRegistro?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = 'http://localhost:8080/api/clientes'; // Spring Boot URL

  constructor(private http: HttpClient) {}

  // Registrar nuevo cliente
  registrarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/registrar`, cliente);
  }

  // Obtener todos los clientes
  consultarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/consultar`);
  }

  // Obtener cliente por cédula
  obtenerClientePorCedula(cedula: string): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/consultar?cedula=${cedula}`);
  }
}
