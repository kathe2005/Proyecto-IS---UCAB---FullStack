import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ReservaService } from '../../service/reserva.service';
import { Reserva } from '../../models/reserva.model';
import { ClienteService, Cliente } from '../../service/cliente.service';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './historial.component.html',
  styleUrls: ['./historial.component.css']
})
export class HistorialComponent implements OnInit {
  cliente: Cliente | null = null;
  reservas: Reserva[] = [];
  clienteId: string = '';
  cargando: boolean = false;
  error: string = '';

  constructor(
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.clienteId = params['clienteId'];
      if (this.clienteId) {
        this.cargarCliente();
        this.cargarReservas();
      }
    });
  }

  cargarCliente() {
    this.cargando = true;
    this.clienteService.obtenerClientePorId(this.clienteId).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error cargando cliente:', error);
        this.error = 'Error al cargar la información del cliente';
        this.cargando = false;
      }
    });
  }

  cargarReservas() {
    this.cargando = true;
    this.reservaService.obtenerReservasPorCliente(this.clienteId).subscribe({
      next: (reservas) => {
        this.reservas = reservas;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error cargando reservas:', error);
        this.error = 'Error al cargar el historial de reservas';
        this.cargando = false;
      }
    });
  }

  getEstadoColor(estado: string): string {
    const colores: {[key: string]: string} = {
      'PENDIENTE': '#ffc107',
      'CONFIRMADA': '#007bff',
      'CANCELADA': '#6c757d',
      'COMPLETADA': '#28a745'
    };
    return colores[estado] || '#6c757d';
  }

  getEstadoDescripcion(estado: string): string {
    const estados: {[key: string]: string} = {
      'PENDIENTE': 'Pendiente',
      'CONFIRMADA': 'Confirmada',
      'CANCELADA': 'Cancelada',
      'COMPLETADA': 'Completada'
    };
    return estados[estado] || estado;
  }

  volver() {
    window.history.back();
  }
}
