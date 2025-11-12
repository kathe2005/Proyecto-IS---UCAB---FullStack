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

    registrarCliente(cliente: any): Observable<any> {
    return this.http.post(this.apiURL, cliente)
}
}
