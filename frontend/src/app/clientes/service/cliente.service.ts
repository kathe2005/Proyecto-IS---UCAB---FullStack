import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

    actualizarCliente(Cliente: any): Observable<any> 
    {
        return this.http.put(`${this.apiURL}/actualizar`, Cliente); 
    }
}