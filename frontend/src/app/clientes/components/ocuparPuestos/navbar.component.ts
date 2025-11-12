import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar bg-white rounded mx-4 my-3 shadow-sm">
      <div class="d-flex gap-3">
        <a routerLink="/"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark"
           [class.active]="false">
          Inicio
        </a>
        <a routerLink="/puestos"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark">
          Gestión de Puestos
        </a>
        <a routerLink="/ocupar"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark">
          Ocupar Puesto
        </a>
        <a routerLink="/estadisticas"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark">
          Estadísticas
        </a>
        <a routerLink="/buscar"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark">
          Buscar Puestos
        </a>
        <a routerLink="/crear"
           routerLinkActive="active"
           class="nav-link px-4 py-2 rounded text-decoration-none text-dark">
          Crear Puestos
        </a>
      </div>
    </nav>
  `,
  styles: [`
    .nav-link {
      transition: all 0.3s ease;
    }
    .nav-link:hover, .nav-link.active {
      background-color: rgb(28, 85, 255) !important;
      color: white !important;
      transform: scale(1.05);
      box-shadow: 0 0 10px rgba(55, 55, 55, 0.737);
    }
  `]
})
export class NavbarComponent {}
