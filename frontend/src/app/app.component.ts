import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './clientes/components/ocuparPuesto/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="main-content">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .main-content {
      min-height: 100vh;
    }
  `]
})
export class AppComponent {
  title = 'SIGE - Sistema de Gestión de Estacionamiento';
}
