// crear-puesto.component.ts - CORREGIDO
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CrearPuestoService } from '../../service/crear-puesto.service';
import { CrearPuestoRequest } from '../../models/crear-puesto.model';
// Importar directamente desde puestos.model
import { TipoPuesto, EstadoPuesto } from '../../models/puestos.model';

@Component({
  selector: 'app-crear-puesto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-puesto.component.html',
  styleUrls: ['./crear-puesto.component.css']
})
export class CrearPuestoComponent implements OnInit {
  puestoRequest: CrearPuestoRequest = {
    numero: '',
    ubicacion: '',
    tipoPuesto: TipoPuesto.REGULAR, // Usando el enum
    estadoPuesto: EstadoPuesto.DISPONIBLE // Usando el enum
  };

  tiposPuesto: { value: TipoPuesto; label: string }[] = [];
  estadosPuesto: { value: EstadoPuesto; label: string }[] = [];

  // Estados de la UI
  procesando: boolean = false;
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  errores: string[] = [];

  constructor(
    private crearPuestoService: CrearPuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarOpciones();
  }

  cargarOpciones() {
    this.tiposPuesto = this.crearPuestoService.obtenerTiposPuesto();
    this.estadosPuesto = this.crearPuestoService.obtenerEstadosPuesto();
  }

  crearPuesto() {
    // Validaciones básicas
    if (!this.validarFormulario()) {
      return;
    }

    this.procesando = true;
    this.mensaje = '';
    this.errores = [];

    console.log('📤 Enviando datos al backend:', {
      url: 'http://localhost:8080/puestos/api',
      datos: this.puestoRequest
    });

    this.crearPuestoService.crearPuesto(this.puestoRequest).subscribe({
      next: (response: any) => {
        this.procesando = false;
        console.log('✅ Respuesta del backend:', response);

        if (response.id) {
          this.mostrarMensaje(`✅ Puesto creado exitosamente: ${response.numero} (ID: ${response.id})`, 'success');

          setTimeout(() => {
            this.limpiarFormulario();
            this.router.navigate(['/puestos']);
          }, 2000);

        } else if (response.error) {
          console.error('❌ Error del backend:', response.error);
          this.mostrarMensaje('❌ ' + response.error, 'danger');
        }
      },
      error: (error) => {
        this.procesando = false;
        console.error('❌ Error HTTP:', error);

        if (error.status === 0) {
          this.mostrarMensaje('❌ No se puede conectar con el servidor', 'danger');
        } else if (error.error?.error) {
          this.mostrarMensaje('❌ ' + error.error.error, 'danger');
        } else {
          this.mostrarMensaje('❌ Error al crear el puesto. Intente nuevamente.', 'danger');
        }
      },
      complete: () => {
        console.log('✅ Petición completada');
      }
    });
  }

  validarFormulario(): boolean {
    this.errores = [];

    if (!this.puestoRequest.numero.trim()) {
      this.errores.push('El número de puesto es requerido');
    }

    if (!this.puestoRequest.ubicacion.trim()) {
      this.errores.push('La ubicación es requerida');
    }

    if (this.errores.length > 0) {
      this.mostrarMensaje('Por favor complete todos los campos requeridos', 'warning');
      return false;
    }

    return true;
  }

  limpiarFormulario() {
    this.puestoRequest = {
      numero: '',
      ubicacion: '',
      tipoPuesto: TipoPuesto.REGULAR,
      estadoPuesto: EstadoPuesto.DISPONIBLE
    };
    this.errores = [];
    this.mensaje = '';
  }

  volverALista() {
    this.router.navigate(['/puestos']);
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;

    // Auto-ocultar mensajes después de 5 segundos
    if (tipo !== 'danger') {
      setTimeout(() => {
        this.mensaje = '';
      }, 5000);
    }
  }

  // Métodos de utilidad para la vista
  getTipoDescripcion(tipo: TipoPuesto): string {
    const descripciones: {[key: string]: string} = {
      [TipoPuesto.REGULAR]: 'Regular',
      [TipoPuesto.DISCAPACITADO]: 'Discapacitado',
      [TipoPuesto.DOCENTE]: 'Docente',
      [TipoPuesto.VISITANTE]: 'Visitante',
      [TipoPuesto.MOTOCICLETA]: 'Motocicleta'
    };
    return descripciones[tipo] || tipo;
  }

  getEstadoDescripcion(estado: EstadoPuesto): string {
    const descripciones: {[key: string]: string} = {
      [EstadoPuesto.DISPONIBLE]: 'Disponible',
      [EstadoPuesto.OCUPADO]: 'Ocupado',
      [EstadoPuesto.BLOQUEADO]: 'Bloqueado',
      [EstadoPuesto.MANTENIMIENTO]: 'Mantenimiento',
      [EstadoPuesto.RESERVADO]: 'Reservado'
    };
    return descripciones[estado] || estado;
  }

  getTipoColor(tipo: TipoPuesto): string {
    const colores: {[key: string]: string} = {
      [TipoPuesto.REGULAR]: '#007bff',
      [TipoPuesto.DISCAPACITADO]: '#6f42c1',
      [TipoPuesto.DOCENTE]: '#28a745',
      [TipoPuesto.VISITANTE]: '#ffc107',
      [TipoPuesto.MOTOCICLETA]: '#fd7e14'
    };
    return colores[tipo] || '#6c757d';
  }

  getEstadoColor(estado: EstadoPuesto): string {
    const colores: {[key: string]: string} = {
      [EstadoPuesto.DISPONIBLE]: '#28a745',
      [EstadoPuesto.OCUPADO]: '#ffc107',
      [EstadoPuesto.BLOQUEADO]: '#6c757d',
      [EstadoPuesto.MANTENIMIENTO]: '#fd7e14',
      [EstadoPuesto.RESERVADO]: '#17a2b8'
    };
    return colores[estado] || '#6c757d';
  }
}
