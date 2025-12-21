import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService, Cliente } from '../../service/cliente.service';

interface ErroresFormulario {
  cedula: string;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  tipoPersona: string;
  usuario: string;
  contrasena: string;
  confirmarContrasena: string;
  direccion: string;
}

@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro-cliente.component.html',
  styleUrls: ['./registro-cliente.component.css']
})
export class RegistroClienteComponent {
  nuevoCliente: Cliente = {
    usuario: '',
    contrasena: '',
    confirmarContrasena: '',
    cedula: '',
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    tipoPersona: 'UCAB',
    direccion: ''
  };

  // Mensajes
  mensaje: string = '';
  mensajeTipo: 'success' | 'danger' | 'warning' | 'info' = 'info';
  procesando: boolean = false;

  // Errores específicos por campo
  errores: ErroresFormulario = {
    cedula: '',
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    tipoPersona: '',
    usuario: '',
    contrasena: '',
    confirmarContrasena: '',
    direccion: ''
  };

  // Formatos aceptados
  formatosCedula = [
    '12345678',
    'V-12345678',
    'E12345678',
    'J-12345678'
  ];

  formatosTelefono = [
    '0212-1234567',
    '0412-1234567',
    '0414-1234567',
    '0416-1234567',
    '0424-1234567',
    '0426-1234567'
  ];

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  registrarCliente(form?: NgForm) {
    // Limpiar errores anteriores
    this.limpiarErrores();

    console.log('📝 ========== INTENTANDO REGISTRAR CLIENTE ==========');
    console.log('📦 Datos del cliente:', this.nuevoCliente);

    // Validar todos los campos
    if (!this.validarFormulario()) {
      console.log('❌ Formulario no válido');
      this.mostrarMensaje('Por favor corrija los errores en el formulario', 'warning');
      return;
    }

    // ✅ VALIDACIONES ESPECÍFICAS PARA EL BACKEND

    // 1. Validar email según tipo de persona
    if (!this.validarEmailPorTipoPersona()) {
      return;
    }

    // 2. Validar formato de teléfono para backend
    if (this.nuevoCliente.telefono && !this.validarFormatoTelefonoBackend(this.nuevoCliente.telefono)) {
      this.errores.telefono = 'Formato incorrecto. Debe ser: 0412-1234567';
      this.mostrarMensaje('Formato de teléfono incorrecto', 'warning');
      return;
    }

    // 3. Validar contraseña para backend
    if (!this.validarFormatoContrasenaBackend(this.nuevoCliente.contrasena)) {
      this.errores.contrasena = 'Mínimo 8 caracteres, una mayúscula y un número';
      this.mostrarMensaje('La contraseña no cumple los requisitos de seguridad', 'warning');
      return;
    }

    console.log('✅ Validaciones pasadas, enviando al backend...');

    this.procesando = true;

    this.clienteService.registrarCliente(this.nuevoCliente).subscribe({
      next: (response: any) => {
        console.log('✅ ========== RESPUESTA DEL BACKEND ==========');
        console.log('📦 Response completo:', response);

        this.procesando = false;

        if (response.id || response.usuario) {
          // Éxito - cliente creado
          console.log('✅ Cliente registrado exitosamente:', response.usuario);
          this.mostrarMensaje('✅ Cliente registrado exitosamente', 'success');

          // Limpiar el formulario
          this.limpiarFormulario();

          setTimeout(() => {
            this.router.navigate(['/gestion-perfiles']);
          }, 2000);
        } else if (response.mensaje) {
          // Mensaje del backend
          this.mostrarMensaje('✅ ' + response.mensaje, 'success');
        }
      },
      error: (error: any) => {
        console.error('❌ ========== ERROR EN REGISTRO ==========');
        console.error('📡 Status:', error.status);
        console.error('📡 StatusText:', error.statusText);
        console.error('📦 Error completo:', error);
        console.error('📦 Error body:', error.error);

        this.procesando = false;

        // Reset field errors
        this.limpiarErrores();

        // Network error / CORS (ProgressEvent) -> status === 0 in HttpErrorResponse
        if (error && error.status === 0) {
          this.mostrarMensaje('❌ No se pudo conectar al servidor. Verifica que el backend esté corriendo.', 'danger');
          return;
        }

        // Backend returned error
        const backendError = error?.error;

        if (backendError) {
          // Si el backend devolvió un objeto con mensaje
          if (backendError.error) {
            // Formato: {error: "mensaje de error"}
            this.mostrarMensaje('❌ ' + backendError.error, 'danger');

            // Mapear errores específicos si existen
            if (backendError.fieldErrors) {
              backendError.fieldErrors.forEach((fe: any) => {
                if (fe?.field && fe?.message && (fe.field in this.errores)) {
                  (this.errores as any)[fe.field] = fe.message;
                }
              });
            }
          } else if (typeof backendError === 'string') {
            // Si el error es una cadena simple
            this.mostrarMensaje('❌ ' + backendError, 'danger');
          }
        } else if (error.message) {
          // Error de red u otro
          this.mostrarMensaje('❌ ' + error.message, 'danger');
        } else {
          // Fallback
          this.mostrarMensaje('❌ Error desconocido al registrar el cliente', 'danger');
        }
      }
    });
  }

