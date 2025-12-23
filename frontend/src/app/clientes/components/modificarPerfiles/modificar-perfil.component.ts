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
  clienteEditado: any = {};

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
        console.log('Clientes cargados:', clientes);
        this.clientes = clientes;
        this.clientesFiltrados = [...clientes];
        this.cargando = false;
      },
      error: (error: any) => {
        console.error('Error cargando clientes:', error);
        this.errorMensaje = 'Error al cargar los clientes: ' + error.message;
        this.mensajeTipo = 'danger';
        this.mensaje = this.errorMensaje;
        this.cargando = false;
      }
    });
  }

  aplicarFiltros() {
    let filtrados = this.clientes;

    // Filtrar por texto (búsqueda en cédula, nombre, apellido, usuario)
    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      filtrados = filtrados.filter(cliente =>
        (cliente.cedula?.toLowerCase().includes(texto) || false) ||
        (cliente.nombre?.toLowerCase().includes(texto) || false) ||
        (cliente.apellido?.toLowerCase().includes(texto) || false) ||
        (cliente.usuario?.toLowerCase().includes(texto) || false) ||
        (cliente.email?.toLowerCase().includes(texto) || false)
      );
    }

    // Filtrar por tipo
    if (this.filtroTipo) {
      filtrados = filtrados.filter(cliente =>
        cliente.tipoPersona === this.filtroTipo
      );
    }

    this.clientesFiltrados = filtrados;
  }

  limpiarFiltros() {
    this.filtroTexto = '';
    this.filtroTipo = '';
    this.clientesFiltrados = [...this.clientes];
  }

  seleccionarCliente(cliente: Cliente) {
    console.log('Cliente seleccionado:', cliente);
    this.clienteSeleccionado = cliente;

    // Crear copia del cliente para editar
    this.clienteEditado = {
      id: cliente.id,
      usuario: cliente.usuario,
      cedula: cliente.cedula,
      nombre: cliente.nombre,
      apellido: cliente.apellido,
      email: cliente.email,
      telefono: cliente.telefono || '',
      tipoPersona: cliente.tipoPersona || 'VISITANTE',
      direccion: cliente.direccion || '',
      contrasena: '', // Dejar vacío para no cambiar
      confirmarContrasena: ''
    };

    this.mostrarFormulario = true;
    this.mensaje = '';
    this.errorMensaje = '';
    this.mensajeExito = '';
  }

  modificarCliente(cliente: Cliente) {
    this.seleccionarCliente(cliente);
    // Abrir modal programáticamente (necesitamos agregar el modal al HTML)
    this.mostrarFormulario = true;
  }

  cerrarFormulario() {
    this.mostrarFormulario = false;
    this.clienteSeleccionado = null;
    this.clienteEditado = {};
    this.errorMensaje = '';
    this.mensajeExito = '';
  }

  cancelarSeleccion() {
    this.cerrarFormulario();
  }

  guardarCambios() {
    if (!this.clienteEditado || !this.clienteEditado.id) {
      this.errorMensaje = 'No hay cliente seleccionado para modificar';
      this.mensajeTipo = 'danger';
      this.mensaje = this.errorMensaje;
      return;
    }

    // Validaciones básicas
    if (!this.clienteEditado.cedula || !this.clienteEditado.nombre ||
        !this.clienteEditado.apellido || !this.clienteEditado.email) {
      this.errorMensaje = 'Por favor complete todos los campos obligatorios';
      this.mensajeTipo = 'warning';
      this.mensaje = this.errorMensaje;
      return;
    }

    // Validar formato de email
    if (!this.clienteEditado.email.includes('@')) {
      this.errorMensaje = 'El email debe tener un formato válido (ejemplo@dominio.com)';
      this.mensajeTipo = 'warning';
      this.mensaje = this.errorMensaje;
      return;
    }

    // Validar contraseña si se quiere cambiar
    if (this.clienteEditado.contrasena) {
      if (this.clienteEditado.contrasena.length < 8) {
        this.errorMensaje = 'La contraseña debe tener al menos 8 caracteres';
        this.mensajeTipo = 'warning';
        this.mensaje = this.errorMensaje;
        return;
      }
      if (this.clienteEditado.contrasena !== this.clienteEditado.confirmarContrasena) {
        this.errorMensaje = 'Las contraseñas no coinciden';
        this.mensajeTipo = 'warning';
        this.mensaje = this.errorMensaje;
        return;
      }
    } else {
      // Si no se quiere cambiar la contraseña, eliminarla del objeto
      delete this.clienteEditado.contrasena;
      delete this.clienteEditado.confirmarContrasena;
    }

    this.guardando = true;
    this.errorMensaje = '';

    console.log('Enviando datos para modificar:', this.clienteEditado);

    // Usar el método modificarCliente
    this.clienteService.modificarCliente(this.clienteEditado).subscribe({
      next: (clienteActualizado: Cliente) => {
        console.log('Cliente modificado exitosamente:', clienteActualizado);
        this.guardando = false;
        this.mensajeExito = '✅ Cliente modificado exitosamente';
        this.mensajeTipo = 'success';
        this.mensaje = this.mensajeExito;

        // Actualizar la lista local
        const index = this.clientes.findIndex(c => c.id === clienteActualizado.id);
        if (index !== -1) {
          this.clientes[index] = clienteActualizado;
        }

        // Actualizar lista filtrada
        this.aplicarFiltros();

        // Cerrar formulario después de 2 segundos
        setTimeout(() => {
          this.cerrarFormulario();
          this.mensajeExito = '';
          this.mensaje = 'Cliente actualizado correctamente';
        }, 2000);
      },
      error: (error: any) => {
        this.guardando = false;
        console.error('Error modificando cliente:', error);

        let mensajeError = 'Error al modificar el cliente';
        if (error.error?.error) {
          mensajeError = error.error.error;
        } else if (error.error?.message) {
          mensajeError = error.error.message;
        } else if (error.message) {
          mensajeError = error.message;
        }

        this.errorMensaje = '❌ ' + mensajeError;
        this.mensajeTipo = 'danger';
        this.mensaje = this.errorMensaje;
      }
    });
  }

  confirmarModificacion() {
    if (confirm('¿Está seguro de que desea guardar los cambios?')) {
      this.guardarCambios();
    }
  }

  volverAGestion() {
    this.router.navigate(['/gestion-perfiles']);
  }

  volverAGestionPerfiles() {
    this.volverAGestion();
  }
}
