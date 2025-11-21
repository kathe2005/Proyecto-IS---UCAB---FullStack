import { Component } from '@angular/core';
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

  registrarCliente() {
    // Limpiar errores anteriores
    this.limpiarErrores();

    // Validar todos los campos
    if (!this.validarFormulario()) {
      this.mostrarMensaje('Por favor corrija los errores en el formulario', 'warning');
      return;
    }

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

        let mensajeError = 'Error al registrar el cliente';
        if (err.error) {
          mensajeError = err.error;
        }

        this.mostrarMensaje('❌ ' + mensajeError, 'danger');
      }
    });
  }

  validarFormulario(): boolean {
    let esValido = true;

    // Validar cédula
    if (!this.nuevoCliente.cedula) {
      this.errores.cedula = 'La cédula es obligatoria';
      esValido = false;
    } else if (!/^\d+$/.test(this.nuevoCliente.cedula)) {
      this.errores.cedula = 'La cédula debe contener solo números';
      esValido = false;
    } else if (this.nuevoCliente.cedula.length < 6) {
      this.errores.cedula = 'La cédula debe tener al menos 6 dígitos';
      esValido = false;
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

  validarCampo(campo: keyof ErroresFormulario, valor: string) {
    this.errores[campo] = '';

    switch (campo) {
      case 'cedula':
        if (!valor) {
          this.errores.cedula = 'La cédula es obligatoria';
        } else if (!/^\d+$/.test(valor)) {
          this.errores.cedula = 'La cédula debe contener solo números';
        } else if (valor.length < 6) {
          this.errores.cedula = 'La cédula debe tener al menos 6 dígitos';
        }
        break;

      case 'nombre':
        if (!valor) {
          this.errores.nombre = 'El nombre es obligatorio';
        } else if (valor.length < 2) {
          this.errores.nombre = 'El nombre debe tener al menos 2 caracteres';
        }
        break;

      case 'apellido':
        if (!valor) {
          this.errores.apellido = 'El apellido es obligatorio';
        } else if (valor.length < 2) {
          this.errores.apellido = 'El apellido debe tener al menos 2 caracteres';
        }
        break;

      case 'email':
        if (!valor) {
          this.errores.email = 'El email es obligatorio';
        } else if (!this.validarEmail(valor)) {
          this.errores.email = 'El formato del email no es válido';
        }
        break;

      case 'usuario':
        if (!valor) {
          this.errores.usuario = 'El usuario es obligatorio';
        }
        break;

      case 'contrasena':
        if (!valor) {
          this.errores.contrasena = 'La contraseña es obligatoria';
        } else if (valor.length < 8) {
          this.errores.contrasena = 'La contraseña debe tener al menos 8 caracteres';
        }
        break;

      case 'confirmarContrasena':
        if (!valor) {
          this.errores.confirmarContrasena = 'Debe confirmar la contraseña';
        } else if (this.nuevoCliente.contrasena !== valor) {
          this.errores.confirmarContrasena = 'Las contraseñas no coinciden';
        }
        break;

      case 'direccion':
        if (!valor) {
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
