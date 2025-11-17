import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  fechaActual: string = '';

  ngOnInit() {
    this.actualizarFecha();
    // Actualizar la fecha cada minuto (opcional)
    setInterval(() => {
      this.actualizarFecha();
    }, 60000);
  }

  actualizarFecha() {
    const opciones: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };
    this.fechaActual = new Date().toLocaleDateString('es-ES', opciones);
  }
}
