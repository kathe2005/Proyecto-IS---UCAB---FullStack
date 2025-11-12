import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component';

// Interfaces temporales
interface Puesto {
  id: string;
  numero: string;
  tipoPuesto: string;
  estadoPuesto: string;
  ubicacion: string;
  usuarioOcupante?: string;
}

@Component({
  selector: 'app-lista-puestos',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    HeaderComponent,
    NavigationComponent
  ],
  templateUrl: './lista-puestos.component.html',
  styleUrls: ['./lista-puestos.component.css']
})
export class ListaPuestosComponent implements OnInit {
  puestos: Puesto[] = [];
  puestosFiltrados: Puesto[] = [];
  titulo = 'Todos los Puestos';
  criterioBusqueda = '';
  filtroNumero: string = '';

  // Datos de ejemplo
  private datosEjemplo: Puesto[] = [
    { id: '1', numero: 'A-01', tipoPuesto: 'REGULAR', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona A' },
    { id: '2', numero: 'A-02', tipoPuesto: 'DISCAPACITADO', estadoPuesto: 'OCUPADO', ubicacion: 'Zona A', usuarioOcupante: 'Juan Pérez' },
    { id: '3', numero: 'B-01', tipoPuesto: 'DOCENTE', estadoPuesto: 'BLOQUEADO', ubicacion: 'Zona B' },
    { id: '4', numero: 'M-01', tipoPuesto: 'MOTOCICLETA', estadoPuesto: 'DISPONIBLE', ubicacion: 'Zona Motos' },
    { id: '5', numero: 'V-01', tipoPuesto: 'VISITANTE', estadoPuesto: 'OCUPADO', ubicacion: 'Zona Visitantes', usuarioOcupante: 'María García' }
  ];

  constructor(
    private puestoService: PuestoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestos();

    this.route.queryParams.subscribe(params => {
      if (params['estado']) {
        this.buscarPorEstado(params['estado']);
      } else if (params['tipo']) {
        this.buscarPorTipo(params['tipo']);
      } else if (params['ubicacion']) {
        this.buscarPorUbicacion(params['ubicacion']);
      }
    });
  }

  cargarPuestos() {
    // Simulación
    this.puestos = [...this.datosEjemplo];
    this.puestosFiltrados = [...this.puestos];
    this.titulo = 'Todos los Puestos';
    this.criterioBusqueda = '';

    // Descomenta para usar el servicio real:
    /*
    this.puestoService.obtenerTodosLosPuestos().subscribe({
      next: (data) => {
        this.puestos = data;
        this.puestosFiltrados = data;
        this.titulo = 'Todos los Puestos';
        this.criterioBusqueda = '';
      },
      error: (error) => {
        console.error('Error cargando puestos:', error);
        this.mostrarError('Error al cargar los puestos');
      }
    });
    */
  }

  cargarPuestosDisponibles() {
    this.puestosFiltrados = this.puestos.filter(p => p.estadoPuesto === 'DISPONIBLE');
    this.titulo = 'Puestos Disponibles';
    this.criterioBusqueda = '';
  }

  cargarPuestosOcupados() {
    this.puestosFiltrados = this.puestos.filter(p => p.estadoPuesto === 'OCUPADO');
    this.titulo = 'Puestos Ocupados';
    this.criterioBusqueda = '';
  }

  buscarPorEstado(estado: string) {
    this.puestosFiltrados = this.puestos.filter(p => p.estadoPuesto === estado);
    this.titulo = `Puestos - Estado: ${this.getEstadoDescripcion(estado)}`;
    this.criterioBusqueda = `Estado: ${this.getEstadoDescripcion(estado)}`;
  }

  buscarPorTipo(tipo: string) {
    this.puestosFiltrados = this.puestos.filter(p => p.tipoPuesto === tipo);
    this.titulo = `Puestos - Tipo: ${this.getTipoDescripcion(tipo)}`;
    this.criterioBusqueda = `Tipo: ${this.getTipoDescripcion(tipo)}`;
  }

  buscarPorUbicacion(ubicacion: string) {
    this.puestosFiltrados = this.puestos.filter(p =>
      p.ubicacion.toLowerCase().includes(ubicacion.toLowerCase())
    );
    this.titulo = `Puestos - Ubicación: ${ubicacion}`;
    this.criterioBusqueda = `Ubicación: ${ubicacion}`;
  }

  buscarPorNumero() {
    if (!this.filtroNumero.trim()) {
      this.puestosFiltrados = [...this.puestos];
      return;
    }

    this.puestosFiltrados = this.puestos.filter(p =>
      p.numero.toLowerCase().includes(this.filtroNumero.toLowerCase())
    );
    this.titulo = `Puestos - Número: ${this.filtroNumero}`;
    this.criterioBusqueda = `Número: ${this.filtroNumero}`;
  }

  limpiarFiltros() {
    this.filtroNumero = '';
    this.cargarPuestos();
  }

  // Métodos de utilidad
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

  getEstadoDescripcion(estado: string): string {
    const estados: {[key: string]: string} = {
      'DISPONIBLE': 'Disponible',
      'OCUPADO': 'Ocupado',
      'BLOQUEADO': 'Bloqueado',
      'MANTENIMIENTO': 'Mantenimiento'
    };
    return estados[estado] || estado;
  }

  getEstadoColor(estado: string): string {
    const colores: {[key: string]: string} = {
      'DISPONIBLE': '#28a745',
      'OCUPADO': '#ffc107',
      'BLOQUEADO': '#6c757d',
      'MANTENIMIENTO': '#fd7e14'
    };
    return colores[estado] || '#6c757d';
  }

  getEstadoIcon(estado: string): string {
    const icons: {[key: string]: string} = {
      'DISPONIBLE': 'fas fa-check-circle',
      'OCUPADO': 'fas fa-car',
      'BLOQUEADO': 'fas fa-lock',
      'MANTENIMIENTO': 'fas fa-tools'
    };
    return icons[estado] || 'fas fa-question-circle';
  }

  getTextColor(backgroundColor: string): string {
    return '#FFFFFF'; // Texto siempre blanco para mejor contraste
  }

  // Métodos de acción
  ocuparPuesto(id: string) {
    this.router.navigate(['/puestos/ocupar'], { queryParams: { puestoId: id } });
  }

  liberarPuesto(id: string) {
    if (confirm('¿Está seguro de que desea liberar este puesto?')) {
      // Lógica para liberar puesto
      this.mostrarMensajeExito('Puesto liberado exitosamente');
      this.cargarPuestos();
    }
  }

  bloquearPuesto(id: string) {
    if (confirm('¿Está seguro de que desea bloquear este puesto?')) {
      // Lógica para bloquear puesto
      this.mostrarMensajeExito('Puesto bloqueado exitosamente');
      this.cargarPuestos();
    }
  }

  desbloquearPuesto(id: string) {
    if (confirm('¿Está seguro de que desea desbloquear este puesto?')) {
      // Lógica para desbloquear puesto
      this.mostrarMensajeExito('Puesto desbloqueado exitosamente');
      this.cargarPuestos();
    }
  }

  ponerEnMantenimiento(id: string) {
    if (confirm('¿Está seguro de que desea poner este puesto en mantenimiento?')) {
      // Lógica para mantenimiento
      this.mostrarMensajeExito('Puesto puesto en mantenimiento exitosamente');
      this.cargarPuestos();
    }
  }

  // Navegación
  volverAInicio() {
    this.router.navigate(['/']);
  }

  irABuscarPuestos() {
    this.router.navigate(['/puestos/buscar']);
  }

  irAEstadisticas() {
    this.router.navigate(['/puestos/estadisticas']);
  }

  // Utilidades
  private mostrarMensajeExito(mensaje: string) {
    alert(`✅ ${mensaje}`);
  }

  private mostrarError(mensaje: string) {
    alert(`❌ ${mensaje}`);
  }

  // Estadísticas
  getCountByEstado(estado: string): number {
    return this.puestosFiltrados.filter(puesto => puesto.estadoPuesto === estado).length;
  }
}
