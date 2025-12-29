import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehiculo } from '../models/vehiculo.model';

@Injectable({
    providedIn: 'root'
})
export class VehiculoService
{
    private baseUrl = 'http://localhost:8080/vehiculos'; 

    constructor(private http: HttpClient){}

    //--------    REGISTRAR VEHICULO    --------
    registrarVehiculo(vehiculo: Vehiculo): Observable<any> 
    {
        return this.http.post(`${this.baseUrl}/registrar`, vehiculo, { responseType: 'text'}); 
    }

    //--------    CONSULTAR TODOS LOS VEHICULOS    --------
    consultarVehiculos(): Observable<Vehiculo[]>
    {
        return this.http.get<Vehiculo[]>(`${this.baseUrl}/listar`); 
    }

    //--------    VERIFICAR SI EXISTE PLACA    --------
    existePlaca(placa:String): Observable<{existe:Boolean}>
    {
        return this.http.get<{existe:Boolean}>(`${this.baseUrl}/existe/${placa}`); 
    }


    //--------    OBTENER LISTA DE CLIENTES    --------
    obtenerClientesParaVehiculo(): Observable<any[]> 
    {
        return this.http.get<any[]>('http://localhost:8080/clientes/api'); 
    }



    //--------    OBTENER LISTA DE VEHICULOS    --------
    obtenerVehiculos(): Observable<Vehiculo[]> 
    {
        return this.http.get<Vehiculo[]>(`${this.baseUrl}/listar`);
    }
}