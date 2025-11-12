import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { TipoPuesto, EstadoPuesto, TipoPuestoInfo, EstadoPuestoInfo } from '../../../models/puestos.model';
import { NavigationComponent } from '../navegacion/navigation.component';

@Component({
  selector: 'app-buscar-puestos',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, NavigationComponent],
  templateUrl: './buscar-puestos.component.html', // Cambiado a buscar-puestos.component.html
  styleUrls: ['./buscar-puestos.component.css']
})
export class BuscarPuestosComponent implements OnInit {
  tiposPuesto = Object.values(TipoPuesto);
  estadosPuesto = Object.values(EstadoPuesto);

  estadoSeleccionado: string = '';
  tipoSeleccionado: string = '';
  ubicacionSeleccionada: string = '';
  fechaActual: string = '';

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.actualizarFecha();
  }

  actualizarFecha() {
    const opciones: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    };

    this.fechaActual = new Date().toLocaleDateString('es-ES', opciones);
  }

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
    if (this.ubicacionSeleccionada) {
      this.router.navigate(['/puestos'], {
        queryParams: { ubicacion: this.ubicacionSeleccionada }
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
