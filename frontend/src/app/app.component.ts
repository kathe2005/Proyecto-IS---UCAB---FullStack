import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './clientes/components/ocuparPuestos/header/header.component';
import { NavbarComponent } from './clientes/components/ocuparPuestos/navbar.component';
import { BuscarPuestosComponent } from './clientes/components/ocuparPuestos/buscar/buscar-puestos.component';
import { CrearPuestosComponent } from './clientes/components/ocuparPuestos/crear/crear-puestos.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, NavbarComponent],
  template: `
    <app-header></app-header>
    <app-navbar></app-navbar>
    <div class="main-content">
      <router-outlet></router-outlet>
    </div>
    <footer class="bg-primary text-white text-center py-3 mt-5">
      © 2025 UCAB Montalbán - Sistema de Estacionamiento
    </footer>
  `,
  styles: [`
    .main-content {
      min-height: calc(100vh - 200px);
    }
  `]
})
export class AppComponent {
  title = 'SIGE - Sistema de Gestión de Estacionamiento';
}
