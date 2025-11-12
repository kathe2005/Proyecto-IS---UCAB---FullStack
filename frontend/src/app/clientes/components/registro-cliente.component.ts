import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../service/cliente.service'; 
import { Cliente } from '../models/cliente'; 
import { FormsModule } from '@angular/forms'; 
import { Observable } from 'rxjs';

import { Router } from '@angular/router';

@Component({
    selector: 'app-registro-cliente',
    templateUrl: './registro-cliente.component.html',
    styleUrls: ['./registro-cliente.component.css'],
    standalone: true, 
    imports: [FormsModule, CommonModule,], // Añadimos LucideAngularModule
})
export class RegistroClienteComponent implements OnInit {

    // Modelo de datos para el formulario 
    nuevoCliente: Cliente = { 
    usuario: '',
    contrasena: '',
    confirmarcontrasena: '', 
    nombre: '', 
    apellido: '', 
    cedula: '', 
    email: '', 
    tipoPersona: '', 
    direccion: '',
    telefono: '',
};

    //Variables de control Adicionales 
    errorMensaje: string | null = null ; 
    estaEditando: boolean = false;

    //Cliente que recibimos de SpringBoot (La pantalla de confirmacion)
    clienteConfirmado: Cliente | null = null;

    //Estado para controllar qué pantalla se muestra 
    paso: 'credenciales' | 'datos_personales' | 'contacto_ubicacion' | 'cargando' | 'confirmacion' = 'credenciales'; 

    constructor(private clienteService: ClienteService, private router: Router){}

    ngOnInit(): void
    {
        // Inicializar el tipoPersona para evitar que sea nulo al inicio
        this.nuevoCliente.tipoPersona = 'Seleccionar'; 
    }

    siguientepaso()
    {
        this.errorMensaje = null;

        // VALIDACIÓN DE CREDENCIALES
        if (this.paso === 'credenciales')
        {

            if(!this.nuevoCliente.usuario || !this.nuevoCliente.contrasena || !this.nuevoCliente.email || !this.nuevoCliente.confirmarcontrasena)
            {
                this.errorMensaje = 'Debes completar el usuario, email y ambas contraseñas.';
                return; 
            }
    
        
            if(this.nuevoCliente.contrasena !== this.nuevoCliente.confirmarcontrasena)
            {
                this.errorMensaje = 'Las contraseñas no coinciden. Por favor revisalas.';
                return; 
            }

            this.paso = 'datos_personales'; 
        }
        else if (this.paso === 'datos_personales')
        {
             // Validación de tipo de persona
            if(!this.nuevoCliente.tipoPersona || (this.nuevoCliente.tipoPersona !== 'UCAB' && this.nuevoCliente.tipoPersona !== 'VISITANTE'))
            {
                this.errorMensaje = 'Debe seleccionar si es UCAB o VISITANTE.'; 
                return; 
            }
            
            if(!this.nuevoCliente.cedula || !this.nuevoCliente.nombre || !this.nuevoCliente.apellido)
            {
                this.errorMensaje = 'Debes completar la cédula, el nombre y el apellido para continuar.'; 
                return; 
            }

            this.paso = 'contacto_ubicacion';
        }


        // Validación de contacto y ubicación 
        else if(this.paso === 'contacto_ubicacion')
        {
            // Corregido: La validación estaba pidiendo cédula/nombre/apellido de nuevo.
            if(!this.nuevoCliente.direccion || !this.nuevoCliente.telefono )
            {
                this.errorMensaje = 'Debes completar la dirección y el teléfono para registrarte.'; 
                return; 
            }
            this.onSubmit(); 
        }
        else 
        {
            console.log('Intento de navegación en un estado desconocido: ' + this.paso); 
        }

    }

    pasoAnterior()
    {
        this.errorMensaje = null; 
        this.clienteConfirmado = null; // Reiniciar datos de confirmación

        if (this.paso === 'datos_personales')
        {
            this.paso = 'credenciales'; 
        }
        else if(this.paso === 'contacto_ubicacion')
        {
            this.paso = 'datos_personales'; 
        }
        else if (this.paso === 'confirmacion') 
        {
         // Si estamos en confirmación y queremos volver, vamos al último paso de datos.
        this.paso = 'contacto_ubicacion';
        }
    }

    onSubmit()
    {
        this.errorMensaje = null; 
        this.paso = 'cargando'; 
        console.log('Iniciando registro...'); 

        let solicitud$: Observable<any>;

        //Decidir si es Actualización (PUT) o Registro (POST)
        if (this.estaEditando) 
        {
            solicitud$ = this.clienteService.actualizarCliente(this.nuevoCliente);
        } 
        else 
        {
            solicitud$ = this.clienteService.registrarCliente(this.nuevoCliente);
        }

        solicitud$.subscribe({ 
        next: (clienteRespuesta) =>
        {
            console.log("Datos recibidos de Spring Boot: ", clienteRespuesta);
            this.clienteConfirmado = clienteRespuesta; 
            this.paso = 'confirmacion';
            this.estaEditando = false; 
        }, 
        error: (err) => {
            console.log('Error completo recibido: ', err);
            
            let mensajeDelServidor: string = 'Error de conexión. ¿El servidor está corriendo?';
            const status = err.status;

            // 2. LÓGICA CLAVE: BUSCAR TU CLAVE PERSONALIZADA 'mensajeError'
                if (err.error && err.error.mensajeError) 
                {
                    mensajeDelServidor = err.error.mensajeError;
                } 
                // Fallback para errores de validación automática (si Spring envía detalles)
                else if (err.error && err.error.detalles && Array.isArray(err.error.detalles)) {
                        mensajeDelServidor = `Error ${status}: ${err.error.detalles.join(' | ')}`;
                }
                
                // Fallback genérico para errores de red o servidor
                else if (status === 0) {
                    mensajeDelServidor = 'No se pudo conectar con el servidor. Verifica la URL y si está encendido.';
                } else if (status >= 500) {
                    mensajeDelServidor = `Error del Servidor (${status}). Contacte a soporte.`;
                }

                this.errorMensaje = mensajeDelServidor; 

                const mensajeLower = mensajeDelServidor.toLowerCase();

                if (mensajeLower.includes('usuario') || mensajeLower.includes('email') || mensajeLower.includes('contraseña')) 
                {
                    this.paso = 'credenciales';
                } 
                else if (mensajeLower.includes('cédula') || mensajeLower.includes('nombre') || mensajeLower.includes('apellido') || mensajeLower.includes('persona')) 
                {
                    
                    this.paso = 'datos_personales';
                } 
                else if (mensajeLower.includes('teléfono') || mensajeLower.includes('dirección')) 
                {
                    this.paso = 'contacto_ubicacion';
                } 
                else 
                {
                    this.paso = 'credenciales';
                }
        }
    });
    }

    //Método para modificar 
    modificarDatos()
    {
        
        this.paso = 'credenciales'; 
        this.errorMensaje = null; 
        this.estaEditando = true; 


        if (this.clienteConfirmado) 
        {
            this.nuevoCliente.usuario = this.clienteConfirmado.usuario;
        }
        
        console.log("Modo edición activado. Usuario clave: ", this.nuevoCliente.usuario);

    }

    finalizarRegistro()
    {
        // Aquí deberías navegar a la pantalla de login o al home.
        console.log("Registro de cliente confirmado y finalizado"); 

        // Mostrar mensaje de éxito
        alert('✅ Registro exitoso! Serás redirigido al sistema de estacionamiento.');

        // Redirigir al dashboard del sistema
        setTimeout(() => {
        this.router.navigate(['/inicio']);
        },  
    1500);
    }
}