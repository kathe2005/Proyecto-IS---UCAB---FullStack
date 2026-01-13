import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para formularios
import { Router } from '@angular/router';
import { IncidenciaService } from '../../service/incidencia.service';
import { Incidencia } from '../../models/incidencia.model';

@Component({
  selector: 'app-crear-incidencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [IncidenciaService],
  templateUrl: './crear-incidencias.component.html',
  styleUrls: ['./crear-incidencias.component.css']
})
export class CrearIncidenciaComponent {
  incidencia: any = {
    clienteAfectado: '',
    tipoProblema: 'PUESTO',
    descripcion: '',
    estado: 'NO_ATENDIDA'
  };
  tiposProblema = [
    { valor: 'PAGO', etiqueta: 'Problema con Pago' },
    { valor: 'RESERVA', etiqueta: 'Problema con Reserva' },
    { valor: 'PUESTO', etiqueta: 'Problema con el Puesto / Barrera' },
    { valor: 'OTROS', etiqueta: 'Otro tipo de problema' }
  ];
  procesando: boolean = false;
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' = 'success';

  constructor(
    private incidenciaService: IncidenciaService,
    private router: Router
  ) {}

  crear(): void {
    if (!this.incidencia.clienteAfectado || !this.incidencia.descripcion) {
      this.mostrarMensaje('Por favor complete los campos obligatorios.', 'warning');
      return;
    }

    this.procesando = true;
    console.log('Enviando datos:', this.incidencia);

    this.incidenciaService.crearIncidencia(this.incidencia).subscribe({
      next: (resp) => {
        console.log('Respuesta servidor:', resp);
        this.mostrarMensaje('Incidencia registrada correctamente.', 'success');
        setTimeout(() => {
          this.router.navigate(['/incidencias']);
        }, 1500);
      },
      error: (e) => {
        console.error('Error reportado:', e);
        if (e.status === 400) {
           this.mostrarMensaje('Error de datos: El servidor no aceptó los valores enviados (Revise Enums).', 'danger');
        } else {
           this.mostrarMensaje('Error al conectar con el servidor.', 'danger');
        }
        this.procesando = false;
      }
    });
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'danger' | 'warning') {
    this.mensaje = texto;
    this.mensajeTipo = tipo;
    setTimeout(() => this.mensaje = '', 5000);
  }

  limpiarFormulario(): void {
    this.incidencia = {
      clienteAfectado: '',
      tipoProblema: 'PUESTO',
      descripcion: '',
      estado: 'NO_ATENDIDA'
    };
    this.mensaje = '';
  }

  volver(): void {
    this.router.navigate(['/incidencias']);
  }
}