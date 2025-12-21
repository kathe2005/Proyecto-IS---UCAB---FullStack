import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { PagoService } from '../../service/pago.service';
import { ReservaService } from '../../service/reserva.service';
import { ClienteService, Cliente } from '../../service/cliente.service';

interface ReservaParaPago {
  id: string;
  puestoId: string;
  clienteId: string;
  usuario: string;
  fecha: string;
  turno: string;
  estado: string;
  tarifaCalculada: number;
  cliente: {  // ← CAMBIA: quita el opcional (?)
    nombre: string;
    apellido: string;
    cedula: string;
    email: string;
    tipoPersona: string;
    id: string;
  };
  puesto: {  // ← CAMBIA: quita el opcional (?)
    numero: string;
    ubicacion: string;
    tipoPuesto: string;
    estadoPuesto: string;
  };
}

@Component({
  selector: 'app-registrar-pago',
  templateUrl: './registrar-pago.component.html',
  styleUrls: ['./registrar-pago.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class RegistrarPagoComponent implements OnInit {

  // Datos del formulario
  clienteId: string = '';
  reservaId: string = '';
  monto: number = 0;
  metodoPago: string = 'EFECTIVO';
  referencia: string = '';
  descripcion: string = '';

  // Listas de datos
  clientes: Cliente[] = [];
  reservasPendientes: ReservaParaPago[] = [];
  reservasFiltradas: ReservaParaPago[] = [];

  // Estados
  cargando: boolean = false;
  procesando: boolean = false;
  error: string = '';
  exito: string = '';

  // Opciones
  metodosPago = [
    { value: 'EFECTIVO', label: 'Efectivo' },
    { value: 'TARJETA_CREDITO', label: 'Tarjeta de Crédito' },
    { value: 'TARJETA_DEBITO', label: 'Tarjeta de Débito' },
    { value: 'TRANSFERENCIA', label: 'Transferencia Bancaria' },
    { value: 'PAGO_MOVIL', label: 'Pago Móvil' }
  ];

  constructor(
    private pagoService: PagoService,
    private clienteService: ClienteService,
    private reservaService: ReservaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  cargarDatosIniciales(): void {
    this.cargando = true;
    this.error = '';

    // Cargar clientes
    this.clienteService.consultarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        console.log('✅ Clientes cargados:', clientes.length);

        // Cargar reservas pendientes
        this.cargarReservasPendientes();
      },
      error: (error) => {
        console.error('❌ Error al cargar clientes:', error);
        this.error = 'Error al cargar la lista de clientes';
        this.cargando = false;
      }
    });
  }

  cargarReservasPendientes(): void {
    console.log('📥 Cargando reservas pendientes de pago...');

    this.pagoService.obtenerReservasPendientesPago().subscribe({
      next: (reservas: any[]) => {
        console.log('✅ Reservas recibidas del backend:', reservas);
        this.reservasPendientes = reservas;
        this.reservasFiltradas = [...reservas]; // Copia inicial

        console.log(`📊 ${reservas.length} reservas pendientes cargadas`);

        // Mostrar detalles en consola
        reservas.forEach(reserva => {
          console.log(`   • ${reserva.id} - ${reserva.cliente?.nombre} ${reserva.cliente?.apellido} - $${reserva.tarifaCalculada}`);
        });

        this.cargando = false;

        if (reservas.length === 0) {
          this.error = 'No hay reservas pendientes de pago en el sistema.';
        }
      },
      error: (error) => {
        console.error('❌ Error al cargar reservas pendientes:', error);
        this.error = 'Error al cargar las reservas pendientes de pago: ' +
                    (error.error?.error || error.message || 'Error desconocido');
        this.cargando = false;
      }
    });
  }

  filtrarReservasPorCliente(): void {
    console.log('🔍 Filtrando reservas por cliente:', this.clienteId);

    if (!this.clienteId) {
      // Mostrar todas las reservas
      this.reservasFiltradas = [...this.reservasPendientes];
    } else {
      // Filtrar por cliente
      this.reservasFiltradas = this.reservasPendientes.filter(reserva =>
        reserva.clienteId === this.clienteId ||
        reserva.cliente?.id === this.clienteId
      );
    }

    console.log(`📋 ${this.reservasFiltradas.length} reservas después del filtro`);

    // Si se cambió el filtro y había una reserva seleccionada, limpiarla
    if (this.reservaId) {
      const reservaActual = this.reservasFiltradas.find(r => r.id === this.reservaId);
      if (!reservaActual) {
        this.reservaId = '';
        this.monto = 0;
      }
    }
  }

  onReservaSeleccionada(): void {
    console.log('🎯 Reserva seleccionada:', this.reservaId);

    if (this.reservaId) {
      const reserva = this.getReservaSeleccionada();
      if (reserva) {
        // Establecer el monto automáticamente
        this.monto = reserva.tarifaCalculada;
        console.log(`💰 Monto establecido automáticamente: $${this.monto}`);

        // Generar referencia automática
        this.referencia = `PAGO-${this.reservaId}-${Date.now().toString().slice(-6)}`;
        console.log(`🔢 Referencia generada: ${this.referencia}`);

        // Auto-seleccionar cliente si no está seleccionado
        if (!this.clienteId && reserva.cliente?.id) {
          this.clienteId = reserva.cliente.id;
          console.log(`👤 Cliente auto-seleccionado: ${reserva.cliente.nombre}`);
        }
      }
    } else {
      this.monto = 0;
      this.referencia = '';
    }
  }

  getReservaSeleccionada(): ReservaParaPago | null {
    if (!this.reservaId) return null;

    const reserva = this.reservasFiltradas.find(r => r.id === this.reservaId);
    if (!reserva) {
      console.warn('⚠️ Reserva seleccionada no encontrada en lista filtrada');
      return null;
    }

    return reserva;
  }

  registrarPago(): void {
    console.log('💰 Iniciando registro de pago...');

    // Validaciones
    if (!this.reservaId) {
      this.error = 'Por favor, seleccione una reserva.';
      return;
    }

    if (!this.monto || this.monto <= 0) {
      this.error = 'Por favor, ingrese un monto válido mayor a cero.';
      return;
    }

    if (!this.metodoPago) {
      this.error = 'Por favor, seleccione un método de pago.';
      return;
    }

    if (!this.referencia || this.referencia.trim() === '') {
      this.error = 'Por favor, ingrese una referencia de pago.';
      return;
    }

    const reserva = this.getReservaSeleccionada();
    if (!reserva) {
      this.error = 'Reserva no válida. Por favor, seleccione otra reserva.';
      return;
    }

    // Preparar datos del pago
    const pagoRequest = {
      reservaId: this.reservaId,
      clienteId: reserva.clienteId || reserva.cliente?.id,
      monto: this.monto,
      metodoPago: this.metodoPago,
      referencia: this.referencia,
      descripcion: this.descripcion || `Pago de reserva ${this.reservaId}`
    };

    console.log('📤 Enviando pago al backend:', pagoRequest);

    this.procesando = true;
    this.error = '';
    this.exito = '';

    this.pagoService.registrarPago(pagoRequest).subscribe({
      next: (response: any) => {
        console.log('✅ Pago registrado exitosamente:', response);

        this.exito = `✅ PAGO REGISTRADO EXITOSAMENTE\n\n` +
                    `ID del Pago: ${response.id || 'N/A'}\n` +
                    `Reserva: ${this.reservaId}\n` +
                    `Cliente: ${reserva.cliente?.nombre} ${reserva.cliente?.apellido}\n` +
                    `Monto: ${this.formatearMonto(this.monto)}\n` +
                    `Método: ${this.getMetodoPagoLabel(this.metodoPago)}\n` +
                    `Referencia: ${this.referencia}\n` +
                    `Fecha: ${new Date().toLocaleString()}`;

        this.procesando = false;

        // Recargar lista de reservas pendientes después de 2 segundos
        setTimeout(() => {
          this.cargarReservasPendientes();
          this.limpiarFormulario();
        }, 2000);
      },
      error: (error) => {
        console.error('❌ Error al registrar pago:', error);

        let mensajeError = 'Error al registrar el pago: ';
        if (error.error?.error) {
          mensajeError += error.error.error;
        } else if (error.message) {
          mensajeError += error.message;
        } else {
          mensajeError += 'Error desconocido';
        }

        this.error = mensajeError;
        this.procesando = false;
      }
    });
  }

  getMetodoPagoLabel(value: string): string {
    const metodo = this.metodosPago.find(m => m.value === value);
    return metodo ? metodo.label : value;
  }

  formatearMonto(monto: number): string {
    return `$${monto.toFixed(2)}`;
  }

  limpiarFormulario(): void {
    console.log('🧹 Limpiando formulario...');
    this.reservaId = '';
    this.monto = 0;
    this.metodoPago = 'EFECTIVO';
    this.referencia = '';
    this.descripcion = '';
    this.clienteId = '';
    this.reservasFiltradas = [...this.reservasPendientes];
    this.error = '';
    this.exito = '';
  }

  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }

  recargarReservas(): void {
    console.log('🔄 Recargando reservas...');
    this.cargando = true;
    this.cargarReservasPendientes();
  }
}
