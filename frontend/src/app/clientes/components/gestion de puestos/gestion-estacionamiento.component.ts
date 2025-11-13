import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../ocuparPuesto/header/header.component';
import { NavigationComponent } from '../ocuparPuesto/navegador/navigation.component';

@Component({
  selector: 'app-gestion-estacionamiento',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HeaderComponent,
    NavigationComponent
  ],
  templateUrl: './gestion-estacionamiento.component.html',
  styleUrls: ['./gestion-estacionamiento.component.css']
})
export class GestionEstacionamientoComponent {

  menuCards = [
    {
      title: '👤 Registrar Cliente',
      description: 'Registrar nuevos clientes en el sistema',
      route: '/registrar-cliente',
      icon: 'fas fa-user-plus',
      color: 'primary',
      badge: 'Nuevo'
    },
    {
      title: '🚗 Ocupar Puesto',
      description: 'Asignar puestos de estacionamiento a clientes',
      route: '/puestos/ocupar',
      icon: 'fas fa-parking',
      color: 'success',
      badge: 'Disponible'
    },
    {
      title: '➕ Crear Puesto',
      description: 'Agregar nuevos puestos de estacionamiento',
      route: '/puestos/crear',
      icon: 'fas fa-plus-circle',
      color: 'info',
      badge: 'Administrar'
    },
    {
      title: '📋 Consultar Perfil',
      description: 'Ver y editar información de clientes',
      route: '/consultar-perfil',
      icon: 'fas fa-id-card',
      color: 'warning',
      badge: 'Consulta'
    },
    {
      title: '📊 Gestión de Puestos',
      description: 'Ver todos los puestos y su estado actual',
      route: '/puestos',
      icon: 'fas fa-list',
      color: 'secondary',
      badge: 'Todos'
    },
    {
      title: '🔍 Buscar Puestos',
      description: 'Búsqueda avanzada de puestos disponibles',
      route: '/puestos/buscar',
      icon: 'fas fa-search',
      color: 'dark',
      badge: 'Búsqueda'
    },
    {
      title: '📈 Estadísticas',
      description: 'Métricas y reportes del estacionamiento',
      route: '/puestos/estadisticas',
      icon: 'fas fa-chart-bar',
      color: 'primary',
      badge: 'Analítica'
    },
    {
      title: '🔄 Historial',
      description: 'Registro de actividad del estacionamiento',
      route: '/puestos/historial',
      icon: 'fas fa-history',
      color: 'info',
      badge: 'Registros'
    }
  ];

  // Estadísticas rápidas
  estadisticas = {
    totalPuestos: 150,
    disponibles: 85,
    ocupados: 45,
    porcentajeOcupacion: 30
  };

  constructor() { }
}
