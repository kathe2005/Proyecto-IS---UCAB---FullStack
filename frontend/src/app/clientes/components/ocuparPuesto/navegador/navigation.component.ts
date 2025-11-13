import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  menuItems = [
    { route: '/inicio', icon: 'fas fa-home', title: 'Inicio' },
    { route: '/puestos', icon: 'fas fa-list', title: 'Gestión de Puestos' },
    { route: '/puestos/ocupar', icon: 'fas fa-parking', title: 'Ocupar Puesto' },
    { route: '/puestos/crear', icon: 'fas fa-plus', title: 'Crear Puesto' },
    { route: '/puestos/estadisticas', icon: 'fas fa-chart-bar', title: 'Estadísticas' },
    { route: '/puestos/buscar', icon: 'fas fa-search', title: 'Buscar Puestos' }
  ];
}
