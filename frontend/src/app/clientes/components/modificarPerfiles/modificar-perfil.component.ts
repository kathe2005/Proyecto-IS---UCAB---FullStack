
/*
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService, Cliente } from '../../service/cliente.service';

@Component({
  selector: 'app-modificar-perfiles',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modificar-perfil.component.html',
  styleUrls: ['./modificar-perfil.component.css']
})
export class ModificarPerfilesComponent implements OnInit {
  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  clienteSeleccionado: Cliente | null = null;
  clienteEditado: Cliente | null = null;

  // Propiedades para filtros
  filtroTexto: string = '';
  filtroTipo: string = '';

  // Estados de la UI
  mostrarFormulario: boolean = false;
  cargando: boolean = false;
  guardando: boolean = false;

  // Mensajes
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  errorMensaje: string = '';
  mensajeExito: string = '';

  // Opciones para filtros
  tiposPersona = [
    { value: '', label: 'Todos los tipos' },
    { value: 'UCAB', label: 'UCAB' },
    { value: 'VISITANTE', label: 'Visitante' }
  ];

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cargarClientes();
  }

  cargarClientes() {
    this.cargando = true;
    this.clienteService.consultarClientes().subscribe({
      next: (clientes: Cliente[]) => {
        this.clientes = clientes;
        this.clientesFiltrados = clientes;
        this.cargando = false;
      },
      error: (error: any) => {
        console.error('Error cargando clientes:', error);
        this.errorMensaje = 'Error al cargar los clientes';
        this.cargando = false;
      }
    });
  }

  aplicarFiltros() {
    let filtrados = this.clientes;

    // Filtrar por texto (búsqueda en cédula, nombre, apellido)
    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      filtrados = filtrados.filter(cliente =>
        cliente.cedula.toLowerCase().includes(texto) ||
        cliente.nombre.toLowerCase().includes(texto) ||
        cliente.apellido.toLowerCase().includes(texto)
      );
    }

    // Filtrar por tipo
    if (this.filtroTipo) {
      filtrados = filtrados.filter(cliente =>
        cliente.tipoCliente === this.filtroTipo
      );
    }

    this.clientesFiltrados = filtrados;
  }

  limpiarFiltros() {
    this.filtroTexto = '';
    this.filtroTipo = '';
    this.clientesFiltrados = this.clientes;
  }

  seleccionarCliente(cliente: Cliente) {
    this.clienteSeleccionado = cliente;
    this.clienteEditado = { ...cliente };
    this.mostrarFormulario = true;
    this.mensaje = '';
    this.errorMensaje = '';
    this.mensajeExito = '';
  }

  modificarCliente(cliente: Cliente) {
    this.seleccionarCliente(cliente);
  }

  cerrarFormulario() {
    this.mostrarFormulario = false;
    this.clienteSeleccionado = null;
    this.clienteEditado = null;
    this.errorMensaje = '';
    this.mensajeExito = '';
  }

  cancelarSeleccion() {
    this.cerrarFormulario();
  }

  guardarCambios() {
    if (!this.clienteEditado) {
      this.errorMensaje = 'No hay cliente seleccionado para modificar';
      return;
    }

    // Validaciones básicas
    if (!this.clienteEditado.cedula || !this.clienteEditado.nombre || !this.clienteEditado.apellido) {
      this.errorMensaje = 'Por favor complete todos los campos obligatorios';
      return;
    }

    this.guardando = true;

    this.clienteService.modificarCliente(this.clienteEditado).subscribe({
      next: (clienteActualizado: Cliente) => {
        this.guardando = false;
        this.mensajeExito = '✅ Cliente modificado exitosamente';

        // Actualizar la lista local
        const index = this.clientes.findIndex(c => c.id === clienteActualizado.id);
        if (index !== -1) {
          this.clientes[index] = clienteActualizado;
        }

        // Actualizar lista filtrada
        this.aplicarFiltros();

        setTimeout(() => {
          this.cerrarFormulario();
          this.mensajeExito = '';
        }, 2000);
      },
      error: (error: any) => {
        this.guardando = false;
        console.error('Error modificando cliente:', error);

        let mensajeError = 'Error al modificar el cliente';
        if (error.error?.message) {
          mensajeError = error.error.message;
        }

        this.errorMensaje = '❌ ' + mensajeError;
      }
    });
  }

  confirmarModificacion() {
    this.guardarCambios();
  }

  volverAGestion() {
    this.router.navigate(['/gestion-perfiles']);
  }

  volverAGestionPerfiles() {
    this.volverAGestion();
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;
  }
}
*/
