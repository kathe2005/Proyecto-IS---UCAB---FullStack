import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente'; 

@Injectable({
    providedIn: 'root'
})

export class ClienteService {

    //URL de Sprint Boot 
    private apiURL = 'http://localhost:8080/api/clientes/registrar'; 

    constructor(private http: HttpClient) { }

    registrarCliente(cliente: Cliente): Observable<Cliente>
    {
        console.log("Enviando datos a Sprint Boot: ", cliente);

        //Convierte automaticamente el objeto en un JSON 
        return this.http.post<Cliente>(this.apiURL, cliente);

    }
}