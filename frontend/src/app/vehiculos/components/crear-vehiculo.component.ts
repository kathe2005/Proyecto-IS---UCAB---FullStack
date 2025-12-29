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
    mensajeError: string | null = null;
    filtroPlaca: string = ''; 
    listaVehiculosFiltrados: Vehiculo [] = []; 
    idClienteConsulta= ""; 



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
            next: (data) => 
            {
                this.listaVehiculos = data;
                this.listaVehiculosFiltrados = data; 
            },
            error: (e) => console.error('Error al listar vehículos', e)
        });
    }

    //--------    CONSULTAR LOS VEHICULOS POR USUARIO     --------
    obtenerNombreCliente(id: any): string 
    {
        const cliente = this.listaClientes.find(c => c.id === id);
        return cliente ? `${cliente.nombre} ${cliente.apellido}` : 'Desconocido';
    }

    //--------    BUSCADOR POR PLACA    --------
    aplicarFiltroMaestro(): void
    {
        this.listaVehiculosFiltrados = this.listaVehiculos.filter(
            v => {
                const matchCliente = this.idClienteConsulta ? (v.idCliente == this.idClienteConsulta) : true;
                const matchPlaca = v.placa.toLowerCase().includes(this.filtroPlaca.toLowerCase());
                return matchCliente && matchPlaca;
            }
        );
    }

    //--------    RESALTAR EL CLIENTE CON LA COINCIDENCIA DE PLACAS    --------
    resaltarTexto(texto: string): string 
    {
        if (!this.filtroPlaca) return texto;
    
        const index = texto.toLowerCase().indexOf(this.filtroPlaca.toLowerCase());
        if (index === -1) return texto;

        const coincidencia = texto.substring(index, index + this.filtroPlaca.length);
        return texto.replace(coincidencia, `<span class="highlight-search">${coincidencia}</span>`);
    }
}   
