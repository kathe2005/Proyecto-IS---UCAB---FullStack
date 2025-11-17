import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  imports: [CommonModule],
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
      ruta: '/puestos',
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
      ruta: '/puestos/estadisticas',
      color: 'orange',
      icono: 'fas fa-chart-bar',
      descripcion: 'Ver reportes y estadísticas'
    },
    {
      id: 'card5',
      titulo: 'Modificar Puesto',
      ruta: '/puestos',
      color: 'magenta',
      icono: 'fas fa-edit',
      descripcion: 'Editar información de puestos'
    },
    {
      id: 'card6',
      titulo: 'Eliminar Puesto',
      ruta: '/puestos',
      color: 'red',
      icono: 'fas fa-trash',
      descripcion: 'Eliminar puestos del sistema'
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
