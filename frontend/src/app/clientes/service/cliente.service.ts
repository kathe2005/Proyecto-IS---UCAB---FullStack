import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
<<<<<<< HEAD
import { Cliente } from '../models/cliente';
=======
import { Cliente } from '../models/cliente'; 
>>>>>>> f60515a7208837665d5d1ce4ef4e0fa1c47abdb1

@Injectable({
    providedIn: 'root'
})
export class ClienteService {

<<<<<<< HEAD
    // URL de Spring Boot
=======
    //URL de Sprint Boot 
>>>>>>> f60515a7208837665d5d1ce4ef4e0fa1c47abdb1
    private apiURL = 'http://localhost:8080/api/cliente';

    constructor(private http: HttpClient) { }

    registrarCliente(cliente: any): Observable<any> {
        return this.http.post(`${this.apiURL}/registrar`, cliente);
    }

<<<<<<< HEAD
    actualizarCliente(cliente: any): Observable<any> {
        return this.http.put(`${this.apiURL}/actualizar`, cliente);
=======
    actualizarCliente(cliente: Cliente): Observable<Cliente> 
    {   
        const usuarioClave = cliente.usuario; 
        return this.http.put<Cliente>(`${this.apiURL}/actualizar/${usuarioClave}`, cliente); 
>>>>>>> f60515a7208837665d5d1ce4ef4e0fa1c47abdb1
    }
}
