import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { Cliente } from '../../models/cliente';

@Component({
  selector: 'app-modificar-perfiles',
  templateUrl: './modificar-perfil.component.html',
  styleUrls: ['./modificar-perfil.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ModificarPerfilesComponent implements OnInit {

  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  clienteSeleccionado: Cliente | null = null;

  // Filtros de búsqueda
  filtroTexto: string = '';
  filtroTipo: string = 'todos';

  // Estados de carga
  cargando: boolean = false;
  guardando: boolean = false;
  mostrarFormulario: boolean = false;

  // Mensajes
  errorMensaje: string | null = null;
  mensajeExito: string | null = null;

  // Opciones para filtros
  tiposPersona = [
    { value: 'todos', label: 'Todos los tipos' },
    { value: 'UCAB', label: 'UCAB' },
    { value: 'VISITANTE', label: 'VISITANTE' }
  ];

  constructor(
    private router: Router,
    private clienteService: ClienteService
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
  }

  // Cargar clientes desde el backend
  private cargarClientes(): void {
    this.cargando = true;
    this.errorMensaje = null;

    this.clienteService.consultarClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.clientesFiltrados = [...this.clientes];
        this.cargando = false;
        console.log('Clientes cargados:', this.clientes.length);
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
        this.errorMensaje = 'Error al cargar los datos de clientes. Por favor, intente más tarde.';
        this.cargando = false;
      }
    });
  }

  // Aplicar filtros de búsqueda
  aplicarFiltros(): void {
    this.clientesFiltrados = this.clientes.filter(cliente => {
      const coincideTexto = !this.filtroTexto ||
        cliente.nombre.toLowerCase().includes(this.filtroTexto.toLowerCase()) ||
        cliente.apellido.toLowerCase().includes(this.filtroTexto.toLowerCase()) ||
        cliente.cedula.includes(this.filtroTexto) ||
        cliente.email.toLowerCase().includes(this.filtroTexto.toLowerCase()) ||
        cliente.usuario.toLowerCase().includes(this.filtroTexto.toLowerCase());

      const coincideTipo = this.filtroTipo === 'todos' || cliente.tipoPersona === this.filtroTipo;

      return coincideTexto && coincideTipo;
    });
  }

  // Limpiar todos los filtros
  limpiarFiltros(): void {
    this.filtroTexto = '';
    this.filtroTipo = 'todos';
    this.clientesFiltrados = [...this.clientes];
  }

  // Modificar un cliente
  modificarCliente(cliente: Cliente): void {
    this.clienteSeleccionado = { ...cliente };
    this.mostrarFormulario = true;
    this.errorMensaje = null;
    this.mensajeExito = null;
  }

  // Cerrar panel de formulario
  cerrarFormulario(): void {
    this.mostrarFormulario = false;
    this.clienteSeleccionado = null;
    this.errorMensaje = null;
    this.mensajeExito = null;
  }

  // Guardar cambios del cliente
  guardarCambios(): void {
    if (!this.clienteSeleccionado || !this.clienteSeleccionado.id) {
      this.errorMensaje = 'Error: Cliente no válido';
      return;
    }

    this.guardando = true;
    this.errorMensaje = null;
    this.mensajeExito = null;

    this.clienteService.modificarCliente(this.clienteSeleccionado.id, this.clienteSeleccionado)
      .subscribe({
        next: (clienteActualizado) => {
          this.guardando = false;
          this.mensajeExito = 'Cliente modificado exitosamente';

          // Actualizar la lista local
          const index = this.clientes.findIndex(c => c.id === clienteActualizado.id);
          if (index !== -1) {
            this.clientes[index] = clienteActualizado;
            this.clientesFiltrados = [...this.clientes];
          }

          // Cerrar el formulario después de 2 segundos
          setTimeout(() => {
            this.cerrarFormulario();
          }, 2000);
        },
        error: (error) => {
          this.guardando = false;
          console.error('Error al modificar cliente:', error);

          if (error.error && error.error.mensaje) {
            this.errorMensaje = error.error.mensaje;
          } else {
            this.errorMensaje = 'Error al modificar el cliente. Por favor, intente nuevamente.';
          }
        }
      });
  }

  // Volver a gestión de perfiles
  volverAGestion(): void {
    this.router.navigate(['/gestion-perfiles']);
  }
}
