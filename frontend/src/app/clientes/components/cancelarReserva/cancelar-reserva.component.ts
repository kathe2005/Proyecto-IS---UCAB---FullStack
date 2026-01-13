import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ReservaService } from '../../service/reserva.service';
import { ClienteService, Cliente } from '../../service/cliente.service';
import { PuestoService, Puesto } from '../../service/puesto.service';
import { Reserva } from '../../models/reserva.model';

@Component({
  selector: 'app-cancelar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './cancelar-reserva.component.html',
  styleUrls: ['./cancelar-reserva.component.css']
})
export class CancelarReservaComponent implements OnInit {
  reservas: Reserva[] = [];
  clientes: Cliente[] = [];
  puestos: Puesto[] = [];
  seleccionada: Reserva | null = null;

  totalReservas = 0;
  cargando = false;
  cancelando = false;
  error = '';
  exito = '';

  constructor(
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargando = true;
    this.error = '';
    forkJoin({
      reservas: this.reservaService.obtenerTodasLasReservas(),
      clientes: this.clienteService.consultarClientes(),
      puestos: this.puestoService.obtenerTodosLosPuestos()
    }).subscribe({
      next: ({ reservas, clientes, puestos }) => {
        this.reservas = reservas;
        this.clientes = clientes;
        this.puestos = puestos;
        this.totalReservas = this.reservas.length;
        this.cargando = false;

        if (this.reservas.length === 0) {
          this.error = 'No hay reservas registradas para cancelar.';
        }
      },
      error: (err) => {
        console.error('Error cargando datos para cancelar', err);
        this.error = err.error?.error || 'No se pudieron cargar las reservas.';
        this.cargando = false;
      }
    });
  }

  seleccionarReserva(reserva: Reserva): void {
    this.seleccionada = { ...reserva };
    this.error = '';
    this.exito = '';
  }

  cancelarSeleccionada(): void {
    if (!this.seleccionada) {
      this.error = 'Seleccione una reserva para cancelar.';
      return;
    }

    this.cancelando = true;
    this.error = '';
    this.exito = '';

    this.reservaService.cancelarReserva(this.seleccionada.id).subscribe({
      next: () => {
        this.exito = 'Reserva cancelada y eliminada exitosamente.';
        this.reservas = this.reservas.filter(r => r.id !== this.seleccionada!.id);
        this.totalReservas = this.reservas.length;
        this.seleccionada = null;
        this.cancelando = false;
      },
      error: (err) => {
        console.error('Error al cancelar reserva', err);
        this.error = err.error?.error || 'No se pudo cancelar la reserva.';
        this.cancelando = false;
      }
    });
  }

  getNombreCliente(clienteId: string): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : 'Cliente no registrado';
  }

  getNumeroPuesto(puestoId: string): string {
    const puesto = this.puestos.find(p => p.id === puestoId);
    return puesto ? `${puesto.numero} (${puesto.ubicacion})` : puestoId;
  }

  volver() {
    this.router.navigate(['/gestion-reservas']);
  }
}
