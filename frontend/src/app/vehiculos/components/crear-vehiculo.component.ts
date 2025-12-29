import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';   
import { Router, RouterModule } from '@angular/router';
import { Vehiculo } from '../models/vehiculo.model';
import { VehiculoService } from '../service/vehiculo.service';

@Component({
    selector: 'app-crear-vehiculo',
    standalone: true, 
    imports: [CommonModule, FormsModule, RouterModule], 
    templateUrl: './crear-vehiculo.component.html',
    styleUrls: ['./crear-vehiculo.component.css']
})
export class CrearVehiculoComponent
{
    nuevoVehiculo: any = { placa: '', marca: '', modelo: '', color: '', idCliente: null  };
    listaVehiculos: Vehiculo[] = []; 
    listaClientes: any[] = [];
    editando: boolean = false;
    mensajeError: string | null = null;

    ngOnInit(): void 
    {
        this.cargarClientes();
        this.listarVehiculos();
    }

    constructor(
        private vehiculoService: VehiculoService,
        private router:Router
    ) {}

    vistaActual: string = 'menu'; 

    cambiarVista(nuevaVista: string) 
    {
        this.vistaActual = nuevaVista;
    }
    
    //--------    REGISTRAR VEHICULO    --------
    registrar(): void 
    {
        
        this.mensajeError = null;

        console.log("📡 Reporte de datos:", this.nuevoVehiculo);

        if (!this.nuevoVehiculo.idCliente || this.nuevoVehiculo.idCliente === null) 
        {
            this.mensajeError = "⚠️ ERROR: Debes seleccionar un cliente de la lista.";
            return;
        }

        this.vehiculoService.registrarVehiculo(this.nuevoVehiculo).subscribe({
            next: (res) => {
                
                console.log("✅ Confirmación recibida:", res);
                alert('¡Vehículo registrado con éxito!');
                this.limpiarFormulario();
                this.cambiarVista('menu');
            },
            error: (err) => {
            
                console.error("❌ Fallo en la comunicación:", err);

                let mensajeFinal = "Error de validación";

                if (err.error) 
                {
                    
                    if (typeof err.error === 'object') 
                    {
                        mensajeFinal= err.error.message || err.error.error || "Datos inválidos";
                    } 
                    else if (typeof err.error === 'string') 
                    {
                        try {
                            const obj = JSON.parse(err.error);
                            mensajeFinal = obj.message || obj.error || err.error;
                        } 
                        catch (e) {
                            mensajeFinal = err.error; 
                        }
                    }

                } 
                else 
                {
                    mensajeFinal = err.statusText || "Servidor no disponible";
                }
            
                this.mensajeError = `❌ Error: ${mensajeFinal}`;
            
                // Pausa táctica: el mensaje se borra tras 8 segundos
                setTimeout(() => this.mensajeError = null, 8000);
            }
        });
    }



    //--------    VOLVER AL MENU   --------
    volverAlMenu() 
    {
        this.router.navigate(['/']); 
    }


    //--------    LIMPIAR FORMULARIO    --------
    limpiarFormulario() 
    {
        this.nuevoVehiculo = { placa: '', marca: '', modelo: '', color: '', idCliente: ''};
    }


    //--------    CARGAR CLIENTES   --------
    cargarClientes() 
    {
        this.vehiculoService.obtenerClientesParaVehiculo().subscribe({
            next: (data) => {
                this.listaClientes = data; 
            },
            error: (err) => console.error('Error de conexion propia', err)
        }); 
    }



    //--------    LISTA DE VEHICULOS    --------
    listarVehiculos() 
    {
        this.vehiculoService.consultarVehiculos().subscribe({
            next: (data) => this.listaVehiculos = data, 
            error: (e) => console.error('Error al listar vehículos', e)
        });
    }
}   
