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
  selector: 'app-gestion-reserva',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-reserva.component.html',
  styleUrls: ['./gestion-reserva.component.css']
})
export class GestionReservaComponent {

  opciones: OpcionMenu[] = [
    {
      id: 'card1',
      titulo: 'Consultar Puestos Disponibles',
      ruta: '/reservas/consultar-disponibilidad', // RUTA CORREGIDA
      color: 'green',
      icono: 'fas fa-search',
      descripcion: 'Consultar puestos disponibles por fecha'
    },
    {
      id: 'card2',
      titulo: 'Reservar Puesto',
      ruta: '/reservas/crear', // NUEVA RUTA PARA RESERVAR
      color: 'blue',
      icono: 'fas fa-calendar-plus',
      descripcion: 'Crear nueva reserva de puesto'
    },
    {
      id: 'card3',
      titulo: 'Registrar Pago De Reserva',
      ruta: '/reservas/pagos', // NUEVA RUTA PARA PAGOS
      color: 'yellow',
      icono: 'fas fa-money-bill-wave',
      descripcion: 'Registrar pago de reservas'
    },
    {
      id: 'card4',
      titulo: 'Modificar Reserva',
      ruta: '/reservas/modificar', // NUEVA RUTA PARA MODIFICAR
      color: 'orange',
      icono: 'fas fa-edit',
      descripcion: 'Modificar reservas existentes'
    },
    {
      id: 'card5',
      titulo: 'Cancelar Reserva',
      ruta: '/reservas/cancelar', // NUEVA RUTA PARA CANCELAR
      color: 'red',
      icono: 'fas fa-times-circle',
      descripcion: 'Cancelar reservas activas'
    },
    {
      id: 'card6',
      titulo: 'Lista de Reservas Activas',
      ruta: '/reservas/activas', // NUEVA RUTA PARA LISTA
      color: 'purple',
      icono: 'fas fa-list',
      descripcion: 'Ver todas las reservas activas'
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
