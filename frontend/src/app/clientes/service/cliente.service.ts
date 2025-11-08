import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Cliente } from '../models/cliente'; 

@Injectable({
    providedIn: 'root', 
})

export class ClienteService {

    private apiURL = 'http://localhost:8080/api/clientes/registrar'; 

    constructor(private http: HttpClient) { }

    /**
   * Maneja errores para devolver un mensaje limpio al componente.
   */
    guardarCliente(cliente: Cliente): Observable<Cliente> {
    
    return this.http.post<Cliente>(this.apiURL, cliente).pipe(
        catchError( (error: HttpErrorResponse) => {
        let mensajeError = 'Error desconocido en el servidor.';
        
        //Mensaje de exepcion
        if (error.error && error.error.message) {
            mensajeError = error.error.message;
        }

        // Devolvemos el error en un formato que el componente pueda leer
        return throwError(() => new Error(mensajeError));
    })
    );
    }
}