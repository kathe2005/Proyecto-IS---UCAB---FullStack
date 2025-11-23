import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // AÑADIDO: Importar CommonModule
import { Router } from '@angular/router';

interface OpcionMenu {
  id: string;
  titulo: string;
  ruta: string;
  color: string;
  icono: string;
  descripcion: string;
}

@Component({
  selector: 'app-gestion-puestos',
  standalone: true,
  imports: [CommonModule], // AÑADIDO: Importar CommonModule para *ngFor
  templateUrl: './gestion-puestos.component.html',
  styleUrls: ['./gestion-puestos.component.css']
})
export class GestionPuestosComponent {

  opciones: OpcionMenu[] = [
    {
      id: 'card1',
      titulo: 'Crear Puesto',
      ruta: '/puestos/crear',
      color: 'green',
      icono: 'fas fa-plus-circle',
      descripcion: 'Agregar nuevo puesto de estacionamiento'
    },
    {
      id: 'card2',
      titulo: 'Desocupar Puesto',
      ruta: '/puestos/desocupar',
      color: 'yellow',
      icono: 'fas fa-sign-out-alt',
      descripcion: 'Liberar puestos ocupados'
    },
    {
      id: 'card3',
      titulo: 'Ocupar Puesto',
      ruta: '/puestos/ocupar',
      color: 'blue',
      icono: 'fas fa-parking',
      descripcion: 'Asignar puesto a cliente'
    },
    {
      id: 'card4',
      titulo: 'Generar Reporte de Ocupación',
      ruta: '/reportes',
      color: 'orange',
      icono: 'fas fa-chart-bar',
      descripcion: 'Ver reportes y estadísticas de ocupación'
    },
    {
      id: 'card5',
      titulo: 'Consultar Puestos',
      ruta: '/puestos',
      color: 'purple',
      icono: 'fas fa-list',
      descripcion: 'Ver lista de todos los puestos'
    },
    {
      id: 'card6',
      titulo: 'Estadísticas',
      ruta: '/puestos/estadisticas',
      color: 'teal',
      icono: 'fas fa-chart-pie',
      descripcion: 'Ver estadísticas generales del sistema'
    }
  ];

  constructor(private router: Router) {}

  seleccionarOpcion(opcion: OpcionMenu) {
    console.log('Navegando a:', opcion.ruta);
    this.router.navigate([opcion.ruta]);
  }

  volverAInicio() {
    this.router.navigate(['/']);
  }
}
