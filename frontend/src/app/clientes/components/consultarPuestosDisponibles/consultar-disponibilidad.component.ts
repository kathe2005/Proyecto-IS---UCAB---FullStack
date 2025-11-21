import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReservaService } from '../../service/reserva.service';
import { PuestosDisponiblesResponse } from '../../models/reserva.model';
import { Puesto } from '../../models/puestos.model';

@Component({
  selector: 'app-consultar-disponibilidad',
  templateUrl: './consultar-disponibilidad.component.html',
  styleUrls: ['./consultar-disponibilidad.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class ConsultarDisponibilidadComponent implements OnInit {

  // Filtros de búsqueda
  fecha: string = '';
  turno: string = 'MAÑANA';

  // Resultados
  disponibilidadResult: PuestosDisponiblesResponse | null = null;
  puestosDisponibles: Puesto[] = [];

  // Estados
  cargando: boolean = false;
  mostrarResultados: boolean = false;
  error: string = '';

  // Opciones
  turnos = [
    { value: 'MAÑANA', label: 'Mañana (6:00 - 14:00)' },
    { value: 'TARDE', label: 'Tarde (14:00 - 22:00)' },
    { value: 'NOCHE', label: 'Noche (22:00 - 6:00)' }
  ];

  constructor(
    private reservaService: ReservaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fecha = this.getFechaActual();
  }

  // Método para obtener fecha actual
  getFechaActual(): string {
    return new Date().toISOString().split('T')[0];
  }

  consultarDisponibilidad(): void {
    if (!this.fecha || !this.turno) {
      this.error = 'Por favor, seleccione fecha y turno';
      return;
    }

    this.cargando = true;
    this.error = '';
    this.mostrarResultados = false;

    this.reservaService.consultarPuestosDisponibles(this.fecha, this.turno)
      .subscribe({
        next: (result) => {
          this.disponibilidadResult = result;
          this.puestosDisponibles = result.puestos;
          this.mostrarResultados = true;
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error al consultar disponibilidad:', error);
          this.error = 'Error al consultar la disponibilidad. Por favor, intente nuevamente.';
          this.cargando = false;
        }
      });
  }

  limpiarFiltros(): void {
    this.fecha = this.getFechaActual();
    this.turno = 'MAÑANA';
    this.mostrarResultados = false;
    this.error = '';
  }

  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }

  // Nuevo método para ir a reservar puesto
  irAReservar(): void {
    this.router.navigate(['/reservas/crear']);
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'DISPONIBLE': return 'estado-disponible';
      case 'OCUPADO': return 'estado-ocupado';
      case 'BLOQUEADO': return 'estado-bloqueado';
      case 'MANTENIMIENTO': return 'estado-mantenimiento';
      default: return 'estado-desconocido';
    }
  }

  getTipoClass(tipo: string): string {
    switch (tipo) {
      case 'REGULAR': return 'tipo-regular';
      case 'DISCAPACITADO': return 'tipo-discapacitado';
      case 'DOCENTE': return 'tipo-docente';
      case 'VISITANTE': return 'tipo-visitante';
      case 'MOTOCICLETA': return 'tipo-motocicleta';
      default: return 'tipo-desconocido';
    }
  }
}
