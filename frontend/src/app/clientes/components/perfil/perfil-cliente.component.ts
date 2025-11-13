import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { Cliente } from '../../models/cliente';

@Component({
  selector: 'app-perfil-cliente',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './perfil-cliente.component.html',
  styleUrls: ['./perfil-cliente.component.css']
})
export class PerfilClienteComponent implements OnInit {
  // Temporal: cambiar por valor dinámico según autenticación
  usuario: string = 'ucabtest';

  cliente: Cliente | null = null;
  reserva: any = null;
  zona: any = null;
  loading: boolean = false;
  error: string | null = null;

  constructor(private clienteService: ClienteService) { }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    this.error = null;

    this.clienteService.obtenerPerfil(this.usuario).subscribe({
      next: (c) => { this.cliente = c; },
      error: (err) => { this.error = 'No se encontró el perfil del usuario'; }
    });

    this.clienteService.obtenerReservaActiva(this.usuario).subscribe({
      next: (r) => { this.reserva = r; },
      error: () => { /* ignorar temporalmente */ }
    });

    this.clienteService.obtenerZonaActual(this.usuario).subscribe({
      next: (z) => { this.zona = z; },
      error: () => { /* ignorar temporalmente */ }
    });

    // Pequeña espera visual; en llamadas reales usar combineLatest o forkJoin
    setTimeout(() => { this.loading = false; }, 300);
  }

}
