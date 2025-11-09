import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Cliente } from '../models/cliente'; 

@Injectable({
    providedIn: 'root'
})

export class ClienteService {

    private apiURL = 'http://localhost:8080/api/clientes/registrar'; 
    private useMockData = false; 
    private mockFileUrl = '/assets/clientes-mock.json';

    constructor(private http: HttpClient) { }

    /**
   * Maneja errores para devolver un mensaje limpio al componente.
   */
    /** guardarCliente(cliente: Cliente): Observable<Cliente> {
    
    return this.http.post<Cliente>(this.apiURL, cliente).pipe(
        catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Error desconocido en el servidor.';
        
        // **MEJORA CLAVE:** Intenta obtener el mensaje del cuerpo del error
        if (error.error instanceof ErrorEvent) {
            // Error del lado del cliente/red
            errorMessage = `Error: ${error.error.message}`;
        } else if (error.error && error.error.message) {
            // Error del lado del servidor (Spring Boot)
            errorMessage = error.error.message;
        } else if (error.status === 400 && error.error) {
            // Intenta capturar el cuerpo del error si es un 400 (Bad Request)
            errorMessage = error.error; 
        } else {
            // Error con el status code
            errorMessage = `Código de error: ${error.status}`;
        }

        // Devolvemos el mensaje limpio al componente
        return throwError(() => new Error(errorMessage));
    })
    );
    }**/


    

    guardarCliente(cliente: Cliente): Observable<Cliente> {
    if (this.useMockData) {
        // --- MODO MOCK (ÉXITO SIMULADO) ---
        console.log("Usando datos de prueba (Mock JSON)");
        
        // Carga el JSON y lo devuelve como si fuera la respuesta del servidor
        return this.http.get<Cliente>(this.mockFileUrl).pipe(
            map((response: Cliente) => response) 
        );
    } else {
        // --- MODO REAL (SPRING BOOT) ---
        return this.http.post<Cliente>(this.apiURL, cliente).pipe(
            catchError((error: HttpErrorResponse) => {
                // Declarar la variable 'errorMessage' LOCALMENTE con 'let'
                let errorMessage = 'Error desconocido en el servidor.'; 
                
                // ... Aquí va la lógica para intentar extraer el error del servidor ...

                if (error.error instanceof ErrorEvent) {
                    errorMessage = `Error de red: ${error.error.message}`;
                } else if (error.error && error.error.message) {
                    errorMessage = error.error.message;
                } else {
                    errorMessage = `Código de error: ${error.status}. Intentando puerto: ${this.apiURL}`;
                }

                // Devolvemos el error envuelto en throwError
                return throwError(() => new Error(errorMessage));
            })
        );
    }
}
}