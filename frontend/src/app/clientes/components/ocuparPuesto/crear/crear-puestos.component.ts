import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { Puesto, TipoPuestoInfo, EstadoPuestoInfo, EstadoPuesto, TipoPuesto } from '../../../models/puestos.model';

@Component({
  selector: 'app-crear-puestos',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    HeaderComponent,
  ],
  templateUrl: './crear-puestos.component.html',
  styleUrls: ['./crear-puestos.component.css']
})
export class CrearPuestosComponent implements OnInit {
  titulo = 'Crear Nuevo Puesto de Estacionamiento';
  puesto: Puesto = {
    id: '',
    numero: '',
    ubicacion: '',
    tipoPuesto: TipoPuesto.REGULAR,
    estadoPuesto: EstadoPuesto.DISPONIBLE,
    fechaCreacion: '',
    historialOcupacion: [],
    usuarioOcupante: null,
    fechaOcupacion: null
  };

  tiposPuesto = Object.values(TipoPuesto);
  estadosPuesto = Object.values(EstadoPuesto);
  mensaje: string = '';
  isError: boolean = false;
  ubicacionesDisponibles: string[] = [
    'Zona A',
    'Zona B',
    'Zona C',
    'Zona Motocicletas',
    'Zona Docentes',
    'Zona Visitantes',
    'Zona Discapacitados'
  ];

  formSubmitted = false;
  isLoading = false;

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.tiposPuesto = Object.values(TipoPuesto);
    this.estadosPuesto = Object.values(EstadoPuesto);
  }

  crearPuesto() {
    this.formSubmitted = true;
    this.mensaje = '';
    this.isError = false;

    if (!this.validarFormulario()) {
      this.mostrarFeedback('❌ Por favor, complete todos los campos obligatorios correctamente.', true);
      return;
    }

    this.isLoading = true;

    // Preparar el objeto puesto para enviar (sin ID, el backend lo genera)
    const puestoParaEnviar: Puesto = {
      ...this.puesto,
      id: '', // El backend generará el ID
      fechaCreacion: new Date().toISOString(),
      historialOcupacion: [],
      usuarioOcupante: null,
      fechaOcupacion: null
    };

    console.log('Enviando puesto:', puestoParaEnviar);

    this.puestoService.crearPuesto(puestoParaEnviar).subscribe({
      next: (respuesta) => {
        this.mostrarFeedback(`✅ Puesto N° ${respuesta.numero} creado exitosamente. ID: ${respuesta.id}`, false);
        this.resetFormulario();
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/puestos']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error al crear el puesto:', error);
        const errorMsg = error.error?.message || error.message || 'Error de conexión con el servidor.';
        this.mostrarFeedback('❌ Error al crear el puesto: ' + errorMsg, true);
        this.isLoading = false;
      }
    });
  }

  validarFormulario(): boolean {
    return !!(
      this.puesto.numero &&
      this.puesto.numero.trim() !== '' &&
      this.puesto.ubicacion &&
      this.puesto.ubicacion.trim() !== '' &&
      this.puesto.tipoPuesto &&
      this.puesto.estadoPuesto
    );
  }

  getTipoDescripcion(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.descripcion || tipo;
  }

  getEstadoDescripcion(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.descripcion || estado;
  }

  resetFormulario() {
    this.puesto = {
      id: '',
      numero: '',
      ubicacion: '',
      tipoPuesto: TipoPuesto.REGULAR,
      estadoPuesto: EstadoPuesto.DISPONIBLE,
      fechaCreacion: '',
      historialOcupacion: [],
      usuarioOcupante: null,
      fechaOcupacion: null
    };
    this.formSubmitted = false;
  }

  mostrarFeedback(msg: string, isError: boolean) {
    this.mensaje = msg;
    this.isError = isError;
    setTimeout(() => this.mensaje = '', 5000);
  }

  volverAInicio() {
    this.router.navigate(['/inicio']);
  }

  volverAListaPuestos() {
    this.router.navigate(['/puestos']);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.puesto[fieldName as keyof Puesto];
    return this.formSubmitted && (!field || (typeof field === 'string' && field.trim() === ''));
  }
}
