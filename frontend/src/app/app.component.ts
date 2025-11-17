import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './clientes/components/ocuparPuesto/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `
    <app-header></app-header>
    <div class="main-content">
      <router-outlet></router-outlet>
    </div>
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
