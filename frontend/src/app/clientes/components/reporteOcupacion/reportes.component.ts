import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../service/reporte.service';
import { ReporteOcupacion, ReporteDetallado, EstadisticasRapidas } from '../../models/reporte.model';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit {
  // Datos
  estadisticasRapidas: EstadisticasRapidas | null = null;
  reporteActual: ReporteOcupacion | null = null;
  reporteDetallado: ReporteDetallado | null = null;
  tendencia: ReporteOcupacion[] = [];

  // Filtros
  fechaSeleccionada: string = new Date().toISOString().split('T')[0];
  turnoSeleccionado: string = 'MAÑANA';
  tipoReporte: string = 'basico';

  // Estados
  cargando: boolean = false;
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';

  // Opciones
  turnos = [
    { valor: 'MAÑANA', texto: 'Mañana (6:00 - 14:00)' },
    { valor: 'TARDE', texto: 'Tarde (14:00 - 22:00)' },
    { valor: 'NOCHE', texto: 'Noche (22:00 - 6:00)' }
  ];

  tiposReporte = [
    { valor: 'basico', texto: 'Reporte Básico' },
    { valor: 'detallado', texto: 'Reporte Detallado' }
  ];

  constructor(private reporteService: ReporteService) {}

  ngOnInit() {
    this.cargarEstadisticasRapidas();
    this.cargarTendencia();
  }

  // MÉTODO AGREGADO PARA ITERAR SOBRE OBJETOS EN EL TEMPLATE
  objectKeys(obj: any): string[] {
    if (!obj) return [];
    return Object.keys(obj);
  }

  cargarEstadisticasRapidas() {
    this.cargando = true;
    this.reporteService.obtenerEstadisticasRapidas().subscribe({
      next: (data) => {
        this.estadisticasRapidas = data;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error cargando estadísticas:', error);
        this.mostrarMensaje('Error al cargar estadísticas rápidas', 'danger');
        this.cargando = false;
      }
    });
  }

  cargarTendencia() {
    this.reporteService.obtenerTendenciaOcupacion().subscribe({
      next: (data) => {
        this.tendencia = data;
      },
      error: (error) => {
        console.error('Error cargando tendencia:', error);
      }
    });
  }

  generarReporte() {
    if (!this.fechaSeleccionada) {
      this.mostrarMensaje('Por favor seleccione una fecha', 'warning');
      return;
    }

    this.cargando = true;
    this.reporteDetallado = null;

    if (this.tipoReporte === 'basico') {
      this.reporteService.obtenerReporteOcupacion(this.fechaSeleccionada, this.turnoSeleccionado).subscribe({
        next: (data) => {
          this.reporteActual = data;
          this.cargando = false;
          this.mostrarMensaje('Reporte generado exitosamente', 'success');
        },
        error: (error) => {
          console.error('Error generando reporte:', error);
          this.mostrarMensaje('Error al generar el reporte', 'danger');
          this.cargando = false;
        }
      });
    } else {
      this.reporteService.obtenerReporteDetallado(this.fechaSeleccionada, this.turnoSeleccionado).subscribe({
        next: (data) => {
          this.reporteDetallado = data;
          this.reporteActual = data.resumen;
          this.cargando = false;
          this.mostrarMensaje('Reporte detallado generado exitosamente', 'success');
        },
        error: (error) => {
          console.error('Error generando reporte detallado:', error);
          this.mostrarMensaje('Error al generar el reporte detallado', 'danger');
          this.cargando = false;
        }
      });
    }
  }

  descargarReporte() {
    if (!this.reporteActual) {
      this.mostrarMensaje('No hay reporte para descargar', 'warning');
      return;
    }

    const contenido = this.generarContenidoDescarga();
    const blob = new Blob([contenido], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `reporte-ocupacion-${this.fechaSeleccionada}-${this.turnoSeleccionado}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);

    this.mostrarMensaje('Reporte descargado exitosamente', 'success');
  }

  private generarContenidoDescarga(): string {
    if (!this.reporteActual) return '';

    let contenido = `REPORTE DE OCUPACIÓN - UCAB ESTACIONAMIENTO\n`;
    contenido += `Fecha: ${this.reporteActual.fecha} | Turno: ${this.reporteActual.turno}\n`;
    contenido += `============================================\n\n`;
    contenido += `RESUMEN GENERAL:\n`;
    contenido += `• Total de puestos: ${this.reporteActual.totalPuestos}\n`;
    contenido += `• Puestos ocupados: ${this.reporteActual.puestosOcupados}\n`;
    contenido += `• Puestos disponibles: ${this.reporteActual.puestosDisponibles}\n`;
    contenido += `• Porcentaje de ocupación: ${this.reporteActual.porcentajeOcupacion}%\n\n`;

    contenido += `OCUPACIÓN POR TIPO:\n`;
    Object.entries(this.reporteActual.ocupacionPorTipo).forEach(([tipo, cantidad]) => {
      contenido += `• ${tipo}: ${cantidad}\n`;
    });

    contenido += `\nOCUPACIÓN POR UBICACIÓN:\n`;
    Object.entries(this.reporteActual.ocupacionPorUbicacion).forEach(([ubicacion, cantidad]) => {
      contenido += `• ${ubicacion}: ${cantidad}\n`;
    });

    if (this.reporteDetallado) {
      contenido += `\nDETALLE DE OCUPACIÓN:\n`;
      this.reporteDetallado.detalleOcupacion.forEach((detalle, index) => {
        contenido += `\n${index + 1}. Puesto ${detalle.numeroPuesto} (${detalle.tipoPuesto})\n`;
        contenido += `   Ubicación: ${detalle.ubicacion}\n`;
        contenido += `   Fecha ocupación: ${detalle.fechaOcupacion}\n`;
        if (detalle.cliente) {
          contenido += `   Cliente: ${detalle.cliente.nombre} ${detalle.cliente.apellido}\n`;
          contenido += `   Cédula: ${detalle.cliente.cedula} | Tipo: ${detalle.cliente.tipoPersona}\n`;
        }
      });
    }

    contenido += `\n\nGenerado el: ${new Date().toLocaleString()}`;
    return contenido;
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;
    setTimeout(() => {
      this.mensaje = '';
    }, 5000);
  }

  // Helper methods para la template
  getColorPorcentaje(porcentaje: number): string {
    if (porcentaje < 50) return '#28a745';
    if (porcentaje < 80) return '#ffc107';
    return '#dc3545';
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
}
