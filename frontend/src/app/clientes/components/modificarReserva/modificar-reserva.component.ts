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
  selector: 'app-modificar-reserva',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './modificar-reserva.component.html',
  styleUrls: ['./modificar-reserva.component.css']
})
export class ModificarReservaComponent implements OnInit {
  reservas: Reserva[] = [];
  clientes: Cliente[] = [];
  todosPuestos: Puesto[] = [];
  puestosDisponibles: Puesto[] = [];
  totalReservas = 0;

  seleccionada: Reserva | null = null;
  form = {
    fecha: '',
    turno: 'MAÑANA',
    puestoId: ''
  };

  cargando = false;
  cargandoPuestos = false;
  guardando = false;
  error = '';
  exito = '';

  turnos: { value: string; label: string }[] = [];

  constructor(
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.turnos = this.reservaService.obtenerTurnos();
    this.cargarDatosIniciales();
  }

  cargarDatosIniciales(): void {
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
        this.todosPuestos = puestos;
        this.totalReservas = this.reservas.length;
        this.cargando = false;

        if (this.reservas.length === 0) {
          this.error = 'No hay reservas registradas para modificar.';
        }
      },
      error: (err) => {
        console.error('Error cargando datos iniciales', err);
        this.error = err.error?.error || 'No se pudieron cargar las reservas.';
        this.cargando = false;
      }
    });
  }

  seleccionarReserva(reserva: Reserva): void {
    this.seleccionada = { ...reserva };
    this.form.fecha = reserva.fecha;
    this.form.turno = reserva.turno;
    this.form.puestoId = reserva.puestoId;
    this.error = '';
    this.exito = '';
    this.cargarPuestosDisponibles();
  }

  cargarPuestosDisponibles(): void {
    if (!this.form.fecha || !this.form.turno) {
      this.error = 'Seleccione fecha y turno para consultar disponibilidad.';
      return;
    }

    this.cargandoPuestos = true;
    this.error = '';
    this.puestosDisponibles = [];

    this.reservaService.consultarPuestosDisponibles(this.form.fecha, this.form.turno)
      .subscribe({
        next: (resp) => {
          const puestos = resp?.puestos || resp?.puestosDisponibles || [];
          this.puestosDisponibles = [...puestos];

          // Permitir mostrar el puesto actual de la reserva solo si fecha y turno no han cambiado
          if (this.form.fecha === this.seleccionada?.fecha && this.form.turno === this.seleccionada?.turno) {
            const actualId = this.seleccionada?.puestoId;
            if (actualId) {
              const actual = this.buscarPuestoPorId(actualId);
              if (actual && !this.puestosDisponibles.find(p => p.id === actual.id)) {
                this.puestosDisponibles.unshift(actual);
              }
            }
          }

          // Si el puesto seleccionado no está en la lista disponible, resetearlo
          if (this.form.puestoId && !this.puestosDisponibles.find(p => p.id === this.form.puestoId)) {
            this.form.puestoId = '';
          }

          if (this.puestosDisponibles.length === 0) {
            this.error = 'No hay puestos disponibles para la fecha y turno seleccionados.';
          }

          this.cargandoPuestos = false;
        },
        error: (err) => {
          console.error('Error cargando puestos disponibles', err);
          this.error = err.error?.error || 'No se pudieron cargar los puestos disponibles.';
          this.cargandoPuestos = false;
        }
      });
  }

  onFechaOTurnoCambio(): void {
    if (this.seleccionada) {
      this.cargarPuestosDisponibles();
    }
  }

  guardarCambios(): void {
    if (!this.seleccionada) {
      this.error = 'Seleccione una reserva para modificar.';
      return;
    }

    if (!this.form.fecha || !this.form.turno || !this.form.puestoId) {
      this.error = 'Complete fecha, turno y puesto.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.exito = '';

    const payload = {
      puestoId: this.form.puestoId,
      fecha: this.form.fecha,
      turno: this.form.turno
    };

    this.reservaService.actualizarReserva(this.seleccionada.id, payload).subscribe({
      next: (reservaActualizada) => {
        this.exito = 'Reserva actualizada exitosamente.';
        this.guardando = false;
        this.actualizarEnLista(reservaActualizada);
        this.seleccionarReserva(reservaActualizada);
      },
      error: (err) => {
        console.error('Error al actualizar reserva', err);
        this.error = err.error?.error || 'No se pudo actualizar la reserva.';
        window.alert(this.error);
        this.guardando = false;

        // Revertir datos locales recargando desde backend para asegurar que no quede el cambio en la vista
        const idFallido = this.seleccionada ? this.seleccionada.id : null;
        this.cargarDatosIniciales();
        setTimeout(() => {
          if (!idFallido) {
            this.seleccionada = null;
            return;
          }

          const encontrada = this.reservas.find(r => r.id === idFallido);
          if (encontrada) {
            this.seleccionarReserva(encontrada);
          } else {
            this.seleccionada = null;
          }
        }, 200);
      }
    });
  }

  actualizarEnLista(reservaActualizada: Reserva): void {
    const idx = this.reservas.findIndex(r => r.id === reservaActualizada.id);
    if (idx >= 0) {
      this.reservas[idx] = { ...reservaActualizada };
    }
    this.totalReservas = this.reservas.length;
  }

  getNombreCliente(clienteId: string): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : 'Cliente no registrado';
  }

  getNumeroPuesto(puestoId: string): string {
    const puesto = this.todosPuestos.find(p => p.id === puestoId) || this.puestosDisponibles.find(p => p.id === puestoId);
    return puesto ? `${puesto.numero} (${puesto.ubicacion})` : puestoId;
  }

  buscarPuestoPorId(puestoId: string): Puesto | undefined {
    return this.todosPuestos.find(p => p.id === puestoId) || this.puestosDisponibles.find(p => p.id === puestoId);
  }

  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }
}
