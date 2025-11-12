import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component'; // ✅ Importa NavigationComponent

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HeaderComponent,
    NavigationComponent // ✅ Agrega NavigationComponent aquí
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  totalPuestos = 0;
  disponibles = 0;
  ocupados = 0;

  menuItems = [
    { route: '/puestos', icon: 'fas fa-list', title: 'Ver Todos los Puestos', description: 'Consulta el estado de todos los puestos' },
    { route: '/puestos/ocupar', icon: 'fas fa-parking', title: 'Ocupar Puesto', description: 'Asignar un puesto disponible' },
    { route: '/puestos/disponibles', icon: 'fas fa-check', title: 'Puestos Disponibles', description: 'Ver puestos libres' },
    { route: '/puestos/ocupados', icon: 'fas fa-car', title: 'Puestos Ocupados', description: 'Ver puestos en uso' },
    { route: '/puestos/estadisticas', icon: 'fas fa-chart-bar', title: 'Estadísticas', description: 'Métricas del estacionamiento' },
    { route: '/puestos/buscar', icon: 'fas fa-search', title: 'Buscar Puestos', description: 'Búsqueda avanzada' }
  ];

  constructor(private puestoService: PuestoService) {}

  ngOnInit() {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    this.puestoService.obtenerEstadisticas().subscribe({
      next: (data) => {
        this.totalPuestos = data.total;
        this.disponibles = data.disponibles;
        this.ocupados = data.ocupados;
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        // Datos de ejemplo en caso de error
        this.totalPuestos = 150;
        this.disponibles = 85;
        this.ocupados = 65;
      }
    });
  }
}
