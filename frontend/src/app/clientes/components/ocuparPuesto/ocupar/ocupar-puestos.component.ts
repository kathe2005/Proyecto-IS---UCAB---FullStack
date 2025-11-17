import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';

interface Puesto {
  id: string;
  numero: string;
  tipoPuesto: string;
  estadoPuesto: string;
  ubicacion: string;
}

interface OcuparPuestoRequest {
  puestoId: string;
  usuario: string;
  clienteId: string;
  tipoCliente: string;
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
  ocuparRequest: OcuparPuestoRequest = {
    puestoId: '',
    usuario: '',
    clienteId: '',
    tipoCliente: ''
  };
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  procesando: boolean = false;

  // Datos de ejemplo
  private datosEjemplo: Puesto[] = [
    { id: '1', numero: 'A-01', tipoPuesto: 'REGULAR', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona A' },
    { id: '4', numero: 'M-01', tipoPuesto: 'MOTOCICLETA', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona Motos' },
    { id: '6', numero: 'A-03', tipoPuesto: 'REGULAR', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona A' }
  ];

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestosDisponibles();
  }

  cargarPuestosDisponibles() {
    // Simulación
    this.puestosDisponibles = this.datosEjemplo.filter(p => p.estadoPuesto === 'DISPONIBLE');

    // Descomenta para usar el servicio real:
    /*
    this.puestoService.obtenerPuestosPorEstado('DISPONIBLE').subscribe({
      next: (data) => {
        this.puestosDisponibles = data;
      },
      error: (error) => {
        console.error('Error cargando puestos disponibles:', error);
        this.mostrarMensaje('Error al cargar puestos disponibles', 'danger');
      }
    });
    */
  }

  seleccionarPuesto(puesto: Puesto) {
    this.puestoSeleccionado = puesto;
    this.ocuparRequest.puestoId = puesto.id;
    this.mensaje = '';
  }

  cancelarSeleccion() {
    this.puestoSeleccionado = null;
    this.ocuparRequest = {
      puestoId: '',
      usuario: '',
      clienteId: '',
      tipoCliente: ''
    };
    this.mensaje = '';
  }

  confirmarOcupacion() {
    if (!this.ocuparRequest.usuario || !this.ocuparRequest.clienteId || !this.ocuparRequest.tipoCliente) {
      this.mostrarMensaje('Por favor complete todos los campos', 'warning');
      return;
    }

    this.procesando = true;

    // Simulación de ocupación
    setTimeout(() => {
      this.procesando = false;
      this.mostrarMensaje('✅ Puesto ocupado exitosamente', 'success');

      setTimeout(() => {
        this.router.navigate(['/puestos']);
      }, 2000);
    }, 1500);

    // Descomenta para usar el servicio real:
    /*
    this.puestoService.ocuparPuesto(this.ocuparRequest).subscribe({
      next: (resultado) => {
        this.procesando = false;
        if (resultado.exito) {
          this.mostrarMensaje('✅ ' + resultado.mensaje, 'success');
          setTimeout(() => {
            this.router.navigate(['/puestos']);
          }, 2000);
        } else {
          this.mostrarMensaje('❌ ' + resultado.mensaje, 'danger');
        }
      },
      error: (error) => {
        this.procesando = false;
        console.error('Error ocupando puesto:', error);
        this.mostrarMensaje('Error al ocupar el puesto', 'danger');
      }
    });
    */
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
