import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PuestoService } from '../../../service/puesto.service';
import { HeaderComponent } from '../header/header.component';
import { NavigationComponent } from '../navegacion/navigation.component';

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, HeaderComponent, NavigationComponent],
  templateUrl: './estadisticas.component.html',
  styleUrls: ['./estadisticas.component.css']
})
export class EstadisticasComponent implements OnInit {
  estadisticas: any = {
    total: 0,
    disponibles: 0,
    ocupados: 0,
    bloqueados: 0,
    mantenimiento: 0,
    porcentajeOcupacion: 0
  };

  constructor(
    private puestoService: PuestoService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    this.estadisticas = {
      total: 150,
      disponibles: 85,
      ocupados: 45,
      bloqueados: 15,
      mantenimiento: 5,
      porcentajeOcupacion: 30
    };

  }

  volverAPuestos() {
    this.router.navigate(['/puestos']);
  }

  irAInicio() {
    this.router.navigate(['/']);
  }
}
