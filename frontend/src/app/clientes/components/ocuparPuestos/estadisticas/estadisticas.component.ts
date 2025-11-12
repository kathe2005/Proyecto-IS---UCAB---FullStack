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
  estadisticas: any = {};

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    this.puestoService.obtenerEstadisticas().subscribe({
      next: (data) => {
        this.estadisticas = data;
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
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