  // ✅ VALIDACIONES ESPECIALES PARA EL BACKEND

  private validarEmailPorTipoPersona(): boolean {
    const email = this.nuevoCliente.email;
    const tipoPersona = this.nuevoCliente.tipoPersona;

    if (!email || !tipoPersona) return false;

    const dominio = email.split('@')[1]?.toLowerCase();

    if (tipoPersona === 'UCAB') {
      // UCAB solo puede usar dominios académicos
      if (!['ucab.edu.ve', 'est.ucab.edu.ve'].includes(dominio)) {
        this.errores.email = 'Para tipo UCAB, el email debe ser @ucab.edu.ve o @est.ucab.edu.ve';
        this.mostrarMensaje('Email no válido para tipo UCAB', 'warning');
        return false;
      }
    } else if (tipoPersona === 'VISITANTE') {
      // Visitante no puede usar dominios académicos
      if (['ucab.edu.ve', 'est.ucab.edu.ve'].includes(dominio)) {
        this.errores.email = 'Para tipo VISITANTE, el email no puede ser de la UCAB';
        this.mostrarMensaje('Email no válido para tipo VISITANTE', 'warning');
        return false;
      }
    }

    return true;
  }

  private validarFormatoTelefonoBackend(telefono: string): boolean {
    // El backend espera: 0412-1234567
    const telefonoRegex = /^(0212|0412|0414|0416|0424|0426)-\d{7}$/;
    return telefonoRegex.test(telefono);
  }

  private validarFormatoContrasenaBackend(contrasena: string): boolean {
    // El backend espera: mínimo 8 caracteres, al menos una mayúscula y un número
    const contrasenaRegex = /^(?=.*[A-Z])(?=.*[0-9]).{8,}$/;
    return contrasenaRegex.test(contrasena);
  }

  validarFormulario(): boolean {
    let esValido = true;

    // Validar cédula (CORREGIDO para backend)
    if (!this.nuevoCliente.cedula) {
      this.errores.cedula = 'La cédula es obligatoria';
      esValido = false;
    } else {
      // El backend normaliza la cédula (quita V-, E, etc.)
      const cedulaLimpia = this.nuevoCliente.cedula.replace(/[^0-9]/g, '');
      if (cedulaLimpia.length < 6 || cedulaLimpia.length > 20) {
        this.errores.cedula = 'La cédula debe tener entre 6 y 20 dígitos';
        esValido = false;
      }
    }

    // Validar nombre
    if (!this.nuevoCliente.nombre) {
      this.errores.nombre = 'El nombre es obligatorio';
      esValido = false;
    } else if (this.nuevoCliente.nombre.trim().length < 2) {
      this.errores.nombre = 'El nombre debe tener al menos 2 caracteres';
      esValido = false;
    }

    // Validar apellido
    if (!this.nuevoCliente.apellido) {
      this.errores.apellido = 'El apellido es obligatorio';
      esValido = false;
    } else if (this.nuevoCliente.apellido.trim().length < 2) {
      this.errores.apellido = 'El apellido debe tener al menos 2 caracteres';
      esValido = false;
    }

    // Validar email
    if (!this.nuevoCliente.email) {
      this.errores.email = 'El email es obligatorio';
      esValido = false;
    } else if (!this.validarEmail(this.nuevoCliente.email)) {
      this.errores.email = 'El formato del email no es válido';
      esValido = false;
    }

    // Validar usuario (sin espacios)
    if (!this.nuevoCliente.usuario) {
      this.errores.usuario = 'El usuario es obligatorio';
      esValido = false;
    } else if (this.nuevoCliente.usuario.includes(' ')) {
      this.errores.usuario = 'El usuario no puede contener espacios';
      esValido = false;
    }

    // Validar contraseña
    if (!this.nuevoCliente.contrasena) {
      this.errores.contrasena = 'La contraseña es obligatoria';
      esValido = false;
    } else if (this.nuevoCliente.contrasena.length < 8) {
      this.errores.contrasena = 'La contraseña debe tener al menos 8 caracteres';
      esValido = false;
    }

    // Validar confirmación de contraseña
    if (!this.nuevoCliente.confirmarContrasena) {
      this.errores.confirmarContrasena = 'Debe confirmar la contraseña';
      esValido = false;
    } else if (this.nuevoCliente.contrasena !== this.nuevoCliente.confirmarContrasena) {
      this.errores.confirmarContrasena = 'Las contraseñas no coinciden';
      esValido = false;
    }

    // Validar teléfono (si se proporciona)
    if (this.nuevoCliente.telefono && !this.validarFormatoTelefonoBackend(this.nuevoCliente.telefono)) {
      this.errores.telefono = 'Formato: 0412-1234567';
      esValido = false;
    }

    // Validar dirección
    if (!this.nuevoCliente.direccion || this.nuevoCliente.direccion.trim() === '') {
      this.errores.direccion = 'La dirección es obligatoria';
      esValido = false;
    }

    return esValido;
  }

