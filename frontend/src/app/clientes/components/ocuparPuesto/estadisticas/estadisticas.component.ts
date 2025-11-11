import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component';

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, HeaderComponent, NavigationComponent],
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
    // Datos de ejemplo - reemplaza con servicio real
    this.estadisticas = {
      total: 150,
      disponibles: 85,
      ocupados: 45,
      bloqueados: 15,
      mantenimiento: 5,
      porcentajeOcupacion: 30
    };

    // Descomenta para usar el servicio real:
    /*
    this.puestoService.obtenerEstadisticas().subscribe({
      next: (data) => {
        this.estadisticas = data;
        // Calcular porcentaje si no viene del servicio
        if (data.total > 0) {
          this.estadisticas.porcentajeOcupacion = (data.ocupados / data.total) * 100;
        }
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        // Mantener datos de ejemplo en caso de error
      }
    });
    */
  }

  volverAPuestos() {
    this.router.navigate(['/puestos']);
  }

  irAInicio() {
    this.router.navigate(['/']);
  }
}
