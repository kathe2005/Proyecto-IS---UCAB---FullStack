import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; 

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
      RouterOutlet             // Para que reconozca <router-outlet>
  ],
  templateUrl: './app.component.html',
  //...
})
export class AppComponent {
  title = 'Mi Proyecto UCAB';
}