  validarCampo(campo: keyof ErroresFormulario, valor?: string | null) {
    this.errores[campo] = '';

    // Normalizar valor a cadena para evitar errores cuando está undefined
    const v = valor ?? '';

    switch (campo) {
      case 'cedula':
        if (!v) {
          this.errores.cedula = 'La cédula es obligatoria';
        } else {
          const cedulaLimpia = v.replace(/[^0-9]/g, '');
          if (cedulaLimpia.length < 6) {
            this.errores.cedula = 'La cédula debe tener al menos 6 dígitos';
          } else if (cedulaLimpia.length > 20) {
            this.errores.cedula = 'La cédula no puede tener más de 20 dígitos';
          }
        }
        break;

      case 'nombre':
        if (!v) {
          this.errores.nombre = 'El nombre es obligatorio';
        } else if (v.trim().length < 2) {
          this.errores.nombre = 'El nombre debe tener al menos 2 caracteres';
        }
        break;

      case 'apellido':
        if (!v) {
          this.errores.apellido = 'El apellido es obligatorio';
        } else if (v.trim().length < 2) {
          this.errores.apellido = 'El apellido debe tener al menos 2 caracteres';
        }
        break;

      case 'email':
        if (!v) {
          this.errores.email = 'El email es obligatorio';
        } else if (!this.validarEmail(v)) {
          this.errores.email = 'El formato del email no es válido';
        }
        break;

      case 'usuario':
        if (!v) {
          this.errores.usuario = 'El usuario es obligatorio';
        } else if (v.includes(' ')) {
          this.errores.usuario = 'El usuario no puede contener espacios';
        }
        break;

      case 'contrasena':
        if (!v) {
          this.errores.contrasena = 'La contraseña es obligatoria';
        } else if (v.length < 8) {
          this.errores.contrasena = 'La contraseña debe tener al menos 8 caracteres';
        }
        break;

      case 'confirmarContrasena':
        if (!v) {
          this.errores.confirmarContrasena = 'Debe confirmar la contraseña';
        } else if (this.nuevoCliente.contrasena !== v) {
          this.errores.confirmarContrasena = 'Las contraseñas no coinciden';
        }
        break;

      case 'telefono':
        if (v && !this.validarFormatoTelefonoBackend(v)) {
          this.errores.telefono = 'Formato: 0412-1234567';
        }
        break;

      case 'direccion':
        if (!v || v.trim() === '') {
          this.errores.direccion = 'La dirección es obligatoria';
        }
        break;
    }
  }

  tieneErrores(): boolean {
    return Object.values(this.errores).some(error => error !== '');
  }

  limpiarErrores() {
    this.errores = {
      cedula: '',
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      tipoPersona: '',
      usuario: '',
      contrasena: '',
      confirmarContrasena: '',
      direccion: ''
    };
  }

  limpiarFormulario() {
    this.nuevoCliente = {
      usuario: '',
      contrasena: '',
      confirmarContrasena: '',
      cedula: '',
      nombre: '',
      apellido: '',
      email: '',
      telefono: '',
      tipoPersona: 'UCAB',
      direccion: ''
    };
    this.limpiarErrores();
  }

  private validarEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private mostrarMensaje(mensaje: string, tipo: 'success' | 'danger' | 'warning' | 'info') {
    this.mensaje = mensaje;
    this.mensajeTipo = tipo;
    setTimeout(() => {
      this.mensaje = '';
    }, 5000);
  }

  volverAGestionPerfiles() {
    this.router.navigate(['/gestion-perfiles']);
  }

  // Métodos auxiliares para mostrar ejemplos
  mostrarEjemploCedula() {
    alert(`Formatos aceptados de cédula:\n\n${this.formatosCedula.join('\n')}`);
  }

  mostrarEjemploTelefono() {
    alert(`Formatos aceptados de teléfono:\n\n${this.formatosTelefono.join('\n')}`);
  }

  // Validación en tiempo real
  validarEmailEnTiempoReal() {
    if (this.nuevoCliente.email && this.validarEmail(this.nuevoCliente.email)) {
      // Validar tipo de email según tipo de persona
      this.validarEmailPorTipoPersona();
    }
  }

  validarTelefonoEnTiempoReal() {
    if (this.nuevoCliente.telefono) {
      this.validarCampo('telefono', this.nuevoCliente.telefono);
    }
  }

  validarContrasenaEnTiempoReal() {
    if (this.nuevoCliente.contrasena) {
      this.validarCampo('contrasena', this.nuevoCliente.contrasena);
    }
  }
}
