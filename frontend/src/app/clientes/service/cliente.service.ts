import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente';

@Injectable({
    providedIn: 'root'
})
export class ClienteService {

    // URL de Spring Boot
    private apiURL = 'http://localhost:8080/api/cliente';

    constructor(private http: HttpClient) { }

    registrarCliente(cliente: any): Observable<any> {
        return this.http.post(`${this.apiURL}/registrar`, cliente);
    }

    actualizarCliente(cliente: any): Observable<any> {
        return this.http.put(`${this.apiURL}/actualizar`, cliente);
    }
}
