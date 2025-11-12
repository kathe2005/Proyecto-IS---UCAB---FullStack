import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; 
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component';
import { Puesto, TipoPuestoInfo, EstadoPuestoInfo, EstadoPuesto, TipoPuesto } from '../../../models/puestos.model';

@Component({
  selector: 'app-crear-puestos',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    HeaderComponent,
    NavigationComponent
  ],
  templateUrl: './crear-puestos.component.html',
  styleUrls: ['./crear-puestos.component.css']
})
export class CrearPuestosComponent implements OnInit {

  titulo = 'Crear Nuevo Puesto de Estacionamiento';
  puestos: Puesto[] = [];
  criterioBusqueda = '';
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
    'Zona Motocicletas'
  ];

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.tiposPuesto = Object.values(TipoPuesto);
    this.estadosPuesto = Object.values(EstadoPuesto);
  }
  crearPuesto() {
      this.mensaje = '';
      this.isError = false;
      if (!this.puesto.numero || !this.puesto.ubicacion) {
          this.mostrarFeedback('❌ Error: El número y la ubicación del puesto son obligatorios.', true);
          return;
      }
      this.puestoService.crearPuesto(this.puesto).subscribe({
        next: (respuesta) => {
          this.mostrarFeedback(`✅ Puesto N° ${respuesta.numero} creado exitosamente. ID: ${respuesta.id}`, false);
          this.resetFormulario();
        },
        error: (error) => {
          console.error('Error al crear el puesto:', error);
          const errorMsg = error.error?.mensaje || error.message || 'Error de conexión o validación en el servidor.';
          this.mostrarFeedback('❌ Error al crear el puesto: ' + errorMsg, true);
        }
      });
    }
  cargarPuestos() {
    this.puestoService.obtenerTodosLosPuestos().subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = 'Todos los Puestos';
        this.criterioBusqueda = '';
      },
      error: (error) => {
        console.error('Error cargando puestos:', error);
        this.mostrarError('Error al cargar los puestos');
      }
    });
  }

  cargarPuestosDisponibles() {
    this.puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = 'Puestos Disponibles';
        this.criterioBusqueda = '';
      },
      error: (error) => {
        console.error('Error cargando puestos disponibles:', error);
        this.mostrarError('Error al cargar puestos disponibles');
      }
    });
  }

  cargarPuestosOcupados() {
    this.puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = 'Puestos Ocupados';
        this.criterioBusqueda = '';
      },
      error: (error) => {
        console.error('Error cargando puestos ocupados:', error);
        this.mostrarError('Error al cargar puestos ocupados');
      }
    });
  }

  cargarPuestosBloqueados() {
    this.puestoService.obtenerPuestosPorEstado(EstadoPuesto.BLOQUEADO).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = 'Puestos Bloqueados';
        this.criterioBusqueda = '';
      },
      error: (error) => {
        console.error('Error cargando puestos bloqueados:', error);
        this.mostrarError('Error al cargar puestos bloqueados');
      }
    });
  }

  // === MÉTODOS DE BÚSQUEDA ===
  buscarPorEstado(estado: string) {
    this.puestoService.obtenerPuestosPorEstado(estado as EstadoPuesto).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = `Puestos - Estado: ${this.getEstadoDescripcion(estado)}`;
        this.criterioBusqueda = `Estado: ${this.getEstadoDescripcion(estado)}`;
      },
      error: (error) => {
        console.error('Error buscando por estado:', error);
        this.mostrarError('Error al buscar por estado');
      }
    });
  }

  buscarPorTipo(tipo: string) {
    this.puestoService.obtenerPuestosPorTipo(tipo as TipoPuesto).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = `Puestos - Tipo: ${this.getTipoDescripcion(tipo)}`;
        this.criterioBusqueda = `Tipo: ${this.getTipoDescripcion(tipo)}`;
      },
      error: (error) => {
        console.error('Error buscando por tipo:', error);
        this.mostrarError('Error al buscar por tipo');
      }
    });
  }

  buscarPorUbicacion(ubicacion: string) {
    this.puestoService.filtrarPuestosPorUbicacion(ubicacion).subscribe({
      next: (data) => {
        this.puestos = data;
        this.titulo = `Puestos - Ubicación: ${ubicacion}`;
        this.criterioBusqueda = `Ubicación: ${ubicacion}`;
      },
      error: (error) => {
        console.error('Error buscando por ubicación:', error);
        this.mostrarError('Error al buscar por ubicación');
      }
    });
  }

  // === MÉTODOS DE INFORMACIÓN Y UTILIDAD ===
  getTipoDescripcion(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.descripcion || tipo;
  }

  getTipoColor(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.color || 'gray';
  }

  getEstadoDescripcion(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.descripcion || estado;
  }

  getEstadoColor(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.color || 'gray';
  }

  getEstadoIcon(estado: string): string {
    const icons: any = {
      'DISPONIBLE': 'fas fa-check-circle',
      'OCUPADO': 'fas fa-car',
      'BLOQUEADO': 'fas fa-lock',
      'MANTENIMIENTO': 'fas fa-tools'
    };
    return icons[estado] || 'fas fa-question-circle';
  }

  // === MÉTODOS NUEVOS PARA ESTADÍSTICAS Y MEJORAS VISUALES ===
  getCountByEstado(estado: string): number {
    return this.puestos.filter(puesto => puesto.estadoPuesto === estado).length;
  }

  getTextColor(backgroundColor: string): string {
    // Función para determinar si el texto debe ser claro u oscuro según el fondo
    if (!backgroundColor || backgroundColor === 'gray') return '#FFFFFF';

    const hex = backgroundColor.replace('#', '');
    if (hex.length !== 6) return '#FFFFFF';

    try {
      const r = parseInt(hex.substr(0, 2), 16);
      const g = parseInt(hex.substr(2, 2), 16);
      const b = parseInt(hex.substr(4, 2), 16);
      const brightness = ((r * 299) + (g * 587) + (b * 114)) / 1000;
      return brightness > 128 ? '#000000' : '#FFFFFF';
    } catch (error) {
      return '#FFFFFF';
    }
  }

  getCountByTipo(tipo: string): number {
    return this.puestos.filter(puesto => puesto.tipoPuesto === tipo).length;
  }

  getPorcentajeOcupacion(): number {
    if (this.puestos.length === 0) return 0;
    const ocupados = this.getCountByEstado('OCUPADO');
    return (ocupados / this.puestos.length) * 100;
  }

  refrescarDatos() {
    this.cargarPuestos();
  }

  exportarDatos() {
    console.log('Exportando datos de puestos:', this.puestos);
    // Aquí puedes implementar la lógica para exportar a CSV, Excel, etc.
    alert('Funcionalidad de exportación en desarrollo');
  }

  buscarPorNumero(numero: string) {
    if (!numero.trim()) {
      this.cargarPuestos();
      return;
    }

    this.puestos = this.puestos.filter(puesto =>
      puesto.numero.toLowerCase().includes(numero.toLowerCase())
    );
    this.titulo = `Puestos - Número: ${numero}`;
    this.criterioBusqueda = `Número: ${numero}`;
  }

  limpiarFiltros() {
    this.cargarPuestos();
  }

  getEstadisticasResumen() {
    return {
      total: this.puestos.length,
      disponibles: this.getCountByEstado('DISPONIBLE'),
      ocupados: this.getCountByEstado('OCUPADO'),
      bloqueados: this.getCountByEstado('BLOQUEADO'),
      mantenimiento: this.getCountByEstado('MANTENIMIENTO'),
      porcentajeOcupacion: this.getPorcentajeOcupacion()
    };
  }

  verDetallesPuesto(id: string) {
    this.router.navigate(['/puestos/detalle', id]);
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
      fechaOcupacion: null,
    };
  }

  mostrarFeedback(msg: string, isError: boolean) {
    this.mensaje = msg;
    this.isError = isError;
    setTimeout(() => this.mensaje = '', 5000); 
  }

  volverAInicio() {
    this.router.navigate(['/']);
  }
  
  private mostrarError(mensaje: string) {
    // En lugar de alert, podrías usar un servicio de notificaciones
    alert(`❌ ${mensaje}`);
  }
}