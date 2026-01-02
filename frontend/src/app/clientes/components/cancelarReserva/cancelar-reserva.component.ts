import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReservaService } from '../../service/reserva.service';

@Component({
  selector: 'app-cancelar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cancelar-reserva.component.html',
  styleUrls: ['./cancelar-reserva.component.css']
})
export class CancelarReservaComponent {
  reservaId: string = '';
  mensaje: string = '';

  constructor(private reservaService: ReservaService, private router: Router) {}

  cancelarReserva() {
    if (this.reservaId) {
      // Llamar al servicio para cancelar la reserva
      this.reservaService.cancelarReserva(this.reservaId).subscribe({
        next: (response) => {
          this.mensaje = 'Reserva cancelada exitosamente.';
        },
        error: (error) => {
          this.mensaje = 'Error al cancelar la reserva: ' + error.message;
        }
      });
    } else {
      this.mensaje = 'Por favor, ingrese un ID de reserva válido.';
    }
  }

  volver() {
    this.router.navigate(['/gestion-reservas']);
  }
}
