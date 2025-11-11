import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../service/cliente.service'; 
import { ClienteRegistroDTO } from '../models/cliente'; 
import { FormsModule } from '@angular/forms'; 

@Component({
    selector: 'app-registro-cliente',
    templateUrl: './registro-cliente.component.html',
    styleUrls: ['./registro-cliente.component.css'],
    standalone: true, 
    imports: [FormsModule, CommonModule,], // Añadimos LucideAngularModule
})
export class RegistroClienteComponent implements OnInit {

    // Modelo de datos para el formulario 
    nuevoCliente: ClienteRegistroDTO = { 
    usuario: '',
    contrasena: '',
    confirmcontrasena: '', 
    nombre: '', 
    apellido: '', 
    cedula: '', 
    email: '', 
    tipoPersona: '', // Valores: 'UCAB' o 'VISITANTE'
    direccion: '',
    telefono: '',
};

    //Variables de control Adicionales 
    errorMensaje: string | null = null; 
    isProcessing: boolean = false; 

    //Cliente que recibimos de SpringBoot (La pantalla de confirmacion)
    clienteConfirmado: ClienteRegistroDTO | null = null;

    //Estado para controllar qué pantalla se muestra 
    paso: 'credenciales' | 'datos_personales' | 'contacto_ubicacion' | 'cargando' | 'confirmacion' = 'credenciales'; 

    constructor(private clienteService: ClienteService){}

    ngOnInit(): void{
        // Inicializar el tipoPersona para evitar que sea nulo al inicio
        this.nuevoCliente.tipoPersona = 'VISITANTE'; 
    }

    siguientepaso()
    {
        this.errorMensaje = null;
        
        // VALIDACIÓN DE CREDENCIALES
        if (this.paso === 'credenciales')
        {
            
            if(!this.nuevoCliente.usuario || !this.nuevoCliente.contrasena || !this.nuevoCliente.email || !this.nuevoCliente.confirmcontrasena)
            {
                this.errorMensaje = 'Debes completar el usuario, email y ambas contraseñas.';
                return; 
            }
    
            if(this.nuevoCliente.contrasena !== this.nuevoCliente.confirmcontrasena)
            {
                this.errorMensaje = 'Las contraseñas no coinciden. Por favor revisalas.';
                return; 
            }
            // Validación mínima de longitud de contraseña (ejemplo)
            if (this.nuevoCliente.contrasena.length < 6) {
                this.errorMensaje = 'La contraseña debe tener al menos 6 caracteres.';
                return;
            }

            this.paso = 'datos_personales'; 
        }
        // VALIDACIÓN DE DATOS PERSONALES
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
        // VALIDACIÓN DE CONTACTO Y UBICACIÓN
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
            console.warn('Intento de navegación en un estado desconocido: ' + this.paso); 
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
        else if (this.paso === 'confirmacion') {
            // Si estamos en confirmación y queremos volver, vamos al último paso de datos.
            this.paso = 'contacto_ubicacion';
        }
    }

    onSubmit()
    {
        this.errorMensaje = null; 

    // 1. Validación de Lógica Final (Evita 400s por datos faltantes del Paso 3)
    if (!this.nuevoCliente.direccion || !this.nuevoCliente.telefono) {
        this.errorMensaje = 'Debes completar la dirección y el teléfono para registrarte.';
        this.paso = 'contacto_ubicacion'; // Regresar al paso 3
        return;
    }

    // Preparación para el envio y cambio de estado
    this.isProcessing = true; 
    this.paso = 'cargando'; 
    console.log('Iniciando registro...'); 

    // Copia para enviar, eliminando campos no necesarios
    const clienteParaRegistro = {...this.nuevoCliente}; 
    delete clienteParaRegistro.confirmcontrasena; 

    console.log('Datos limpios para enviar:', clienteParaRegistro);

    // Llamamos al servicio de Spring Boot 
    this.clienteService.registrarCliente(clienteParaRegistro)
        .subscribe({
            next: (clienteRespuesta) =>
            {
                console.log("Datos recibidos de Spring Boot: ", clienteRespuesta);
                this.clienteConfirmado = clienteRespuesta; 
                this.paso = 'confirmacion'; 
                this.isProcessing = false; 
            }, 
            error: (err) => {

                console.error('Error completo recibido: ', err);
                this.isProcessing = false; 
                
                let mensajeDelServidor: string = 'Error de conexión. ¿El servidor está corriendo?';
                const status = err.status;

                // 2. LÓGICA CLAVE: BUSCAR TU CLAVE PERSONALIZADA 'mensajeError'
                if (err.error && err.error.mensajeError) {
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
                
                // 3. Volver al inicio para forzar la revisión de datos.
                this.paso = 'credenciales'; 
            }
        });
    }

    //Método para modificar (ya lo tenías bien)
    modificarDatos()
    {
        this.paso = 'credenciales'; 
        this.errorMensaje = null; 
        this.clienteConfirmado = null;
    }

    finalizarRegistro()
    {
        // Aquí deberías navegar a la pantalla de login o al home.
        console.log("Registro de cliente confirmado y finalizado. Navegando..."); 
    }

}