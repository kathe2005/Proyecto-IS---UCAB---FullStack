import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente';

@Injectable({
    providedIn: 'root'
})
export class ClienteService {

    private apiURL = 'http://localhost:8080/api/clientes';

    constructor(private http: HttpClient) { }

    registrarCliente(cliente: Cliente): Observable<Cliente> {
        return this.http.post<Cliente>(`${this.apiURL}/registrar`, cliente);
    }

    consultarClientes(): Observable<Cliente[]> {
        return this.http.get<Cliente[]>(`${this.apiURL}/consultar`);
    }

    // NUEVO: Obtener cliente por ID
    obtenerClientePorId(id: string): Observable<Cliente> {
        return this.http.get<Cliente>(`${this.apiURL}/${id}`);
    }

    // NUEVO: Modificar cliente
    modificarCliente(id: string, cliente: Cliente): Observable<Cliente> {
        return this.http.put<Cliente>(`${this.apiURL}/${id}`, cliente);
    }
}
