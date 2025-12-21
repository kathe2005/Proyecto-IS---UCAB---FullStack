import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReservaService } from '../../service/reserva.service';
import { ClienteService, Cliente } from '../../service/cliente.service';
import { PuestoService } from '../../service/puesto.service';
import { Puesto } from '../../models/puestos.model';

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

    // Cargar puestos disponibles automáticamente con fecha actual
    setTimeout(() => {
      this.cargarPuestosDisponibles();
    }, 500);
  }

  getFechaActual(): string {
    return new Date().toISOString().split('T')[0];
  }

  cargarClientes(): void {
    this.clienteService.consultarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        console.log('✅ Clientes cargados:', clientes.length);
      },
      error: (error) => {
        console.error('❌ Error al cargar clientes:', error);
        this.error = 'Error al cargar la lista de clientes';
      }
    });
  }

  onFechaOTurnoCambio(): void {
    console.log('📅 Fecha/Turno cambiado:', this.fecha, this.turno);
    this.cargarPuestosDisponibles();
  }

  cargarPuestosDisponibles(): void {
    if (!this.fecha || !this.turno) {
      this.error = 'Por favor, seleccione fecha y turno.';
      return;
    }

    console.log('🔍 Cargando puestos disponibles para:', this.fecha, this.turno);

    this.puestosCargando = true;
    this.error = '';
    this.puestosDisponibles = [];

    this.reservaService.consultarPuestosDisponibles(this.fecha, this.turno)
      .subscribe({
        next: (result: any) => {
          console.log('✅ Puestos disponibles recibidos:', result);
          this.puestosDisponibles = result.puestos || [];
          this.puestosCargando = false;

          console.log(`📊 ${this.puestosDisponibles.length} puestos disponibles`);

          if (this.puestosDisponibles.length === 0) {
            this.error = 'No hay puestos disponibles para la fecha y turno seleccionados.';
          } else {
            // Auto-seleccionar el primer puesto si solo hay uno
            if (this.puestosDisponibles.length === 1 && !this.puestoId) {
              this.puestoId = this.puestosDisponibles[0].id;
              console.log('🎯 Puesto auto-seleccionado:', this.puestoId);
            }
          }
        },
        error: (error) => {
          console.error('❌ Error al cargar puestos disponibles:', error);
          this.error = 'Error al cargar los puestos disponibles: ' +
                      (error.error?.error || error.message || 'Error desconocido');
          this.puestosCargando = false;
        }
      });
  }

  crearReserva(): void {
    console.log('📅 Creando reserva...');

    // Validaciones
    if (!this.clienteId || !this.puestoId || !this.fecha || !this.turno) {
      this.error = 'Por favor, complete todos los campos obligatorios.';
      return;
    }

    const clienteSeleccionado = this.clientes.find(c => c.id === this.clienteId);
    if (!clienteSeleccionado) {
      this.error = 'Cliente no válido. Por favor, seleccione un cliente de la lista.';
      return;
    }

    this.reservando = true;
    this.error = '';
    this.exito = '';

    const reservaRequest = {
      puestoId: this.puestoId,
      clienteId: this.clienteId,
      usuario: clienteSeleccionado.usuario,
      fecha: this.fecha,
      turno: this.turno
    };

    console.log('📤 Enviando reserva al backend:', reservaRequest);

    this.reservaService.crearReserva(reservaRequest).subscribe({
      next: (reserva: any) => {
        console.log('✅ Reserva creada exitosamente:', reserva);

        const numeroPuesto = this.getNumeroPuesto();

        this.exito = `✅ RESERVA CREADA EXITOSAMENTE\n\n` +
                    `ID de Reserva: ${reserva.id}\n` +
                    `Cliente: ${clienteSeleccionado.nombre} ${clienteSeleccionado.apellido}\n` +
                    `Puesto: ${numeroPuesto}\n` +
                    `Fecha: ${this.fecha}\n` +
                    `Turno: ${this.turno}\n` +
                    `Estado: ${reserva.estado || 'PENDIENTE'}\n\n` +
                    `⚠️ IMPORTANTE: Ahora puede registrar el pago de esta reserva.`;

        this.reservando = false;

        // Limpiar formulario después de 3 segundos
        setTimeout(() => {
          this.limpiarFormulario();
        }, 3000);
      },
      error: (error) => {
        console.error('❌ Error al crear reserva:', error);

        let mensajeError = 'Error al crear la reserva: ';
        if (error.error?.error) {
          mensajeError += error.error.error;
        } else if (error.message) {
          mensajeError += error.message;
        } else {
          mensajeError += 'Error desconocido';
        }

        this.error = mensajeError;
        this.reservando = false;
      }
    });
  }

  getNumeroPuesto(): string {
    const puesto = this.puestosDisponibles.find(p => p.id === this.puestoId);
    return puesto ? puesto.numero : 'Desconocido';
  }

  getNombreCliente(clienteId: string): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : 'Desconocido';
  }

  limpiarFormulario(): void {
    console.log('🧹 Limpiando formulario...');
    this.clienteId = '';
    this.puestoId = '';
    this.fecha = this.getFechaActual();
    this.turno = 'MAÑANA';
    this.puestosDisponibles = [];
    this.error = '';
    this.exito = '';

    // Recargar puestos disponibles
    this.cargarPuestosDisponibles();
  }

  volverAGestion(): void {
    this.router.navigate(['/gestion-reservas']);
  }

  irARegistrarPago(): void {
    this.router.navigate(['/registrar-pago']);
  }

  irAConsultarDisponibilidad(): void {
    this.router.navigate(['/reservas/consultar-disponibilidad']);
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'DISPONIBLE': return 'estado-disponible';
      case 'OCUPADO': return 'estado-ocupado';
      case 'BLOQUEADO': return 'estado-bloqueado';
      case 'MANTENIMIENTO': return 'estado-mantenimiento';
      case 'RESERVADO': return 'estado-reservado';
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

  getEstadoLabel(estado: string): string {
    switch (estado) {
      case 'DISPONIBLE': return 'Disponible';
      case 'OCUPADO': return 'Ocupado';
      case 'BLOQUEADO': return 'Bloqueado';
      case 'MANTENIMIENTO': return 'Mantenimiento';
      case 'RESERVADO': return 'Reservado';
      default: return estado;
    }
  }

  getTipoLabel(tipo: string): string {
    switch (tipo) {
      case 'REGULAR': return 'Regular';
      case 'DISCAPACITADO': return 'Discapacitado';
      case 'DOCENTE': return 'Docente';
      case 'VISITANTE': return 'Visitante';
      case 'MOTOCICLETA': return 'Motocicleta';
      default: return tipo;
    }
  }
}
