import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { PagoService } from '../../service/pago.service';
import { ClienteService, Cliente } from '../../service/cliente.service';
import { PagoRequest, ReservaConCliente, MetodoPago, EstadoPago } from '../../models/pago.model';

@Component({
  selector: 'app-registrar-pago',
  templateUrl: './registrar-pago.component.html',
  styleUrls: ['./registrar-pago.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class RegistrarPagoComponent implements OnInit {

  // Datos del formulario
  reservaId: string = '';
  clienteId: string = '';
  monto: number = 0;
  metodoPago: MetodoPago = MetodoPago.EFECTIVO;
  referencia: string = '';
  descripcion: string = '';

  // Listas de datos
  reservasPendientes: ReservaConCliente[] = [];
  clientes: Cliente[] = [];
  reservasFiltradas: ReservaConCliente[] = [];

  // Estados
  procesando: boolean = false;
  cargando: boolean = false;
  error: string = '';
  exito: string = '';

  // Opciones
  metodosPago: { value: MetodoPago; label: string }[] = [];

  constructor(
    private pagoService: PagoService,
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatosIniciales();
    this.metodosPago = this.pagoService.obtenerMetodosPago();
  }

  cargarDatosIniciales(): void {
    this.cargando = true;

    // Cargar reservas pendientes de pago
    this.pagoService.obtenerReservasPendientesPago().subscribe({
      next: (reservas) => {
        this.reservasPendientes = reservas;
        this.reservasFiltradas = reservas;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar reservas pendientes:', error);
        this.error = 'Error al cargar las reservas pendientes de pago.';
        this.cargando = false;

        // Datos de ejemplo para desarrollo
        this.cargarDatosEjemplo();
      }
    });

    // Cargar clientes
    this.clienteService.consultarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
      }
    });
  }

  // Datos de ejemplo para desarrollo
  private cargarDatosEjemplo(): void {
    this.reservasPendientes = [
      {
        id: 'R1',
        puestoId: 'P1',
        clienteId: '1',
        usuario: 'juan.perez',
        fecha: new Date().toISOString().split('T')[0],
        turno: 'MAÑANA',
        estado: 'CONFIRMADA',
        cliente: {
          nombre: 'Juan',
          apellido: 'Pérez',
          cedula: 'V-12345678'
        },
        puesto: {
          numero: 'A-01',
          ubicacion: 'Zona A',
          tipoPuesto: 'REGULAR'
        }
      },
      {
        id: 'R2',
        puestoId: 'P2',
        clienteId: '2',
        usuario: 'maria.gonzalez',
        fecha: new Date().toISOString().split('T')[0],
        turno: 'TARDE',
        estado: 'CONFIRMADA',
        cliente: {
          nombre: 'María',
          apellido: 'González',
          cedula: 'V-87654321'
        },
        puesto: {
          numero: 'B-01',
          ubicacion: 'Zona B',
          tipoPuesto: 'DOCENTE'
        }
      }
    ];
    this.reservasFiltradas = [...this.reservasPendientes];
    this.cargando = false;
  }

  // Cuando se selecciona una reserva
  onReservaSeleccionada(): void {
    const reservaSeleccionada = this.reservasPendientes.find(r => r.id === this.reservaId);
    if (reservaSeleccionada) {
      this.clienteId = reservaSeleccionada.clienteId;
      this.monto = this.pagoService.calcularMonto(
        reservaSeleccionada.puesto.tipoPuesto,
        reservaSeleccionada.turno
      );
      this.descripcion = `Pago reserva puesto ${reservaSeleccionada.puesto.numero} - ${reservaSeleccionada.fecha} ${reservaSeleccionada.turno}`;
    }
  }

  // Filtrar reservas por cliente
  filtrarReservasPorCliente(): void {
    if (this.clienteId) {
      this.reservasFiltradas = this.reservasPendientes.filter(
        r => r.clienteId === this.clienteId
      );
    } else {
      this.reservasFiltradas = [...this.reservasPendientes];
    }

    // Resetear reserva seleccionada si no está en la lista filtrada
    if (this.reservaId && !this.reservasFiltradas.find(r => r.id === this.reservaId)) {
      this.reservaId = '';
      this.monto = 0;
      this.descripcion = '';
    }
  }

  // Registrar el pago
  registrarPago(): void {
    if (!this.reservaId || !this.clienteId || !this.monto || !this.metodoPago || !this.referencia) {
      this.error = 'Por favor, complete todos los campos obligatorios.';
      return;
    }

    if (this.monto <= 0) {
      this.error = 'El monto debe ser mayor a cero.';
      return;
    }

    this.procesando = true;
    this.error = '';
    this.exito = '';

    const pagoRequest: PagoRequest = {
      reservaId: this.reservaId,
      clienteId: this.clienteId,
      monto: this.monto,
      metodoPago: this.metodoPago,
      referencia: this.referencia,
      descripcion: this.descripcion
    };

    this.pagoService.registrarPago(pagoRequest).subscribe({
      next: (pago) => {
        this.procesando = false;
        this.exito = `✅ Pago registrado exitosamente!\nID: ${pago.id}\nMonto: $${pago.monto}\nMétodo: ${this.getMetodoPagoLabel(pago.metodoPago)}\nReferencia: ${pago.referencia}`;

        // Limpiar formulario después de éxito
        this.limpiarFormulario();

        // Recargar lista de reservas pendientes
        setTimeout(() => {
          this.cargarDatosIniciales();
        }, 2000);
      },
      error: (error) => {
        console.error('Error al registrar pago:', error);
        this.error = error.error?.error || 'Error al registrar el pago. Por favor, intente nuevamente.';
        this.procesando = false;
      }
    });
  }

  // Obtener label del método de pago
  getMetodoPagoLabel(metodo: MetodoPago): string {
    const metodoObj = this.metodosPago.find(m => m.value === metodo);
    return metodoObj ? metodoObj.label : metodo;
  }

  // Obtener nombre completo del cliente
  getNombreCliente(clienteId: string): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : '';
  }

  // Limpiar formulario
  limpiarFormulario(): void {
    this.reservaId = '';
    this.clienteId = '';
    this.monto = 0;
    this.metodoPago = MetodoPago.EFECTIVO;
    this.referencia = '';
    this.descripcion = '';
    this.reservasFiltradas = [...this.reservasPendientes];
  }

  // Volver a gestión de reservas
  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }

  // Obtener información de la reserva seleccionada
  getReservaSeleccionada(): ReservaConCliente | null {
    return this.reservasPendientes.find(r => r.id === this.reservaId) || null;
  }

  // Formatear monto como moneda
  formatearMonto(monto: number): string {
    return `$${monto.toFixed(2)}`;
  }
}
