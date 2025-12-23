import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
  tipoPersona?: string;  // 'UCAB' | 'VISITANTE'
  direccion?: string;
  fechaRegistro?: string;
  estado?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private baseUrl = 'http://localhost:8080/clientes/api';

  constructor(private http: HttpClient) {}

  // ✅ REGISTRAR CLIENTE
  registrarCliente(cliente: Cliente): Observable<any> {
    return this.http.post(`${this.baseUrl}`, cliente);
  }

  // ✅ OBTENER TODOS LOS CLIENTES
  consultarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.baseUrl}`);
  }

  // ✅ OBTENER CLIENTE POR ID
  obtenerClientePorId(id: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  // ✅ OBTENER CLIENTE POR USUARIO
  obtenerClientePorUsuario(usuario: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/usuario/${usuario}`);
  }

  // ✅ OBTENER CLIENTE POR CÉDULA
  obtenerClientePorCedula(cedula: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/cedula/${cedula}`);
  }

  // ✅ OBTENER CLIENTE POR EMAIL
  obtenerClientePorEmail(email: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/email/${email}`);
  }

  // ✅ VERIFICAR EXISTENCIA DE USUARIO
  existeUsuario(usuario: string): Observable<{existe: boolean}> {
    return this.http.get<{existe: boolean}>(`${this.baseUrl}/existe/usuario/${usuario}`);
  }

  // ✅ VERIFICAR EXISTENCIA DE EMAIL
  existeEmail(email: string): Observable<{existe: boolean}> {
    return this.http.get<{existe: boolean}>(`${this.baseUrl}/existe/email/${email}`);
  }

  // ✅ VERIFICAR EXISTENCIA DE CÉDULA
  existeCedula(cedula: string): Observable<{existe: boolean}> {
    return this.http.get<{existe: boolean}>(`${this.baseUrl}/existe/cedula/${cedula}`);
  }

  // ✅ ACTUALIZAR CLIENTE (PUT tradicional)
  actualizarCliente(id: string, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${id}`, cliente);
  }

  // ✅ MODIFICAR CLIENTE (PATCH para modificación parcial)
  modificarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.patch<Cliente>(`${this.baseUrl}/modificar`, cliente);
  }

  // ✅ ELIMINAR CLIENTE
  eliminarCliente(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // ✅ VALIDAR EMAIL
  validarEmail(email: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/validar/email/${email}`);
  }

  // ✅ VALIDAR USUARIO
  validarUsuario(usuario: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/validar/usuario/${usuario}`);
  }
}
