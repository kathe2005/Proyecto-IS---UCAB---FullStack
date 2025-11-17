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
  selector: 'app-gestion-perfiles',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-perfiles.component.html',
  styleUrls: ['./gestion-perfiles.component.css']
})
export class GestionPerfilesComponent {

  opciones: OpcionMenu[] = [
    {
      id: 'card1',
      titulo: 'Registrar Cliente',
      ruta: '/perfiles/registrar',
      color: 'green',
      icono: 'fas fa-user-plus',
      descripcion: 'Agregar nuevo cliente al sistema'
    },
    {
      id: 'card2',
      titulo: 'Consultar Perfiles',
      ruta: '/perfiles/consultar',
      color: 'blue',
      icono: 'fas fa-search',
      descripcion: 'Buscar y ver información de clientes'
    },
    {
      id: 'card3',
      titulo: 'Modificar Perfil del Cliente',
      ruta: '/perfiles/modificar',
      color: 'orange',
      icono: 'fas fa-user-edit',
      descripcion: 'Editar información del cliente'
    },
    {
      id: 'card4',
      titulo: 'Ver Historial de Reserva de Clientes',
      ruta: '/perfiles/historial',
      color: 'purple',
      icono: 'fas fa-history',
      descripcion: 'Consultar historial de reservas del cliente'
    },
    {
      id: 'card5',
      titulo: 'Eliminar Perfil',
      ruta: '/perfiles/eliminar',
      color: 'red',
      icono: 'fas fa-user-times',
      descripcion: 'Eliminar cliente del sistema'
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
