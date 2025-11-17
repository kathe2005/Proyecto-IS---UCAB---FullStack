import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../models/cliente';

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

      // Validación de cédula CORREGIDA - formato V-/E-
      if (!this.validarCedula(this.nuevoCliente.cedula)) {
        this.errorMensaje = 'La cédula debe tener formato V-12345678 o E-12345678 (V o E seguido de guion y 6-8 dígitos)';
        return;
      }

      if (this.nuevoCliente.nombre.length < 2 || this.nuevoCliente.apellido.length < 2) {
        this.errorMensaje = 'Nombre y apellido deben tener al menos 2 caracteres';
        return;
      }

      // Validar email según tipo de persona
      const errorEmail = this.validarEmailPorTipoPersona(this.nuevoCliente.tipoPersona, this.nuevoCliente.email);
      if (errorEmail) {
        this.errorMensaje = errorEmail;
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
      this.errorMensaje = 'El formato del teléfono no es válido. Debe ser: 0412-6112225';
      return;
    }

    this.isProcessing = true;
    this.paso = 'cargando';

    // USAR EL SERVICIO REAL
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

  // Métodos de validación CORREGIDOS para formato V-/E-
  private validarEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private validarCedula(cedula: string): boolean {
    // Validar formato V-12345678 o E-12345678 (6-8 dígitos)
    const cedulaRegex = /^[VEve]-\d{6,8}$/;
    return cedulaRegex.test(cedula);
  }

  private validarTelefono(telefono: string): boolean {
    // Validar formato de teléfono: 0412-6112225
    const telefonoRegex = /^(0212|0424|0416|0426|0414|0412)-\d{7}$/;
    return telefonoRegex.test(telefono);
  }

  private validarEmailPorTipoPersona(tipoPersona: string, email: string): string | null {
    if (!tipoPersona || !email) return null;

    const dominio = email.substring(email.lastIndexOf("@") + 1).toLowerCase();

    if (tipoPersona === 'UCAB') {
      if (dominio !== 'ucab.edu.ve' && dominio !== 'est.ucab.edu.ve') {
        return 'Para el tipo UCAB, el email debe ser @ucab.edu.ve o @est.ucab.edu.ve';
      }
    } else if (tipoPersona === 'VISITANTE') {
      const dominiosPermitidos = ['gmail.com', 'outlook.com', 'yahoo.com', 'hotmail.com'];
      if (!dominiosPermitidos.includes(dominio)) {
        return 'Para el tipo VISITANTE, el email debe ser @gmail.com, @outlook.com, @yahoo.com o @hotmail.com';
      }
    }

    return null;
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
