import { Component } from '@angular/core';
import { ClienteService } from '../service/cliente.service'; 
import { Cliente } from '../models/cliente'; 
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-registro-cliente',
  templateUrl: './registro-cliente.component.html',
  standalone: true, 
  imports: [
      FormsModule
    ], 
})


export class RegistroClienteComponent {
    
    // 1. VARIABLE DE ESTADO para los datos del formulario (el modelo que enviamos)
    cliente: Cliente = { 
        usuario:'',
        contrasena: '',
        nombre: '', 
        apellido: '', 
        cedula: '', 
        email: '', 
        tipoPersona: 'UCAB' // Valor por defecto
    };

    // 2. VARIABLE DE ESTADO para el manejo de errores (¡La que necesitabas!)
    errorMessage: string = ''; 

    // Inyección de Dependencias: Traemos el servicio de comunicación HTTP
    constructor(private clienteService: ClienteService) { } 

    /**
     * Método que se llama al enviar el formulario.
     * Implementa la lógica de manejo de errores.
     */
    registrarCliente(): void {
        this.errorMessage = ''; // Limpiar el error de un intento anterior

        // Llamamos al servicio (la conexión al Backend)
        this.clienteService.guardarCliente(this.cliente).subscribe({
            next: (clienteGuardado: Cliente) => {
                // Si todo va bien (código 201 Created de Spring Boot)
                alert(`Cliente ${clienteGuardado.nombre} registrado con éxito.`);
                // Opcional: limpiar el formulario o redirigir
            },
            error: (err: Error) => {
                // Si el Backend falla (ej. "El dominio del correo no coincide")
                console.error('Error del Servidor:', err);
                
                // 3. Asignamos el mensaje limpio que capturamos en el Service
                this.errorMessage = err.message; 
            }
        });
    }
}