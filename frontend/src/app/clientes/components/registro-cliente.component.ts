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
    paso: 'registro' | 'cargando' | 'confirmacion' = 'registro'; 

    constructor(private clienteService: ClienteService){}

    ngOnInit(): void{
        this.nuevoCliente.tipoPersona = ''; 
    }

    onSubmit()
    {
        this.errorMensaje = null; 
        
        //Validación de coincidencia de contraseñas
        if(this.nuevoCliente.contrasena !== this.confirmarContraseña)
        {
            this.errorMensaje = ' La contraseña y la confirmación no coinciden';
            return; 
        }

        //Validacion de tipo de persona
        if(!this.nuevoCliente.tipoPersona)
        {
            this.errorMensaje = ' Debe seleccionar si es UCAB o VISITANTE'; 
            return; 
        }

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
                        this.paso = 'registro'; 
                        this.isProcessing = false; 
                    }                 
            }); 

    }

    //Metodo para modificar 
    modificarDatos()
    {
        this.paso = 'registro'; 
        this.errorMensaje = null; 
    }

    finalizarRegistro()
    {
        console.log("Registro de cliente confirmado y finalizado"); 
        alert('Registro y confirmación finalizados. Redirigiendo...'); 
    }

}