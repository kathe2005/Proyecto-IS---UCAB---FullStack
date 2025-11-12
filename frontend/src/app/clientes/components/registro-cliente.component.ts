import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ClienteService } from '../service/cliente.service'; 
import { Cliente } from '../models/cliente'; 
import { FormsModule } from '@angular/forms'; 
import { Observable } from 'rxjs'

@Component({
    selector: 'app-registro-cliente',
    templateUrl: './registro-cliente.component.html',
    styleUrls: ['./registro-cliente.component.css'],
    standalone: true, 
    imports: [FormsModule, CommonModule], 
})


export class RegistroClienteComponent {
    
    // Modelo de datos para el formulario 
    nuevoCliente: Cliente = { 
        usuario:'',
        contrasena: '',
        confirmarContrasena:'',
        nombre: '', 
        apellido: '', 
        cedula: '', 
        email: '', 
        tipoPersona: '',
        direccion: '',
        telefono: '',
    };

    //Variables de control Adicionales 
    confirmarContraseña: string = ''; 
    errorMensaje: string | null = null; 
    isProcessing: boolean = false; 

    //Cliente que recibimos de SpringBoot (La pantalla de confirmacion)
    clienteConfirmado: Cliente | null = null;

    //Estado para controllar qué pantalla se muestra 
    paso: 'credenciales' | 'datos_personales' | 'contacto_ubicacion' | 'registrar' | 'cargando' |'confirmacion' = 'credenciales'; 

    constructor(private clienteService: ClienteService){}

    ngOnInit(): void{
        this.nuevoCliente.tipoPersona = ''; 
    }

    siguientepaso()
    {
        this.errorMensaje = null;
        if (this.paso === 'credenciales')
        {
            //Validación de coincidencia de contraseñas
            if(this.nuevoCliente.contrasena !== this.nuevoCliente.confirmarContrasena)
            {
                this.errorMensaje = 'Las contraseñas no coinciden. Por favor revisalas';
                return; 
            }
            this.paso = 'datos_personales'; 
        }
        else if (this.paso === 'datos_personales')
        {

              //Validacion de tipo de persona
            if(!this.nuevoCliente.tipoPersona)
            {
                this.errorMensaje = ' Debe seleccionar si es UCAB o VISITANTE'; 
                return; 
            }

            //
            if(!this.nuevoCliente.cedula || !this.nuevoCliente.nombre || !this.nuevoCliente.apellido)
            {
                this.errorMensaje = ' Debes completar la cedula, el nombre y el apellido para continuar'; 3
                return; 
            }


            this.paso = 'contacto_ubicacion';
        }
        else if(this.paso === 'contacto_ubicacion')
        {
            this.onSubmit(); 
        }
        else 
        {
            console.warn(' Intento de navegacion en un estado desconocido ' + this.paso); 
        }

    }

    pasoAnterior()
    {
        this.errorMensaje = null; 

        if (this.paso === 'datos_personales')
        {
            this.paso = 'credenciales'; 
        }
        else if(this.paso === 'contacto_ubicacion')
        {
            this.paso = 'datos_personales'; 
        }
    }

    onSubmit()
    {
        this.errorMensaje = null; 

        console.log('Datos para enviar: ', this.nuevoCliente); 


        //Preparación para el envio
        this.isProcessing = true; 
        this.paso = 'cargando'; 
        console.log('iniciando registro...'); 


        //Cambiamos a la pantalla de Cargando 
        this.paso = 'cargando';
        console.log('Iniciando registro...');

        //Llamamos al servicio de Sprint Boot 
        this.clienteService.registrarCliente(this.nuevoCliente)
            .subscribe({
                next: (clienteRespuesta) =>
                {
                    console.log("Datos recibidos de Spring Boot: ", clienteRespuesta);

                    // Guardamos los datos recibidos y pasamos la información 
                    this.clienteConfirmado = clienteRespuesta; 
                    this.paso = 'confirmacion'; 
                    this.isProcessing = false; 
                    
                }, 
                error: (err) => 
                    {
                        console.error('Error de registro: ', err); 
                        this.errorMensaje = err.error?.mensaje || "Hubo un error desconocido al registrar"; 
                        this.paso = 'registrar'; 
                        this.isProcessing = false; 
                    }                 
            }); 

    }

    //Metodo para modificar 
    modificarDatos()
    {
        this.paso = 'registrar'; 
        this.errorMensaje = null; 
    }

    finalizarRegistro()
    {
        console.log("Registro de cliente confirmado y finalizado"); 
        alert('Registro y confirmación finalizados. Redirigiendo...'); 
    }

}