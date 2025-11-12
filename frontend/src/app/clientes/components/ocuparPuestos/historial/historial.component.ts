import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { ActivatedRoute } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { Puesto, TipoPuestoInfo, EstadoPuestoInfo } from '../../../models/puestos.model';
import { NavigationComponent } from '../navegacion/navigation.component';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, NavigationComponent],
  templateUrl: './historial.component.html',
  styleUrls: ['./historial.component.css']
})
export class HistorialComponent implements OnInit {
  puesto: Puesto | null = null;
  historial: string[] = [];
  puestoId: string = '';

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
  }

  getTipoDescripcion(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.descripcion || tipo;
  }

  getTipoColor(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.color || 'gray';
  }

  getEstadoDescripcion(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.descripcion || estado;
  }

  getEstadoColor(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.color || 'gray';
  }
}
