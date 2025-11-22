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

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  registrarCliente(form?: NgForm) {
    // Limpiar errores anteriores
    this.limpiarErrores();

    // Validar todos los campos
    if (!this.validarFormulario()) {
      this.mostrarMensaje('Por favor corrija los errores en el formulario', 'warning');
      return;
    }

    // DEBUG: log del intento de envío
    console.log('Intento de registrar cliente', {
      nuevoCliente: this.nuevoCliente,
      formValid: form?.valid,
      tieneErrores: this.tieneErrores(),
    });

    this.procesando = true;

    this.clienteService.registrarCliente(this.nuevoCliente).subscribe({
      next: (clienteRespuesta: Cliente) => {
        this.procesando = false;
        this.mostrarMensaje('✅ Cliente registrado exitosamente', 'success');

        // Limpiar el formulario
        this.limpiarFormulario();

        setTimeout(() => {
          this.router.navigate(['/gestion-perfiles']);
        }, 2000);
      },
      error: (err: any) => {
        this.procesando = false;
        console.error('Error registrando cliente:', err);

        // Reset field errors
        this.limpiarErrores();

        // Network error / CORS (ProgressEvent) -> status === 0 in HttpErrorResponse
        if (err && err.status === 0) {
          this.mostrarMensaje('❌ No se pudo conectar al servidor. Verifica que el backend esté corriendo y que no haya errores CORS.', 'danger');
          return;
        }

        // If backend returned structured error
        const backend = err?.error;

        // If backend provided a 'mensaje' string, show it
        if (backend && typeof backend === 'object' && typeof backend.mensaje === 'string') {
          // Try to map field-specific messages if present
          // Common patterns: { mensaje: '...', fieldErrors: [{field, message}] } or { errors: [{ field, message }] }
          if (Array.isArray(backend.fieldErrors) && backend.fieldErrors.length) {
            backend.fieldErrors.forEach((fe: any) => {
              if (fe?.field && fe?.message && (fe.field in this.errores)) {
                (this.errores as any)[fe.field] = fe.message;
              }
            });
          }

          if (Array.isArray(backend.errors) && backend.errors.length) {
            backend.errors.forEach((e: any) => {
              if (e?.field && e?.message && (e.field in this.errores)) {
                (this.errores as any)[e.field] = e.message;
              }
            });
          }

          // If backend also provided direct field keys, map them
          Object.keys(this.errores).forEach((k) => {
            if (backend[k]) {
              (this.errores as any)[k] = backend[k];
            }
          });

          // Show top-level message as alert as well
          this.mostrarMensaje('❌ ' + backend.mensaje, 'danger');
          return;
        }

        // If backend returned a plain string
        if (backend && typeof backend === 'string') {
          this.mostrarMensaje('❌ ' + backend, 'danger');
          return;
        }

        // If response has status and message
        if (err?.message) {
          this.mostrarMensaje('❌ ' + err.message, 'danger');
          return;
        }

        // Fallback
        this.mostrarMensaje('❌ Error desconocido al registrar el cliente', 'danger');
      }
    });
  }

  validarFormulario(): boolean {
    let esValido = true;

    // Validar cédula (CORREGIDO)
    if (!this.nuevoCliente.cedula) {
      this.errores.cedula = 'La cédula es obligatoria';
      esValido = false;
    } else {
      const cedulaLimpia = this.nuevoCliente.cedula.replace(/[^0-9]/g, '');
      if (cedulaLimpia.length < 6) {
        this.errores.cedula = 'La cédula debe tener al menos 6 dígitos';
        esValido = false;
      } else if (cedulaLimpia.length > 20) {
        this.errores.cedula = 'La cédula no puede tener más de 20 dígitos';
        esValido = false;
      }
    }

    // Validar nombre
    if (!this.nuevoCliente.nombre) {
      this.errores.nombre = 'El nombre es obligatorio';
      esValido = false;
    } else if (this.nuevoCliente.nombre.length < 2) {
      this.errores.nombre = 'El nombre debe tener al menos 2 caracteres';
      esValido = false;
    }

    // Validar apellido
    if (!this.nuevoCliente.apellido) {
      this.errores.apellido = 'El apellido es obligatorio';
      esValido = false;
    } else if (this.nuevoCliente.apellido.length < 2) {
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

    // Validar usuario
    if (!this.nuevoCliente.usuario) {
      this.errores.usuario = 'El usuario es obligatorio';
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

    // Validar dirección
    if (!this.nuevoCliente.direccion) {
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
          // CORREGIDO: Permitir formatos como V-12345678, E12345678, etc.
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
        } else if (v.length < 2) {
          this.errores.nombre = 'El nombre debe tener al menos 2 caracteres';
        }
        break;

      case 'apellido':
        if (!v) {
          this.errores.apellido = 'El apellido es obligatorio';
        } else if (v.length < 2) {
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

      case 'direccion':
        if (!v) {
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
}
