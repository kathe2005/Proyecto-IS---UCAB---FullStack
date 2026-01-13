import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router'; // ActivatedRoute es vital aquí
import { IncidenciaService } from '../../service/incidencia.service';
import { Incidencia } from '../../models/incidencia.model';


@Component({
  selector: 'app-editar-incidencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [IncidenciaService], // Tu provider local
  templateUrl: './editar-incidencias.component.html',
  styleUrls: ['./editar-incidencias.component.css']
})
export class EditarIncidenciasComponent implements OnInit {

  idIncidencia: number = 0;
  
  // Objeto base
  incidencia: any = {
    clienteAfectado: '',
    tipoProblema: '',
    descripcion: '',
    estado: ''
  };

  // Listas para los Selects
  tiposProblema = [
    { valor: 'PAGO', etiqueta: 'Problema con Pago' },
    { valor: 'RESERVA', etiqueta: 'Problema con Reserva' },
    { valor: 'PUESTO', etiqueta: 'Problema con el Puesto / Barrera' },
    { valor: 'OTROS', etiqueta: 'Otro tipo de problema' }
  ];
  estados = [
    { valor: 'NO_ATENDIDA', etiqueta: 'Pendiente / No Atendida' },
    { valor: 'ATENDIDA', etiqueta: 'Resuelta / Atendida' },
    { valor: 'CANCELADA', etiqueta: 'Cancelada' }
  ];

  procesando: boolean = false;
  cargandoDatos: boolean = true;
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' = 'success';

  constructor(
    private incidenciaService: IncidenciaService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 1. Obtener el ID de la URL
    this.idIncidencia = this.route.snapshot.params['id'];
    
    // 2. Cargar los datos existentes
    if (this.idIncidencia) {
      this.cargarDatos();
    } else {
      this.mostrarMensaje('Error: No se especificó un ID válido.', 'danger');
      this.cargandoDatos = false;
    }
  }

  cargarDatos(): void {
    this.incidenciaService.obtenerIncidenciaPorId(this.idIncidencia).subscribe({
      next: (data) => {
        this.incidencia = data;
        this.cargandoDatos = false;
      },
      error: (e) => {
        console.error(e);
        this.mostrarMensaje('No se pudo cargar la incidencia.', 'danger');
        this.cargandoDatos = false;
      }
    });
  }

  guardarCambios(): void {
    if (!this.incidencia.clienteAfectado || !this.incidencia.descripcion) {
      this.mostrarMensaje('Por favor complete los campos obligatorios.', 'warning');
      return;
    }

    this.procesando = true;

    // Usamos un método actualizarIncidencia (asegúrate de tenerlo en el servicio, ver abajo)
    // Si tu servicio usa PUT en el mismo endpoint, ajusta según necesidad.
    // Aquí asumo que usaremos crearIncidencia o un nuevo endpoint PUT.
    // Dado que el Controller Java tiene PUT para estado pero no explícito para todo el cuerpo, 
    // lo ideal sería agregar un PUT general en Java, pero para no complicarte:
    // **Truco**: Si solo quieres cambiar estado, usa cambiarEstado. 
    // Si quieres editar todo, necesitamos asegurar que el Backend tenga un PUT general.
    
    // Por ahora, intentaremos guardar asumiendo que agregaste el método actualizarIncidencia en el paso anterior.
    this.incidenciaService.actualizarIncidencia(this.idIncidencia, this.incidencia).subscribe({
      next: () => {
        this.mostrarMensaje('Cambios guardados correctamente.', 'success');
        setTimeout(() => {
          this.router.navigate(['/incidencias']);
        }, 1500);
      },
      error: (e) => {
        console.error(e);
        this.mostrarMensaje('Error al guardar cambios.', 'danger');
        this.procesando = false;
      }
    });
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'danger' | 'warning') {
    this.mensaje = texto;
    this.mensajeTipo = tipo;
  }

  volver(): void {
    this.router.navigate(['/incidencias']);
  }
}