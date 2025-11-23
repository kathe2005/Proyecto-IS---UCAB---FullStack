import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Agregar RouterModule
import { PuestoService } from '../../service/puesto.service';
import { Puesto } from '../../models/puestos.model';

@Component({
  selector: 'app-lista-puestos',
  standalone: true,
  imports: [CommonModule, RouterModule], // Agregar RouterModule aquí
  templateUrl: './lista-puestos.component.html',
  styleUrls: ['./lista-puestos.component.css']
})
export class ListaPuestosComponent implements OnInit {
  puestos: Puesto[] = [];
  puestosFiltrados: Puesto[] = [];
  filtro: string = '';
  cargando: boolean = true;
  criterioBusqueda: string = ''; // Agregar esta propiedad

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarPuestos();
  }

  cargarPuestos() {
    this.cargando = true;
    this.puestoService.obtenerTodosLosPuestos().subscribe({
      next: (data: Puesto[]) => {
        this.puestos = data;
        this.puestosFiltrados = data;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error cargando puestos:', error);
        this.cargando = false;
      }
    });
  }

  aplicarFiltro() {
    if (!this.filtro) {
      this.puestosFiltrados = this.puestos;
      this.criterioBusqueda = ''; // Limpiar criterio cuando no hay filtro
      return;
    }

    const filtroLower = this.filtro.toLowerCase();
    this.puestosFiltrados = this.puestos.filter(puesto =>
      puesto.numero.toLowerCase().includes(filtroLower) ||
      puesto.ubicacion.toLowerCase().includes(filtroLower) ||
      this.getTipoDescripcion(puesto.tipoPuesto).toLowerCase().includes(filtroLower) ||
      this.getEstadoDescripcion(puesto.estadoPuesto).toLowerCase().includes(filtroLower)
    );

    this.criterioBusqueda = `Búsqueda: "${this.filtro}"`; // Establecer criterio de búsqueda
  }

  // Método para contar puestos por estado
  getCountByEstado(estado: string): number {
    return this.puestos.filter(p => p.estadoPuesto === estado).length;
  }

  // Métodos para las acciones de los puestos
  ocuparPuesto(id: string) {
    this.router.navigate(['/ocupar-puestos']);
  }

  liberarPuesto(id: string) {
    this.puestoService.liberarPuesto(id).subscribe({
      next: (response: any) => {
        console.log('Puesto liberado:', response);
        this.cargarPuestos(); // Recargar la lista
      },
      error: (error) => {
        console.error('Error liberando puesto:', error);
        alert('Error al liberar el puesto');
      }
    });
  }

  bloquearPuesto(id: string) {
    this.puestoService.bloquearPuesto(id).subscribe({
      next: (response: any) => {
        console.log('Puesto bloqueado:', response);
        this.cargarPuestos(); // Recargar la lista
      },
      error: (error) => {
        console.error('Error bloqueando puesto:', error);
        alert('Error al bloquear el puesto');
      }
    });
  }

  desbloquearPuesto(id: string) {
    this.puestoService.desbloquearPuesto(id).subscribe({
      next: (response: any) => {
        console.log('Puesto desbloqueado:', response);
        this.cargarPuestos(); // Recargar la lista
      },
      error: (error) => {
        console.error('Error desbloqueando puesto:', error);
        alert('Error al desbloquear el puesto');
      }
    });
  }

  ponerEnMantenimiento(id: string) {
    this.puestoService.ponerEnMantenimiento(id).subscribe({
      next: (response: any) => {
        console.log('Puesto en mantenimiento:', response);
        this.cargarPuestos(); // Recargar la lista
      },
      error: (error) => {
        console.error('Error poniendo en mantenimiento:', error);
        alert('Error al poner en mantenimiento');
      }
    });
  }

  volverAInicio() {
    this.router.navigate(['/']);
  }

  // Métodos de utilidad para mostrar información
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
      'RESERVADO': 'Reservado',
      'BLOQUEADO': 'Bloqueado',
      'MANTENIMIENTO': 'Mantenimiento'
    };
    return estados[estado] || estado;
  }

  getEstadoColor(estado: string): string {
    const colores: {[key: string]: string} = {
      'DISPONIBLE': '#28a745',
      'OCUPADO': '#dc3545',
      'RESERVADO': '#17a2b8',
      'BLOQUEADO': '#6c757d',
      'MANTENIMIENTO': '#fd7e14'
    };
    return colores[estado] || '#6c757d';
  }

  getEstadoIcon(estado: string): string {
    const iconos: {[key: string]: string} = {
      'DISPONIBLE': 'fas fa-check-circle',
      'OCUPADO': 'fas fa-times-circle',
      'RESERVADO': 'fas fa-clock',
      'BLOQUEADO': 'fas fa-ban',
      'MANTENIMIENTO': 'fas fa-tools'
    };
    return iconos[estado] || 'fas fa-question-circle';
  }

  getTextColor(colorFondo: string): string {
    // Convertir color hexadecimal a RGB
    const hex = colorFondo.replace('#', '');
    const r = parseInt(hex.substr(0, 2), 16);
    const g = parseInt(hex.substr(2, 2), 16);
    const b = parseInt(hex.substr(4, 2), 16);

    // Calcular luminosidad
    const luminosidad = (0.299 * r + 0.587 * g + 0.114 * b) / 255;

    return luminosidad > 0.5 ? '#000000' : '#ffffff';
  }

  navegarAOcupar() {
    this.router.navigate(['/ocupar-puestos']);
  }

  navegarADesocupar() {
    this.router.navigate(['/desocupar-puestos']);
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
}
