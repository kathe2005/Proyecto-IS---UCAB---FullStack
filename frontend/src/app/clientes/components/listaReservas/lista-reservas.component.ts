import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReservaService } from '../../service/reserva.service';
import { Reserva } from '../../models/reserva.model';

@Component({
  selector: 'app-lista-reservas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista-reservas.component.html',
  styleUrls: ['./lista-reservas.component.css']
})
export class ListaReservasComponent implements OnInit {
  reservas: Reserva[] = [];
  mensaje: string = '';

  constructor(private reservaService: ReservaService, private router: Router) {}

  ngOnInit() {
    this.cargarReservasActivas();
  }

  cargarReservasActivas() {
    this.reservaService.obtenerReservasActivas().subscribe({
      next: (data) => {
        this.reservas = data;
      },
      error: (error) => {
        this.mensaje = 'Error al cargar las reservas: ' + error.message;
      }
    });
  }

  volver() {
    this.router.navigate(['/gestion-reservas']);
  }
}
