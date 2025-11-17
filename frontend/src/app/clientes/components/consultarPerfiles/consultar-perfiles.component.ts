import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Cliente {
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

  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  clienteSeleccionado: Cliente | null = null;

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
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
  }

  // Cargar clientes desde el backend Spring Boot
  private cargarClientes(): void {
    this.cargando = true;

    this.http.get<Cliente[]>('http://localhost:8080/api/clientes/consultar')
      .subscribe({
        next: (clientes) => {
          this.clientes = clientes.map(cliente => ({
            ...cliente,
            fechaRegistro: new Date().toISOString().split('T')[0],
            estado: 'ACTIVO' // Por defecto, puedes modificar según tu lógica
          }));
          this.clientesFiltrados = [...this.clientes];
          this.cargando = false;
          console.log('Clientes cargados:', this.clientes.length);
        },
        error: (error) => {
          console.error('Error al cargar clientes:', error);
          this.cargando = false;
          alert('Error al cargar los datos de clientes. Por favor, intente más tarde.');

          // Cargar datos de ejemplo si el backend falla
          //this.cargarDatosEjemplo();
        }
      });
  }


  /*
  // Datos de ejemplo para pruebas
  private cargarDatosEjemplo(): void {
    this.clientes = [
      {
        id: '1',
        usuario: 'juan.perez',
        contrasena: 'password123',
        confirmarContrasena: 'password123',
        nombre: 'Juan',
        apellido: 'Pérez',
        cedula: 'V-12345678',
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
        contrasena: 'password123',
        confirmarContrasena: 'password123',
        nombre: 'María',
        apellido: 'González',
        cedula: 'E-87654321',
        email: 'maria.gonzalez@gmail.com',
        tipoPersona: 'VISITANTE',
        direccion: 'Calle Secundaria, Valencia',
        telefono: '0414-7654321',
        fechaRegistro: '2024-01-10',
        estado: 'ACTIVO'
      }
    ];
    this.clientesFiltrados = [...this.clientes];
  }
  */

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
  verDetalles(cliente: Cliente): void {
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
}
