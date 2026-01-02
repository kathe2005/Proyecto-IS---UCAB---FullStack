import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http'; // <--- AGREGADO IMPORTANTE

// Asegúrate de que la ruta sea correcta según tu estructura de carpetas
import { IncidenciaService } from '../../service/incidencia.service';
import { Incidencia } from '../../models/incidencia.model';

@Component({
  selector: 'app-listar-incidencias',
  standalone: true,
  providers: [IncidenciaService],
  imports: [CommonModule, RouterModule],
  templateUrl: './listar-incidencias.component.html',
  styleUrls: ['./listar-incidencias.component.css']
})
export class ListarIncidenciasComponent implements OnInit {

  incidencias: Incidencia[] = [];
  cargando: boolean = true;
  errorMsg: string = '';

  constructor(
    private incidenciaService: IncidenciaService, // Línea 26 corregida (requiere el servicio abajo)
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cargarIncidencias();
  }

  // ========== LÓGICA DE CARGA DE DATOS ==========

  cargarIncidencias(): void {
    this.cargando = true;
    // Línea 39: Si esto marca error, es porque falta el método en el Service (ver paso 2 abajo)
    this.incidenciaService.obtenerIncidencias().subscribe({
      next: (data) => {
        this.incidencias = data;
        this.cargando = false;
        console.log('✅ Incidencias cargadas:', data.length);
      },
      // Línea 44 corregida: Agregamos el tipo ': any' para evitar error de TypeScript
      error: (e: any) => { 
        console.error('❌ Error al cargar incidencias', e);
        this.errorMsg = 'No se pudieron cargar las incidencias. Verifique que el backend esté encendido.';
        this.cargando = false;
      }
    });
  }

  // ========== LÓGICA DE ESTADÍSTICAS ==========
  
  getConteoPorEstado(estado: string): number {
    if (!this.incidencias) return 0;
    return this.incidencias.filter(i => i.estado === estado).length;
  }

  // ========== NAVEGACIÓN ==========

  irACrear(): void {
    this.router.navigate(['/incidencias/crear']);
  }

  irAEditar(id: number): void {
    this.router.navigate(['/incidencias/editar', id]);
  }

  volver(): void {
    this.router.navigate(['/gestion-estacionamiento']); 
  }

  // ========== ACCIONES ==========

  eliminar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta incidencia permanentemente?')) {
      // Línea 86: Si esto marca error, falta el método en el Service
      this.incidenciaService.eliminarIncidencia(id).subscribe({
        next: () => {
          alert('Incidencia eliminada con éxito');
          this.cargarIncidencias(); 
        },
        error: (e: any) => { // Agregado ': any'
          alert('Error al eliminar la incidencia');
          console.error(e);
        }
      });
    }
  }
}