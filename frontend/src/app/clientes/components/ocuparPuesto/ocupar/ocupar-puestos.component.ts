import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { ClienteService, Cliente } from '../../../service/cliente.service';
import { EstadoPuesto } from '../../../models/puestos.model';

interface Puesto {
  id: string;
  numero: string;
  tipoPuesto: string;
  estadoPuesto: string;
  ubicacion: string;
  usuarioOcupante?: string | null;
  fechaOcupacion?: string | null;
}

interface OcuparPuestoRequest {
  puestoId: string;
  cedula: string;
  clienteId?: string;
}

@Component({
  selector: 'app-ocupar-puestos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './ocupar-puestos.component.html',
  styleUrls: ['./ocupar-puestos.component.css']
})
export class OcuparPuestoComponent implements OnInit {
  puestosDisponibles: Puesto[] = [];
  puestoSeleccionado: Puesto | null = null;
  clienteEncontrado: Cliente | null = null;
  ocuparRequest: OcuparPuestoRequest = {
    puestoId: '',
    cedula: ''
  };
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  procesando: boolean = false;
  buscandoCliente: boolean = false;

  constructor(
    private puestoService: PuestoService,
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestosDisponibles();
  }

  cargarPuestosDisponibles() {
    this.procesando = true;

    this.puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE).subscribe({
      next: (data: any) => {
        this.puestosDisponibles = data;
        this.procesando = false;

        if (this.puestosDisponibles.length === 0) {
          this.mostrarMensaje('No hay puestos disponibles en este momento', 'info');
        }
      },
      error: (error) => {
        console.error('Error cargando puestos disponibles:', error);
        this.mostrarMensaje('Error al cargar puestos disponibles', 'danger');
        this.procesando = false;
      }
    });
  }

  buscarCliente() {
    if (!this.ocuparRequest.cedula) {
      this.mostrarMensaje('Por favor ingrese la cédula del cliente', 'warning');
      return;
    }

    // Validar formato de cédula (solo números)
    const cedulaRegex = /^\d+$/;
    if (!cedulaRegex.test(this.ocuparRequest.cedula)) {
      this.mostrarMensaje('La cédula debe contener solo números', 'warning');
      return;
    }

    this.buscandoCliente = true;
    this.clienteEncontrado = null;

    this.clienteService.obtenerClientePorCedula(this.ocuparRequest.cedula).subscribe({
      next: (clientes: Cliente[]) => {
        this.buscandoCliente = false;

        if (clientes.length > 0) {
          this.clienteEncontrado = clientes[0];
          this.ocuparRequest.clienteId = this.clienteEncontrado.id;
          this.mostrarMensaje(`✅ Cliente encontrado: ${this.clienteEncontrado.nombre} ${this.clienteEncontrado.apellido}`, 'success');
        } else {
          this.mostrarMensaje('❌ No se encontró ningún cliente con esta cédula', 'warning');
        }
      },
      error: (error) => {
        this.buscandoCliente = false;
        console.error('Error buscando cliente:', error);
        this.mostrarMensaje('Error al buscar el cliente', 'danger');
      }
    });
  }

  seleccionarPuesto(puesto: Puesto) {
    this.puestoSeleccionado = puesto;
    this.ocuparRequest.puestoId = puesto.id;
    this.mensaje = '';
    this.clienteEncontrado = null;
    this.ocuparRequest.cedula = '';
  }

  cancelarSeleccion() {
    this.puestoSeleccionado = null;
    this.ocuparRequest = {
      puestoId: '',
      cedula: ''
    };
    this.clienteEncontrado = null;
    this.mensaje = '';
  }

  confirmarOcupacion() {
    if (!this.ocuparRequest.cedula) {
      this.mostrarMensaje('Por favor ingrese la cédula del cliente', 'warning');
      return;
    }

    if (!this.clienteEncontrado) {
      this.mostrarMensaje('Por favor busque y verifique el cliente primero', 'warning');
      return;
    }

    if (!this.puestoSeleccionado) {
      this.mostrarMensaje('No se ha seleccionado ningún puesto', 'warning');
      return;
    }

    this.procesando = true;

    // Preparar los datos para ocupar el puesto
    const datosOcupacion = {
      puestoId: this.ocuparRequest.puestoId,
      usuario: this.clienteEncontrado.usuario, // Usar el usuario del cliente
      clienteId: this.clienteEncontrado.id,
      tipoCliente: this.clienteEncontrado.tipoPersona
    };

    console.log('Datos de ocupación:', datosOcupacion);

    this.puestoService.ocuparPuesto(datosOcupacion).subscribe({
      next: (resultado: any) => {
        this.procesando = false;
        console.log('Respuesta del servidor:', resultado);

        if (resultado.exito || resultado.success) {
          this.mostrarMensaje('✅ Puesto ocupado exitosamente', 'success');

          // Actualizar lista local
          this.puestosDisponibles = this.puestosDisponibles.filter(p => p.id !== this.ocuparRequest.puestoId);

          // Redirigir después de 2 segundos
          setTimeout(() => {
            this.router.navigate(['/gestion-puestos']);
          }, 2000);
        } else {
          const mensajeError = resultado.mensaje || resultado.message || 'Error desconocido al ocupar el puesto';
          this.mostrarMensaje('❌ ' + mensajeError, 'danger');
        }
      },
      error: (error) => {
        this.procesando = false;
        console.error('Error ocupando puesto:', error);

        let mensajeError = 'Error al ocupar el puesto';
        if (error.error?.mensaje) {
          mensajeError = error.error.mensaje;
        } else if (error.error?.message) {
          mensajeError = error.error.message;
        } else if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        }

        this.mostrarMensaje('❌ ' + mensajeError, 'danger');
      }
    });
  }

  getTipoDescripcion(tipo: string): string {
    const tipos: {[key: string]: string} = {
      'REGULAR': 'Regular',
      'DISCAPACITADO': 'Discapacitado',
      'DOCENTE': 'Docente',
      'VISITANTE': 'Visitante',
      'MOTOCICLETA': 'Motocicleta'
    };
    return tipos[tipo] || tipo;
  }

  getTipoColor(tipo: string): string {
    const colores: {[key: string]: string} = {
      'REGULAR': '#007bff',
      'DISCAPACITADO': '#6f42c1',
      'DOCENTE': '#28a745',
      'VISITANTE': '#ffc107',
      'MOTOCICLETA': '#fd7e14'
    };
    return colores[tipo] || '#6c757d';
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;
    setTimeout(() => {
      this.mensaje = '';
    }, 5000);
  }
}
