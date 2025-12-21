// crear-puesto.service.ts - CORREGIDO
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrearPuestoRequest, CrearPuestoResponse } from '../models/crear-puesto.model';
import { TipoPuesto, EstadoPuesto } from '../models/puestos.model';

@Injectable({
  providedIn: 'root'
})
export class CrearPuestoService {
  private apiUrl = 'http://localhost:8080/puestos/api';

  constructor(private http: HttpClient) {}

  crearPuesto(request: CrearPuestoRequest): Observable<CrearPuestoResponse> {
    return this.http.post<CrearPuestoResponse>(this.apiUrl, request);
  }

  obtenerTiposPuesto(): { value: TipoPuesto; label: string }[] {
    return [
      { value: TipoPuesto.REGULAR, label: 'Regular' },
      { value: TipoPuesto.DISCAPACITADO, label: 'Discapacitado' },
      { value: TipoPuesto.DOCENTE, label: 'Docente' },
      { value: TipoPuesto.VISITANTE, label: 'Visitante' },
      { value: TipoPuesto.MOTOCICLETA, label: 'Motocicleta' }
    ];
  }

  obtenerEstadosPuesto(): { value: EstadoPuesto; label: string }[] {
    return [
      { value: EstadoPuesto.DISPONIBLE, label: 'Disponible' },
      { value: EstadoPuesto.OCUPADO, label: 'Ocupado' },
      { value: EstadoPuesto.BLOQUEADO, label: 'Bloqueado' },
      { value: EstadoPuesto.MANTENIMIENTO, label: 'Mantenimiento' },
      { value: EstadoPuesto.RESERVADO, label: 'Reservado' }
    ];
  }
}
