import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Agregar RouterModule
import { PuestoService } from '../../service/puesto.service';
import { Puesto } from '../../models/puestos.model';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-lista-puestos',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule], // Agregar RouterModule aquí
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
    private router: Router,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    this.cargarPuestos();
  }

  cargarPuestos() {
    this.cargando = true;
    console.log('🔄 Intentando cargar puestos...');

    this.puestoService.obtenerTodosLosPuestos().subscribe({
      next: (data: Puesto[]) => {
        console.log('✅ Puestos recibidos del backend:', data);
        console.log('📊 Número de puestos:', data.length);

        if (data.length > 0) {
          console.log('📋 Primer puesto:', {
            id: data[0].id,
            numero: data[0].numero,
            estado: data[0].estadoPuesto,
            tipo: data[0].tipoPuesto
          });
        }

        this.puestos = data;
        this.puestosFiltrados = data;
        this.cargando = false;
      },
      error: (error) => {
        console.error('❌ Error en la petición:', error);
        console.error('📡 Detalles HTTP:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.message
        });
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
    console.log('🚗 ========== OCUPAR PUESTO ANGULAR ==========');
    console.log('📤 ID del puesto:', id);

    const usuario = prompt('Ingrese el nombre de usuario para ocupar el puesto:');

    if (!usuario) {
      console.log('❌ Usuario canceló o no ingresó nombre');
      return;
    }

    console.log('👤 Usuario ingresado:', usuario);

    // ✅ ESTRUCTURA EXACTA que espera el backend
    const requestData = {
      puestoId: id,      // ✅ Nombre exacto: "puestoId" (no "puestoID" ni "idPuesto")
      usuario: usuario   // ✅ Nombre exacto: "usuario" (no "cedula" ni "usuarioOcupante")
    };

    console.log('📦 Datos a enviar:', requestData);
    console.log('🚀 Enviando petición POST a: http://localhost:8080/puestos/api/ocupar');

    this.puestoService.ocuparPuesto(requestData).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del servidor recibida');

        // Manejar diferentes formatos de respuesta
        if (response.id) {
          // Si la respuesta es el puesto actualizado
          console.log('✅ Puesto ocupado exitosamente:', response.id);
          alert(`✅ Puesto ${response.numero} ocupado por ${response.usuarioOcupante}`);
        } else if (response.exito === true) {
          // Si la respuesta tiene estructura {exito: true, mensaje: ...}
          console.log('✅ Operación exitosa:', response.mensaje);
          alert('✅ ' + response.mensaje);
        } else if (response.error) {
          // Si hay error en la respuesta
          console.error('❌ Error del servidor:', response.error);
          alert('❌ ' + response.error);
        }

        // Recargar la lista de puestos
        this.cargarPuestos();
      },
      error: (error) => {
        console.error('❌ ========== ERROR HTTP ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📡 URL:', error.url);
        console.error('📦 Error body:', error.error);

        let mensajeError = 'Error al ocupar el puesto';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        } else if (error.status === 0) {
          mensajeError = 'No se pudo conectar con el servidor';
        } else if (error.status === 404) {
          mensajeError = 'Endpoint no encontrado. Verifica que el backend esté corriendo';
        }

        alert('❌ ' + mensajeError);
      }
    });
  }

  liberarPuesto(id: string) {
    console.log('🔄 ========== LIBERAR PUESTO ANGULAR ==========');
    console.log('📤 ID del puesto:', id);
    console.log('🌐 URL destino:', `http://localhost:8080/puestos/api/liberar/${id}`);
    console.log('⏰ Timestamp:', new Date().toISOString());

    if (!confirm('¿Está seguro de liberar este puesto?')) {
      console.log('❌ Usuario canceló la acción');
      return;
    }

    console.log('🚀 Enviando petición POST...');
    this.puestoService.liberarPuesto(id).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del servidor recibida');
        console.log('📦 Response completo:', response);
        console.log('🔑 Keys del response:', Object.keys(response));
        console.log('📝 Mensaje:', response.mensaje || response.error);
        console.log('✅ Éxito:', response.exito);

        if (response.exito === true || response.mensaje) {
          const mensaje = response.mensaje || 'Puesto liberado exitosamente';
          console.log('✅ ' + mensaje);
          alert('✅ ' + mensaje);
        } else if (response.error) {
          console.error('❌ Error del servidor:', response.error);
          alert('❌ ' + response.error);
        }

        console.log('🔄 Recargando lista de puestos...');
        this.cargarPuestos();
      },
      error: (error) => {
        console.error('❌ ========== ERROR HTTP ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📡 URL:', error.url);
        console.error('📦 Error completo:', error);
        console.error('📦 Error body:', error.error);

        let mensajeError = 'Error al liberar el puesto';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        } else if (error.status === 0) {
          mensajeError = 'No se pudo conectar con el servidor';
        } else if (error.status === 404) {
          mensajeError = 'Endpoint no encontrado';
        }

        alert('❌ ' + mensajeError);
      },
      complete: () => {
        console.log('✅ Petición completada');
      }
    });
  }

  bloquearPuesto(id: string) {
    console.log('🔒 ========== BLOQUEAR PUESTO ANGULAR ==========');
    console.log('📤 ID del puesto:', id);
    console.log('🌐 URL destino:', `http://localhost:8080/puestos/api/bloquear/${id}`);
    console.log('⏰ Timestamp:', new Date().toISOString());

    if (!confirm('¿Está seguro de bloquear este puesto?')) {
      console.log('❌ Usuario canceló la acción');
      return;
    }

    console.log('🚀 Enviando petición POST...');
    this.puestoService.bloquearPuesto(id).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del servidor recibida');
        console.log('📦 Response completo:', response);
        console.log('🔑 Keys del response:', Object.keys(response));
        console.log('📝 Mensaje:', response.mensaje || response.error);
        console.log('✅ Éxito:', response.exito);

        if (response.exito === true || response.mensaje) {
          const mensaje = response.mensaje || 'Puesto bloqueado exitosamente';
          console.log('✅ ' + mensaje);
          alert('✅ ' + mensaje);
        } else if (response.error) {
          console.error('❌ Error del servidor:', response.error);
          alert('❌ ' + response.error);
        }

        console.log('🔄 Recargando lista de puestos...');
        this.cargarPuestos();
      },
      error: (error) => {
        console.error('❌ ========== ERROR HTTP ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📡 URL:', error.url);
        console.error('📦 Error completo:', error);
        console.error('📦 Error body:', error.error);

        let mensajeError = 'Error al bloquear el puesto';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        } else if (error.status === 0) {
          mensajeError = 'No se pudo conectar con el servidor';
        }

        alert('❌ ' + mensajeError);
      }
    });
  }

  desbloquearPuesto(id: string) {
    console.log('🔓 ========== DESBLOQUEAR PUESTO ANGULAR ==========');
    console.log('📤 ID del puesto:', id);
    console.log('🌐 URL destino:', `http://localhost:8080/puestos/api/desbloquear/${id}`);
    console.log('⏰ Timestamp:', new Date().toISOString());

    console.log('🚀 Enviando petición POST...');
    this.puestoService.desbloquearPuesto(id).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del servidor recibida');
        console.log('📦 Response completo:', response);
        console.log('🔑 Keys del response:', Object.keys(response));
        console.log('📝 Mensaje:', response.mensaje || response.error);
        console.log('✅ Éxito:', response.exito);

        if (response.exito === true || response.mensaje) {
          const mensaje = response.mensaje || 'Puesto desbloqueado exitosamente';
          console.log('✅ ' + mensaje);
          alert('✅ ' + mensaje);
        } else if (response.error) {
          console.error('❌ Error del servidor:', response.error);
          alert('❌ ' + response.error);
        }

        console.log('🔄 Recargando lista de puestos...');
        this.cargarPuestos();
      },
      error: (error) => {
        console.error('❌ ========== ERROR HTTP ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📡 URL:', error.url);
        console.error('📦 Error completo:', error);
        console.error('📦 Error body:', error.error);

        let mensajeError = 'Error al desbloquear el puesto';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        }

        alert('❌ ' + mensajeError);
      }
    });
  }

  ponerEnMantenimiento(id: string) {
    console.log('🔧 ========== MANTENIMIENTO PUESTO ANGULAR ==========');
    console.log('📤 ID del puesto:', id);
    console.log('🌐 URL destino:', `http://localhost:8080/puestos/api/mantenimiento/${id}`);
    console.log('⏰ Timestamp:', new Date().toISOString());

    if (!confirm('¿Está seguro de poner este puesto en mantenimiento?')) {
      console.log('❌ Usuario canceló la acción');
      return;
    }

    console.log('🚀 Enviando petición POST...');
    this.puestoService.ponerEnMantenimiento(id).subscribe({
      next: (response: any) => {
        console.log('✅ Respuesta del servidor recibida');
        console.log('📦 Response completo:', response);
        console.log('🔑 Keys del response:', Object.keys(response));
        console.log('📝 Mensaje:', response.mensaje || response.error);
        console.log('✅ Éxito:', response.exito);

        if (response.exito === true || response.mensaje) {
          const mensaje = response.mensaje || 'Puesto puesto en mantenimiento exitosamente';
          console.log('✅ ' + mensaje);
          alert('✅ ' + mensaje);
        } else if (response.error) {
          console.error('❌ Error del servidor:', response.error);
          alert('❌ ' + response.error);
        }

        console.log('🔄 Recargando lista de puestos...');
        this.cargarPuestos();
      },
      error: (error) => {
        console.error('❌ ========== ERROR HTTP ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📡 URL:', error.url);
        console.error('📦 Error completo:', error);
        console.error('📦 Error body:', error.error);

        let mensajeError = 'Error al poner en mantenimiento';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.message) {
          mensajeError = error.message;
        }

        alert('❌ ' + mensajeError);
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
