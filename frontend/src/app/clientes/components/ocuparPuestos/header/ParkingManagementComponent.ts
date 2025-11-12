import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './header.component';

@Component({
  selector: 'app-parking-management',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './parking-management.component.html',
  styleUrls: ['./parking-management.component.css']
})
export class ParkingManagementComponent implements OnInit {
  totalPuestos: number = 0;
  puestosDisponibles: number = 0;
  puestosOcupados: number = 0;
  puestosBloqueados: number = 0;
  puestosMantenimiento: number = 0;
  porcentajeOcupacion: number = 0;

  ngOnInit() {
    this.cargarDatosIniciales();
  }

  private cargarDatosIniciales() {
    // Simular datos de ejemplo - en una app real esto vendría de un servicio
    setTimeout(() => {
      this.totalPuestos = 150;
      this.puestosDisponibles = 85;
      this.puestosOcupados = 45;
      this.puestosBloqueados = 15;
      this.puestosMantenimiento = 5;
      this.porcentajeOcupacion = (this.puestosOcupados / this.totalPuestos) * 100;
    }, 1000);
  }
}
