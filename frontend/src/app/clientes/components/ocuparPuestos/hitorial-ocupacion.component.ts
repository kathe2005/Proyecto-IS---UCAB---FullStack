import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PuestoService } from '../../service/puesto.service';
import { Puesto, TipoPuestoInfo, EstadoPuestoInfo } from '../../models/puestos.model';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container-fluid">
      <div *ngIf="puesto" class="card mb-4 mx-4 shadow">S
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0">
            <i class="fas fa-history"></i> Historial del Puesto
            <span>{{ puesto.numero }}</span>
          </h4>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-6">
              <p><strong>ID:</strong> <span>{{ puesto.id }}</span></p>
              <p><strong>Ubicación:</strong> <span>{{ puesto.ubicacion }}</span></p>
            </div>
            <div class="col-md-6">
              <p><strong>Tipo:</strong>
                <span class="badge"
                      [style.background-color]="getTipoColor(puesto.tipoPuesto)"
                      style="color: white;">
                  {{ getTipoDescripcion(puesto.tipoPuesto) }}
                </span>
              </p>
              <p><strong>Estado:</strong>
                <span class="badge"
                      [style.background-color]="getEstadoColor(puesto.estadoPuesto)"
                      style="color: white;">
                  {{ getEstadoDescripcion(puesto.estadoPuesto) }}
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="card mx-4 shadow">
        <div class="card-header bg-primary text-white">
          <h5 class="mb-0">
            <i class="fas fa-list"></i> Registros de Historial
            <span class="badge bg-secondary">{{ historial.length }}</span>
          </h5>
        </div>
        <div class="card-body">
          <div *ngIf="historial.length === 0" class="text-center py-4">
            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
            <h4 class="text-muted">No hay registros en el historial</h4>
          </div>

          <div *ngIf="historial.length > 0" class="list-group">
            <div *ngFor="let registro of historial; let i = index"
                 class="list-group-item list-group-item-action">
              <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1">{{ registro }}</h6>
                <small class="text-muted">#{{ i + 1 }}</small>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-4 mx-4">
        <button (click)="volverAPuestos()" class="btn btn-outline-primary">
          <i class="fas fa-arrow-left"></i> Volver a Puestos
        </button>
      </div>
    </div>
  `,
  styles: [`
    .list-group-item {
      border: none;
      border-bottom: 1px solid #e9ecef;
      transition: background-color 0.3s ease;
    }
    .list-group-item:hover {
      background-color: rgba(28, 85, 255, 0.1);
    }
  `]
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

  volverAPuestos() {
    window.location.href = '/puestos';
  }
}
