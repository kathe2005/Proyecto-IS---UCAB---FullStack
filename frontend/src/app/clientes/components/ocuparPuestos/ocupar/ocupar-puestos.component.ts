import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { OcuparPuestoRequest } from '../../../models/ocupar-puesto-request.model';
import { EstadoPuesto } from '../../../models/puestos.model';
//import { NavigationComponent } from '../navegacion/navigation.component';

@Component({
  selector: 'app-ocupar-puestos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ocupar-puestos.component.html',
  styleUrls: ['./ocupar-puestos.component.css']
})
export class OcuparPuestoComponent implements OnInit {
  puestosDisponibles: any[] = [];
  puestoSeleccionado: any = null;
  ocuparRequest: OcuparPuestoRequest = {
    puestoId: '',
    usuario: '',
    clienteId: '',
    tipoCliente: ''
  };
  mensaje: string = '';
  mensajeClase: string = '';
  procesando: boolean = false;

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestosDisponibles();
  }

  cargarPuestosDisponibles() {
    this.puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE).subscribe({
      next: (data) => {
        this.puestosDisponibles = data;
      },
      error: (error) => {
        console.error('Error cargando puestos disponibles:', error);
        this.mostrarMensaje('Error al cargar puestos disponibles', 'danger');
      }
    });
  }

  seleccionarPuesto(puesto: any) {
    this.puestoSeleccionado = puesto;
    this.ocuparRequest.puestoId = puesto.id;
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
  }

  getTipoDescripcion(tipo: string): string {
    const tipos: any = {
      'REGULAR': 'Regular',
      'DISCAPACITADO': 'Discapacitado',
      'DOCENTE': 'Docente',
      'VISITANTE': 'Visitante',
      'MOTOCICLETA': 'Motocicleta'
    };
    return tipos[tipo] || tipo;
  }

  getTipoColor(tipo: string): string {
    const colores: any = {
      'REGULAR': '#007bff',
      'DISCAPACITADO': '#6f42c1',
      'DOCENTE': '#28a745',
      'VISITANTE': '#ffc107',
      'MOTOCICLETA': '#fd7e14'
    };
    return colores[tipo] || '#6c757d';
  }

  private mostrarMensaje(mensaje: string, tipo: string) {
    this.mensaje = mensaje;
    this.mensajeClase = `alert-${tipo}`;
    setTimeout(() => {
      this.mensaje = '';
    }, 5000);
  }
}
