import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClienteService } from '../service/cliente.service';
import { FormsModule } from '@angular/forms';

// Interface temporal
interface Cliente {
  id?: string;
  usuario: string;
  contrasena: string;
  confirmarContrasena: string;
  nombre: string;
  apellido: string;
  cedula: string;
  email: string;
  tipoPersona: string
  direccion: string;
  telefono: string;
}

@Component({
  selector: 'app-registro-cliente',
  templateUrl: './registro-cliente.component.html',
  styleUrls: ['./registro-cliente.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class RegistroClienteComponent {
  nuevoCliente: Cliente = {
    usuario: '',
    contrasena: '',
    confirmarContrasena: '',
    nombre: '',
    apellido: '',
    cedula: '',
    email: '',
    tipoPersona: '',
    direccion: '',
    telefono: '',
  };

  errorMensaje: string | null = null;
  isProcessing: boolean = false;
  clienteConfirmado: Cliente | null = null;
  paso: 'credenciales' | 'datos_personales' | 'contacto_ubicacion' | 'cargando' | 'confirmacion' = 'credenciales';

  // Opciones para el select de tipo de persona
  tiposPersona = [
    { value: 'UCAB', label: 'UCAB' },
    { value: 'VISITANTE', label: 'VISITANTE' }
  ];

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.nuevoCliente.tipoPersona = '';
  }

  siguientePaso() {
    this.errorMensaje = null;

    if (this.paso === 'credenciales') {
      // Validaciones del paso 1
      if (!this.nuevoCliente.usuario || !this.nuevoCliente.email) {
        this.errorMensaje = 'Usuario y email son obligatorios';
        return;
      }

      if (this.nuevoCliente.usuario.length < 4) {
        this.errorMensaje = 'El usuario debe tener al menos 4 caracteres';
        return;
      }

      if (!this.validarEmail(this.nuevoCliente.email)) {
        this.errorMensaje = 'El formato del email no es válido';
        return;
      }

      if (this.nuevoCliente.contrasena.length < 6) {
        this.errorMensaje = 'La contraseña debe tener al menos 6 caracteres';
        return;
      }

      if (this.nuevoCliente.contrasena !== this.nuevoCliente.confirmarContrasena) {
        this.errorMensaje = 'Las contraseñas no coinciden. Por favor revíselas';
        return;
      }

      this.paso = 'datos_personales';
    }
    else if (this.paso === 'datos_personales') {
      // Validaciones del paso 2
      if (!this.nuevoCliente.tipoPersona) {
        this.errorMensaje = 'Debe seleccionar si es UCAB o VISITANTE';
        return;
      }

      if (!this.nuevoCliente.cedula || !this.nuevoCliente.nombre || !this.nuevoCliente.apellido) {
        this.errorMensaje = 'Debe completar la cédula, el nombre y el apellido para continuar';
        return;
      }

      if (!this.validarCedula(this.nuevoCliente.cedula)) {
        this.errorMensaje = 'La cédula debe contener solo números';
        return;
      }

      if (this.nuevoCliente.nombre.length < 2 || this.nuevoCliente.apellido.length < 2) {
        this.errorMensaje = 'Nombre y apellido deben tener al menos 2 caracteres';
        return;
      }

      this.paso = 'contacto_ubicacion';
    }
    else if (this.paso === 'contacto_ubicacion') {
      this.onSubmit();
    }
  }

  pasoAnterior() {
    this.errorMensaje = null;

    if (this.paso === 'datos_personales') {
      this.paso = 'credenciales';
    }
    else if (this.paso === 'contacto_ubicacion') {
      this.paso = 'datos_personales';
    }
    else if (this.paso === 'confirmacion') {
      this.paso = 'contacto_ubicacion';
    }
  }

  onSubmit() {
    this.errorMensaje = null;

    // Validación final del paso 3
    if (!this.nuevoCliente.direccion || !this.nuevoCliente.telefono) {
      this.errorMensaje = 'Dirección y teléfono son obligatorios';
      return;
    }

    if (!this.validarTelefono(this.nuevoCliente.telefono)) {
      this.errorMensaje = 'El formato del teléfono no es válido';
      return;
    }

    this.isProcessing = true;
    this.paso = 'cargando';

    // Simulación de registro - reemplaza con servicio real
    setTimeout(() => {
      // Generar ID simulado para el cliente
      const clienteId = 'CLI-' + Math.random().toString(36).substr(2, 9).toUpperCase();

      this.clienteConfirmado = {
        ...this.nuevoCliente,
        id: clienteId
      };

      this.paso = 'confirmacion';
      this.isProcessing = false;

      // Mostrar mensaje de éxito
      console.log('Cliente registrado exitosamente:', this.clienteConfirmado);
    }, 2000);

    // Descomenta para usar el servicio real:
    /*
    this.clienteService.registrarCliente(this.nuevoCliente)
      .subscribe({
        next: (clienteRespuesta) => {
          this.clienteConfirmado = clienteRespuesta;
          this.paso = 'confirmacion';
          this.isProcessing = false;
          console.log('Cliente registrado exitosamente:', clienteRespuesta);
        },
        error: (err) => {
          console.error('Error de registro: ', err);
          this.errorMensaje = err.error?.mensaje || "Hubo un error desconocido al registrar";
          this.paso = 'contacto_ubicacion';
          this.isProcessing = false;
        }
      });
    */
  }

  modificarDatos() {
    this.paso = 'contacto_ubicacion';
    this.errorMensaje = null;
  }

  finalizarRegistro() {
    console.log("Registro de cliente confirmado y finalizado");

    // Mostrar mensaje de éxito
    alert('✅ Registro exitoso! Serás redirigido al sistema de estacionamiento.');

    // Redirigir al dashboard del sistema
    setTimeout(() => {
      this.router.navigate(['/inicio']);
    }, 1500);
  }

  // Métodos de validación
  private validarEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private validarCedula(cedula: string): boolean {
    // Validar que la cédula contenga solo números
    return /^\d+$/.test(cedula);
  }

  private validarTelefono(telefono: string): boolean {
    // Validar formato de teléfono (mínimo 10 dígitos, puede contener +, espacios, guiones)
    const telefonoRegex = /^[\+]?[(]?[\d\s\-\(\)]{10,}$/;
    return telefonoRegex.test(telefono.replace(/\s/g, ''));
  }

  // Método para reiniciar el formulario
  registrarNuevoCliente() {
    this.nuevoCliente = {
      usuario: '',
      contrasena: '',
      confirmarContrasena: '',
      nombre: '',
      apellido: '',
      cedula: '',
      email: '',
      tipoPersona: '',
      direccion: '',
      telefono: '',
    };
    this.clienteConfirmado = null;
    this.paso = 'credenciales';
    this.errorMensaje = null;
  }

  // Método para obtener el progreso del formulario
  getProgreso(): number {
    const pasos = ['credenciales', 'datos_personales', 'contacto_ubicacion', 'confirmacion'];
    const pasoActual = pasos.indexOf(this.paso);
    return ((pasoActual + 1) / pasos.length) * 100;
  }

  // Método para obtener el texto del paso actual
  getTextoPasoActual(): string {
    switch (this.paso) {
      case 'credenciales':
        return 'Paso 1 de 3: Credenciales de Acceso';
      case 'datos_personales':
        return 'Paso 2 de 3: Datos Personales';
      case 'contacto_ubicacion':
        return 'Paso 3 de 3: Contacto y Ubicación';
      case 'cargando':
        return 'Procesando Registro';
      case 'confirmacion':
        return 'Confirmación de Registro';
      default:
        return 'Registro de Cliente';
    }
  }
}
