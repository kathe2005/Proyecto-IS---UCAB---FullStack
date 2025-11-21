import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PuestoService } from '../../service/puesto.service';

interface Puesto {
  id: string;
  numero: string;
  tipoPuesto: string;
  estadoPuesto: string;
  ubicacion: string;
  usuarioOcupante?: string | null;
  fechaOcupacion?: string | null;
  fechaCreacion?: string;
}

@Component({
  selector: 'app-desocupar-puestos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './desocupar-puesto.component.html',
  styleUrls: ['./desocupar-puesto.component.css']
})
export class DesocuparPuestosComponent implements OnInit {
  puestosOcupados: Puesto[] = [];
  puestoSeleccionado: Puesto | null = null;
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  procesando: boolean = false;
  filtroUbicacion: string = '';
  filtroTipo: string = '';

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestosOcupados();
  }

  cargarPuestosOcupados() {
    this.procesando = true;

    this.puestoService.obtenerPuestosPorEstado('OCUPADO').subscribe({
      next: (data: any) => {
        this.puestosOcupados = data;
        this.procesando = false;

        if (this.puestosOcupados.length === 0) {
          this.mostrarMensaje('No hay puestos ocupados en este momento', 'info');
        }
      },
      error: (error) => {
        console.error('Error cargando puestos ocupados:', error);
        this.mostrarMensaje('Error al cargar los puestos ocupados', 'danger');
        this.procesando = false;
      }
    });
  }

  seleccionarPuesto(puesto: Puesto) {
    this.puestoSeleccionado = puesto;
    this.mensaje = '';
  }

  cancelarSeleccion() {
    this.puestoSeleccionado = null;
    this.mensaje = '';
  }

  confirmarLiberacion() {
    if (!this.puestoSeleccionado) {
      this.mostrarMensaje('Por favor seleccione un puesto', 'warning');
      return;
    }

    this.procesando = true;

    this.puestoService.liberarPuesto(this.puestoSeleccionado.id).subscribe({
      next: (response: any) => {
        this.procesando = false;
        this.mostrarMensaje('✅ Puesto liberado exitosamente', 'success');

        // Actualizar la lista local
        this.puestosOcupados = this.puestosOcupados.filter(p => p.id !== this.puestoSeleccionado?.id);
        this.puestoSeleccionado = null;

        setTimeout(() => {
          this.router.navigate(['/puestos']);
        }, 2000);
      },
      error: (error) => {
        this.procesando = false;
        console.error('Error liberando puesto:', error);

        let mensajeError = 'Error al liberar el puesto';
        if (error.error?.mensaje) {
          mensajeError = error.error.mensaje;
        } else if (error.error?.error) {
          mensajeError = error.error.error;
        }

        this.mostrarMensaje('❌ ' + mensajeError, 'danger');
      }
    });
  }

  liberarPuestoRapido(puesto: Puesto) {
    this.puestoSeleccionado = puesto;
    this.confirmarLiberacion();
  }

  get puestosFiltrados(): Puesto[] {
    let filtrados = this.puestosOcupados;

    if (this.filtroUbicacion) {
      filtrados = filtrados.filter(p =>
        p.ubicacion.toLowerCase().includes(this.filtroUbicacion.toLowerCase())
      );
    }

    if (this.filtroTipo) {
      filtrados = filtrados.filter(p =>
        this.getTipoDescripcion(p.tipoPuesto).toLowerCase().includes(this.filtroTipo.toLowerCase())
      );
    }

    return filtrados;
  }

  limpiarFiltros() {
    this.filtroUbicacion = '';
    this.filtroTipo = '';
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

  getEstadoColor(estado: string): string {
    const colores: {[key: string]: string} = {
      'DISPONIBLE': '#28a745',
      'OCUPADO': '#dc3545',
      'MANTENIMIENTO': '#fd7e14',
      'BLOQUEADO': '#6c757d',
      'RESERVADO': '#17a2b8'
    };
    return colores[estado] || '#6c757d';
  }

  formatearFecha(fecha: any): string {
    if (!fecha) return 'No disponible';

    try {
      let fechaObj: Date;

      if (typeof fecha === 'string') {
        fechaObj = new Date(fecha);
      } else if (typeof fecha === 'object' && fecha.year && fecha.monthValue && fecha.dayOfMonth) {
        const fechaStr = `${fecha.year}-${fecha.monthValue.toString().padStart(2, '0')}-${fecha.dayOfMonth.toString().padStart(2, '0')}T${fecha.hour || '00'}:${fecha.minute || '00'}:${fecha.second || '00'}`;
        fechaObj = new Date(fechaStr);
      } else if (fecha.toString) {
        fechaObj = new Date(fecha.toString());
      } else {
        fechaObj = new Date(fecha);
      }

      if (isNaN(fechaObj.getTime())) {
        return 'Fecha no válida';
      }

      return fechaObj.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });

    } catch (error) {
      console.error('Error formateando fecha:', error, fecha);
      return 'Error en fecha';
    }
  }

  volverAGestionPuestos() {
    this.router.navigate(['/gestion-puestos']);
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;
    setTimeout(() => {
      this.mensaje = '';
    }, 5000);
  }
}
