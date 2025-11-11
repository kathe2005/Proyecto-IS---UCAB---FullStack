import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component';

// Enums temporales - reemplaza con tus enums reales
enum TipoPuesto {
  REGULAR = 'REGULAR',
  DISCAPACITADO = 'DISCAPACITADO',
  DOCENTE = 'DOCENTE',
  VISITANTE = 'VISITANTE',
  MOTOCICLETA = 'MOTOCICLETA'
}

enum EstadoPuesto {
  DISPONIBLE = 'DISPONIBLE',
  OCUPADO = 'OCUPADO',
  BLOQUEADO = 'BLOQUEADO',
  MANTENIMIENTO = 'MANTENIMIENTO'
}

const TipoPuestoInfo = {
  [TipoPuesto.REGULAR]: { descripcion: 'Regular', color: '#007bff' },
  [TipoPuesto.DISCAPACITADO]: { descripcion: 'Discapacitado', color: '#6f42c1' },
  [TipoPuesto.DOCENTE]: { descripcion: 'Docente', color: '#28a745' },
  [TipoPuesto.VISITANTE]: { descripcion: 'Visitante', color: '#ffc107' },
  [TipoPuesto.MOTOCICLETA]: { descripcion: 'Motocicleta', color: '#fd7e14' }
};

const EstadoPuestoInfo = {
  [EstadoPuesto.DISPONIBLE]: { descripcion: 'Disponible', color: '#28a745' },
  [EstadoPuesto.OCUPADO]: { descripcion: 'Ocupado', color: '#ffc107' },
  [EstadoPuesto.BLOQUEADO]: { descripcion: 'Bloqueado', color: '#6c757d' },
  [EstadoPuesto.MANTENIMIENTO]: { descripcion: 'Mantenimiento', color: '#fd7e14' }
};

@Component({
  selector: 'app-buscar-puestos',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, NavigationComponent],
  templateUrl: './buscar-puestos.component.html',
  styleUrls: ['./buscar-puestos.component.css']
})
export class BuscarPuestosComponent implements OnInit {
  tiposPuesto = Object.values(TipoPuesto);
  estadosPuesto = Object.values(EstadoPuesto);

  estadoSeleccionado: string = '';
  tipoSeleccionado: string = '';
  ubicacionSeleccionada: string = '';

  constructor(private router: Router) {}

  ngOnInit() {}

  getTipoDescripcion(tipo: string): string {
    return TipoPuestoInfo[tipo as keyof typeof TipoPuestoInfo]?.descripcion || tipo;
  }

  getEstadoDescripcion(estado: string): string {
    return EstadoPuestoInfo[estado as keyof typeof EstadoPuestoInfo]?.descripcion || estado;
  }

  buscarPorEstado() {
    if (this.estadoSeleccionado) {
      this.router.navigate(['/puestos'], {
        queryParams: { estado: this.estadoSeleccionado }
      });
    }
  }

  buscarPorTipo() {
    if (this.tipoSeleccionado) {
      this.router.navigate(['/puestos'], {
        queryParams: { tipo: this.tipoSeleccionado }
      });
    }
  }

  buscarPorUbicacion() {
    if (this.ubicacionSeleccionada.trim()) {
      this.router.navigate(['/puestos'], {
        queryParams: { ubicacion: this.ubicacionSeleccionada.trim() }
      });
    }
  }

  volverAPuestos() {
    this.router.navigate(['/puestos']);
  }

  irAInicio() {
    this.router.navigate(['/']);
  }
}
