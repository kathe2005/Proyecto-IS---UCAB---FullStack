import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente'; 

@Injectable({
    providedIn: 'root'
})

export class ClienteService {

    //URL de Sprint Boot 
    private apiURL = 'http://localhost:8080/api/cliente';

    constructor(private http: HttpClient) { }

    registrarCliente(Cliente: any): Observable<any> 
    {
        return this.http.post(`${this.apiURL}/registrar`,Cliente); 
    }

    actualizarCliente(cliente: Cliente): Observable<Cliente> 
    {   
        const usuarioClave = cliente.usuario; 
        return this.http.put<Cliente>(`${this.apiURL}/actualizar/${usuarioClave}`, cliente); 
    }
}