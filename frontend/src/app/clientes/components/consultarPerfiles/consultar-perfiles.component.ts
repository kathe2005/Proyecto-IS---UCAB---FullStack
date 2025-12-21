import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ClienteService, Cliente } from '../../service/cliente.service'; // ✅ Importar el servicio

interface ClienteCompleto {
  id: string;
  usuario: string;
  contrasena: string;
  confirmarContrasena: string;
  nombre: string;
  apellido: string;
  cedula: string;
  email: string;
  tipoPersona: string;
  direccion: string;
  telefono: string;
  fechaRegistro: string;
  estado: string;
}

@Component({
  selector: 'app-consultar-perfiles',
  templateUrl: './consultar-perfiles.component.html',
  styleUrls: ['./consultar-perfiles.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class ConsultarPerfilesComponent implements OnInit {
  clientes: ClienteCompleto[] = [];
  clientesFiltrados: ClienteCompleto[] = [];
  clienteSeleccionado: ClienteCompleto | null = null;

  // Filtros de búsqueda
  filtroTexto: string = '';
  filtroTipo: string = 'todos';
  filtroEstado: string = 'todos';

  // Estados de carga
  cargando: boolean = false;
  mostrarDetalles: boolean = false;

  // Opciones para filtros
  tiposPersona = [
    { value: 'todos', label: 'Todos los tipos' },
    { value: 'UCAB', label: 'UCAB' },
    { value: 'VISITANTE', label: 'VISITANTE' }
  ];

  estadosCliente = [
    { value: 'todos', label: 'Todos los estados' },
    { value: 'ACTIVO', label: 'Activo' },
    { value: 'INACTIVO', label: 'Inactivo' },
    { value: 'SUSPENDIDO', label: 'Suspendido' }
  ];

  constructor(
    private router: Router,
    private clienteService: ClienteService // ✅ Usar el servicio corregido
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
  }

  // Cargar clientes desde el backend Spring Boot - CORREGIDO
  private cargarClientes(): void {
    this.cargando = true;

    console.log('📥 Cargando clientes desde el backend...');

    // ✅ URL CORRECTA: http://localhost:8080/clientes/api
    this.clienteService.consultarClientes().subscribe({
      next: (clientes: Cliente[]) => {
        console.log('✅ Clientes recibidos del backend:', clientes);

        // Mapear los clientes del backend a nuestro formato
        this.clientes = clientes.map(cliente => ({
          ...cliente,
          id: cliente.id || 'sin-id',
          confirmarContrasena: cliente.confirmarContrasena || '',
          fechaRegistro: cliente.fechaRegistro || new Date().toISOString().split('T')[0],
          estado: cliente.estado || 'ACTIVO',
          direccion: cliente.direccion || 'No especificada',
          telefono: cliente.telefono || 'No especificado'
        })) as ClienteCompleto[];

        this.clientesFiltrados = [...this.clientes];
        this.cargando = false;
        console.log('✅ Clientes procesados:', this.clientes.length);
      },
      error: (error) => {
        console.error('❌ Error al cargar clientes:', error);
        this.cargando = false;

        // Mostrar mensaje más específico
        if (error.status === 0) {
          alert('Error de conexión: No se pudo conectar al servidor. Verifica que el backend esté ejecutándose.');
        } else if (error.status === 404) {
          alert('Endpoint no encontrado. Verifica la URL del servicio.');
        } else {
          alert('Error al cargar los datos de clientes: ' + (error.message || 'Error desconocido'));
        }

        // Cargar datos de ejemplo si el backend falla
        this.cargarDatosEjemplo();
      }
    });
  }

  // Datos de ejemplo para pruebas cuando el backend no está disponible
  private cargarDatosEjemplo(): void {
    console.log('Cargando datos de ejemplo...');
    this.clientes = [
      {
        id: '1',
        usuario: 'juan.perez',
        contrasena: 'Password123',
        confirmarContrasena: 'Password123',
        nombre: 'Juan',
        apellido: 'Pérez',
        cedula: '12345678',
        email: 'juan.perez@ucab.edu.ve',
        tipoPersona: 'UCAB',
        direccion: 'Av. Principal, Caracas',
        telefono: '0412-1234567',
        fechaRegistro: '2024-01-15',
        estado: 'ACTIVO'
      },
      {
        id: '2',
        usuario: 'maria.gonzalez',
        contrasena: 'Password123',
        confirmarContrasena: 'Password123',
        nombre: 'María',
        apellido: 'González',
        cedula: '87654321',
        email: 'maria.gonzalez@gmail.com',
        tipoPersona: 'VISITANTE',
        direccion: 'Calle Secundaria, Valencia',
        telefono: '0414-7654321',
        fechaRegistro: '2024-01-10',
        estado: 'ACTIVO'
      },
      {
        id: '3',
        usuario: 'carlos.lopez',
        contrasena: 'SecurePass123',
        confirmarContrasena: 'SecurePass123',
        nombre: 'Carlos',
        apellido: 'López',
        cedula: '11223344',
        email: 'carlos.lopez@est.ucab.edu.ve',
        tipoPersona: 'UCAB',
        direccion: 'Urbanización Los Chaguaramos',
        telefono: '0416-4455667',
        fechaRegistro: '2024-01-20',
        estado: 'ACTIVO'
      }
    ];
    this.clientesFiltrados = [...this.clientes];
    console.log('Datos de ejemplo cargados:', this.clientes.length, 'clientes');
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
      const coincideEstado = this.filtroEstado === 'todos' || cliente.estado === this.filtroEstado;

      return coincideTexto && coincideTipo && coincideEstado;
    });
  }

  // Limpiar todos los filtros
  limpiarFiltros(): void {
    this.filtroTexto = '';
    this.filtroTipo = 'todos';
    this.filtroEstado = 'todos';
    this.clientesFiltrados = [...this.clientes];
  }

  // Ver detalles de un cliente
  verDetalles(cliente: ClienteCompleto): void {
    this.clienteSeleccionado = cliente;
    this.mostrarDetalles = true;
  }

  // Cerrar panel de detalles
  cerrarDetalles(): void {
    this.mostrarDetalles = false;
    this.clienteSeleccionado = null;
  }

  // Exportar datos
  exportarDatos(): void {
    console.log('Exportando datos de clientes...');

    const headers = ['ID', 'Nombre', 'Apellido', 'Cédula', 'Email', 'Usuario', 'Tipo', 'Dirección', 'Teléfono', 'Fecha Registro', 'Estado'];
    const csvData = this.clientesFiltrados.map(cliente => [
      cliente.id,
      cliente.nombre,
      cliente.apellido,
      cliente.cedula,
      cliente.email,
      cliente.usuario,
      cliente.tipoPersona,
      cliente.direccion,
      cliente.telefono,
      cliente.fechaRegistro,
      cliente.estado
    ]);

    const csvContent = [headers, ...csvData]
      .map(row => row.map(field => `"${field}"`).join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `clientes_${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);

    alert(`Datos exportados exitosamente. Se exportaron ${this.clientesFiltrados.length} clientes.`);
  }

  // Volver a gestión de perfiles
  volverAGestion(): void {
    this.router.navigate(['/gestion-perfiles']);
  }

  // Obtener clase CSS para el estado
  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'ACTIVO': return 'estado-activo';
      case 'INACTIVO': return 'estado-inactivo';
      case 'SUSPENDIDO': return 'estado-suspendido';
      default: return 'estado-desconocido';
    }
  }

  // Actualizar lista de clientes
  actualizarLista(): void {
    this.cargarClientes();
  }
}
