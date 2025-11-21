import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReservaService } from '../../service/reserva.service';
import { ClienteService, Cliente } from '../../service/cliente.service';
import { PuestoService } from '../../service/puesto.service';
import { Puesto } from '../../models/puestos.model'; // Importar desde models

@Component({
  selector: 'app-reservar-puesto',
  templateUrl: './reservar-puesto.component.html',
  styleUrls: ['./reservar-puesto.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class ReservarPuestoComponent implements OnInit {

  // Datos del formulario
  clienteId: string = '';
  puestoId: string = '';
  fecha: string = '';
  turno: string = 'MAÑANA';

  // Listas de datos
  clientes: Cliente[] = [];
  puestosDisponibles: Puesto[] = [];
  puestosCargando: boolean = false;

  // Estados
  reservando: boolean = false;
  error: string = '';
  exito: string = '';

  // Opciones
  turnos = [
    { value: 'MAÑANA', label: 'Mañana (6:00 - 14:00)' },
    { value: 'TARDE', label: 'Tarde (14:00 - 22:00)' },
    { value: 'NOCHE', label: 'Noche (22:00 - 6:00)' }
  ];

  constructor(
    private reservaService: ReservaService,
    private clienteService: ClienteService,
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
    this.fecha = this.getFechaActual();
  }

  // Método para obtener fecha actual
  getFechaActual(): string {
    return new Date().toISOString().split('T')[0];
  }

  cargarClientes(): void {
    this.clienteService.consultarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
        this.error = 'Error al cargar la lista de clientes';
      }
    });
  }

  // Cargar puestos disponibles cuando cambia la fecha o turno
  cargarPuestosDisponibles(): void {
    if (!this.fecha || !this.turno) {
      return;
    }

    this.puestosCargando = true;
    this.error = '';

    this.reservaService.consultarPuestosDisponibles(this.fecha, this.turno)
      .subscribe({
        next: (result) => {
          this.puestosDisponibles = result.puestos;
          this.puestosCargando = false;

          if (this.puestosDisponibles.length === 0) {
            this.error = 'No hay puestos disponibles para la fecha y turno seleccionados.';
          }
        },
        error: (error) => {
          console.error('Error al cargar puestos disponibles:', error);
          this.error = 'Error al cargar los puestos disponibles.';
          this.puestosCargando = false;
        }
      });
  }

  // Método para crear la reserva
  crearReserva(): void {
    if (!this.clienteId || !this.puestoId || !this.fecha || !this.turno) {
      this.error = 'Por favor, complete todos los campos obligatorios.';
      return;
    }

    this.reservando = true;
    this.error = '';
    this.exito = '';

    const clienteSeleccionado = this.clientes.find(c => c.id === this.clienteId);

    const reservaRequest = {
      puestoId: this.puestoId,
      clienteId: this.clienteId,
      usuario: clienteSeleccionado?.usuario || '',
      fecha: this.fecha,
      turno: this.turno
    };

    this.reservaService.crearReserva(reservaRequest).subscribe({
      next: (reserva) => {
        this.reservando = false;
        this.exito = `✅ Reserva creada exitosamente!\nID: ${reserva.id}\nPuesto: ${this.getNumeroPuesto()}\nFecha: ${this.fecha}\nTurno: ${this.turno}`;

        // Limpiar formulario después de éxito
        this.limpiarFormulario();
      },
      error: (error) => {
        console.error('Error al crear reserva:', error);
        this.error = error.error?.error || 'Error al crear la reserva. Por favor, intente nuevamente.';
        this.reservando = false;
      }
    });
  }

  // Obtener el número del puesto seleccionado
  getNumeroPuesto(): string {
    const puesto = this.puestosDisponibles.find(p => p.id === this.puestoId);
    return puesto ? puesto.numero : '';
  }

  // Obtener nombre completo del cliente
  getNombreCliente(clienteId: string): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : '';
  }

  // Limpiar formulario
  limpiarFormulario(): void {
    this.clienteId = '';
    this.puestoId = '';
    this.fecha = this.getFechaActual();
    this.turno = 'MAÑANA';
    this.puestosDisponibles = [];
  }

  // Volver a gestión de reservas
  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }

  // Ir a consultar disponibilidad
  irAConsultarDisponibilidad(): void {
    this.router.navigate(['/reservas/consultar-disponibilidad']);
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'DISPONIBLE': return 'estado-disponible';
      case 'OCUPADO': return 'estado-ocupado';
      case 'BLOQUEADO': return 'estado-bloqueado';
      case 'MANTENIMIENTO': return 'estado-mantenimiento';
      default: return 'estado-desconocido';
    }
  }

  getTipoClass(tipo: string): string {
    switch (tipo) {
      case 'REGULAR': return 'tipo-regular';
      case 'DISCAPACITADO': return 'tipo-discapacitado';
      case 'DOCENTE': return 'tipo-docente';
      case 'VISITANTE': return 'tipo-visitante';
      case 'MOTOCICLETA': return 'tipo-motocicleta';
      default: return 'tipo-desconocido';
    }
  }
}
