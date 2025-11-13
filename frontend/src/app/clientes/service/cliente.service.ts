import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente'; 

@Injectable({
    providedIn: 'root'
})

export class ClienteService {

    //URL de Spring Boot (puerto por defecto del equipo / del proyecto)
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

    obtenerPerfil(usuario: string): Observable<Cliente> {
        return this.http.get<Cliente>(`${this.apiURL}/perfil/${usuario}`);
    }

    obtenerReservaActiva(usuario: string): Observable<any> {
        return this.http.get<any>(`${this.apiURL}/reserva-activa/${usuario}`);
    }

    obtenerZonaActual(usuario: string): Observable<any> {
        return this.http.get<any>(`${this.apiURL}/zona-actual/${usuario}`);
    }
}