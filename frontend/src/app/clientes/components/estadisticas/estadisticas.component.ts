import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PuestoService } from '../../service/puesto.service';

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './estadisticas.component.html',
  styleUrls: ['./estadisticas.component.css']
})
export class EstadisticasComponent implements OnInit {
  estadisticas: any = {
    total: 0,
    disponibles: 0,
    ocupados: 0,
    bloqueados: 0,
    mantenimiento: 0,
    porcentajeOcupacion: 0
  };

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    this.puestoService.obtenerEstadisticas().subscribe({
      next: (res: any) => {
        this.estadisticas.total = res.total ?? 0;
        this.estadisticas.disponibles = res.disponibles ?? (res.puestosDisponibles ?? 0);
        this.estadisticas.ocupados = res.ocupados ?? 0;
        this.estadisticas.bloqueados = res.bloqueados ?? 0;
        this.estadisticas.mantenimiento = res.mantenimiento ?? 0;
        this.estadisticas.porcentajeOcupacion = this.estadisticas.total > 0
          ? Math.round((this.estadisticas.ocupados / this.estadisticas.total) * 100 * 10) / 10
          : 0;
      },
      error: (err) => {
        console.error('Error cargando estadísticas:', err);
        // Mantener valores por defecto (0) si falla
      }
    });

  }

  volverAPuestos() {
    this.router.navigate(['/puestos']);
  }

  irAInicio() {
    this.router.navigate(['/']);
  }
}
