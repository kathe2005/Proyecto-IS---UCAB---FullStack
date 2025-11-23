import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { PuestoService } from '../../service/puesto.service';

// Interfaces temporales
interface Puesto {
  id: string;
  numero: string;
  tipoPuesto: string;
  estadoPuesto: string;
  ubicacion: string;
}

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './historial.component.html',
  styleUrls: ['./historial.component.css']
})
export class HistorialComponent implements OnInit {
  puesto: Puesto | null = null;
  historial: string[] = [];
  puestoId: string = '';

  // Datos de ejemplo
  private datosEjemplo: Puesto[] = [
    { id: '1', numero: 'A-01', tipoPuesto: 'REGULAR', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona A' },
    { id: '2', numero: 'A-02', tipoPuesto: 'DISCAPACITADO', estadoPuesto: 'OCUPADO', ubicacion: 'Zona A' }
  ];

  private historialEjemplo: {[key: string]: string[]} = {
    '1': [
      '2025-01-15 08:30:00 - Puesto ocupado por usuario: juan.perez',
      '2025-01-15 12:45:00 - Puesto liberado',
      '2025-01-16 09:15:00 - Puesto ocupado por usuario: maria.garcia',
      '2025-01-16 17:20:00 - Puesto liberado'
    ],
    '2': [
      '2025-01-14 10:00:00 - Puesto bloqueado por mantenimiento',
      '2025-01-16 14:30:00 - Puesto desbloqueado',
      '2025-01-17 08:45:00 - Puesto ocupado por usuario: carlos.lopez'
    ]
  };

  constructor(
    private puestoService: PuestoService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.puestoId = params['id'];
      this.cargarHistorial();
    });
  }

  cargarHistorial() {
    // Simulación de datos - reemplaza con servicios reales
    this.puesto = this.datosEjemplo.find(p => p.id === this.puestoId) || null;
    this.historial = this.historialEjemplo[this.puestoId] || [];

    // Descomenta para usar servicios reales:
    /*
    this.puestoService.obtenerPuestoPorId(this.puestoId).subscribe({
      next: (puesto) => {
        this.puesto = puesto;
      },
      error: (error) => {
        console.error('Error cargando puesto:', error);
      }
    });

    this.puestoService.obtenerHistorial(this.puestoId).subscribe({
      next: (historial) => {
        this.historial = historial;
      },
      error: (error) => {
        console.error('Error cargando historial:', error);
      }
    });
    */
  }

  getTipoDescripcion(tipo: string): string {
    const tipos: {[key: string]: string} = {
      'REGULAR': 'Regular',
      'DISCAPACITADO': 'Discapacitado',
      'DOCENTE': 'Docente',
      'VISITANTE': 'Visitante',
      'MOTOCICLETA': 'Motocicleta'
    };
    return tipos[tipo] || tipo;
  }

  getTipoColor(tipo: string): string {
    const colores: {[key: string]: string} = {
      'REGULAR': '#007bff',
      'DISCAPACITADO': '#6f42c1',
      'DOCENTE': '#28a745',
      'VISITANTE': '#ffc107',
      'MOTOCICLETA': '#fd7e14'
    };
    return colores[tipo] || '#6c757d';
  }

  getEstadoDescripcion(estado: string): string {
    const estados: {[key: string]: string} = {
      'DISPONIBLE': 'Disponible',
      'OCUPADO': 'Ocupado',
      'BLOQUEADO': 'Bloqueado',
      'MANTENIMIENTO': 'Mantenimiento'
    };
    return estados[estado] || estado;
  }

  getEstadoColor(estado: string): string {
    const colores: {[key: string]: string} = {
      'DISPONIBLE': '#28a745',
      'OCUPADO': '#ffc107',
      'BLOQUEADO': '#6c757d',
      'MANTENIMIENTO': '#fd7e14'
    };
    return colores[estado] || '#6c757d';
  }
}
