import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-gestion-estacionamiento',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-estacionamiento.component.html',
  styleUrls: ['./gestion-estacionamiento.component.css']
})
export class GestionEstacionamientoComponent {

  opciones = [
    {
      id: 'card1',
      titulo: 'Gestión de Puestos',
      ruta: '/gestion-puestos',
      color: 'primary'
    },
    {
      id: 'card2',
      titulo: 'Gestión de Perfiles',
      ruta: '/gestion-perfiles',
      color: 'success'
    },
    {
      id: 'card3',
      titulo: 'Gestión de Reservas', // CAMBIADO DE "Gestión de reserva"
      ruta: '/gestion-reservas', // RUTA CORREGIDA
      color: 'warning'
    },
    {
      id: 'card4',
      titulo: 'Gestión de Incidencias',
      ruta: '/puestos/estadisticas',
      color: 'info'
    },
    {
      id: 'card5',
      titulo: 'Gestión de Vehiculos',
      ruta: '/puestos/buscar',
      color: 'secondary'
    },
    {
      id: 'card6',
      titulo: 'Lista de Puestos',
      ruta: '/puestos',
      color: 'dark'
    }
  ];

  constructor(private router: Router) {}

  seleccionarOpcion(opcion: any) {
    console.log('Navegando a:', opcion.ruta);
    this.router.navigate([opcion.ruta]);
  }
}
