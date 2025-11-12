import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  menuItems = [
    { title: 'Inicio', route: '/home', icon: 'fas fa-home' },
    { title: 'Gestión de Puestos', route: '/puestos', icon: 'fas fa-list' },
    { title: 'Ocupar Puesto', route: '/puestos/ocupar', icon: 'fas fa-parking' },
    { title: 'Estadísticas', route: '/puestos/estadisticas', icon: 'fas fa-chart-bar' },
    { title: 'Buscar Puestos', route: '/puestos/buscar', icon: 'fas fa-search' },
    { title: 'Crear Puestos', route: '/puestos/crear', icon: 'fas fa-plus' }
  ];
}
