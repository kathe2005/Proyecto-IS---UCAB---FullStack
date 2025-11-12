import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/ocuparPuestos/header/header.component';
import { NavbarComponent } from './components/ocuparPuestos/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent,
    NavbarComponent
  ],
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
      padding: 20px;
    }

    footer {
      background: linear-gradient(135deg, #1c55ff 0%, #0d3fd4 100%);
    }
  `]
})
export class AppComponent {
  title = 'SIGE - Sistema de Gestión de Estacionamiento';
}
