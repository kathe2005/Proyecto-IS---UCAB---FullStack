import { Routes } from '@angular/router';
import { RegistroClienteComponent } from './clientes/components/registroCliente/registro-cliente.component';
import { GestionEstacionamientoComponent } from './clientes/components/gestionEstacionamiento/gestion-estacionamiento.component';

export const routes: Routes = [
    // Página principal: Menú de gestión de estacionamiento
    {
        path: '',
        component: GestionEstacionamientoComponent
    },

    // Ruta alternativa para el menú principal
    {
        path: 'inicio',
        component: GestionEstacionamientoComponent
    },

    // Gestión de puestos - Menú principal de puestos
    {
        path: 'gestion-puestos',
        loadComponent: () => import('./clientes/components/gestionPuestos/gestion-puestos.component').then(m => m.GestionPuestosComponent)
    },

    // Gestión de perfiles - Menú principal de perfiles
    {
        path: 'gestion-perfiles',
        loadComponent: () => import('./clientes/components/gestionPerfiles/gestion-perfiles.component').then(m => m.GestionPerfilesComponent)
    },

    // NUEVA RUTA: Gestión de reservas - Menú principal de reservas
    {
        path: 'gestion-reservas',
        loadComponent: () => import('./clientes/components/gestionReservas/gestion-reserva.component').then(m => m.GestionReservaComponent)
    },

    // Registro de cliente (ruta existente)
    {
        path: 'registrar-cliente',
        component: RegistroClienteComponent
    },

    // Nueva ruta para registrar cliente desde gestión de perfiles
    {
        path: 'perfiles/registrar',
        component: RegistroClienteComponent
    },

    // Rutas para gestión de perfiles
    {
        path: 'perfiles/consultar',
        loadComponent: () => import('./clientes/components/consultarPerfiles/consultar-perfiles.component').then(m => m.ConsultarPerfilesComponent)
    },

    // NUEVAS RUTAS: Gestión de reservas
    {
        path: 'reservas/consultar-disponibilidad',
        loadComponent: () => import('./clientes/components/consultarPuestosDisponibles/consultar-disponibilidad.component').then(m => m.ConsultarDisponibilidadComponent)
    },

    {
        path: 'reservas/crear',
        loadComponent: () => import('./clientes/components/reservaPuesto/reservar-puesto.component').then(m => m.ReservarPuestoComponent)
    },

    {
      path: 'reservas/pagos',
      loadComponent: () => import('./clientes/components/registroPago/registrar-pago.component').then(m => m.RegistrarPagoComponent)
    },
    /*
    {
        path: 'reservas/modificar',
        loadComponent: () => import('./clientes/components/modificarReserva/modificar-reserva.component').then(m => m.ModificarReservaComponent)
    },
    {
        path: 'reservas/cancelar',
        loadComponent: () => import('./clientes/components/cancelarReserva/cancelar-reserva.component').then(m => m.CancelarReservaComponent)
    },
    {
        path: 'reservas/activas',
        loadComponent: () => import('./clientes/components/listaReservas/lista-reservas.component').then(m => m.ListaReservasComponent)
    },
    */

    // Ocupar puestos
    {
        path: 'ocupar',
        loadComponent: () => import('./clientes/components/ocuparPuesto/ocupar-puestos.component').then(m => m.OcuparPuestoComponent)
    },

    // NUEVA RUTA: Desocupar puestos
    {
        path: 'desocupar',
        loadComponent: () => import('./clientes/components/desocuparPuestos/desocupar-puesto.component').then(m => m.DesocuparPuestosComponent)
    },

    // NUEVA RUTA: Reportes de ocupación
    {
        path: 'reportes',
        loadComponent: () => import('./clientes/components/reporteOcupacion/reportes.component').then(m => m.ReportesComponent)
    },

    // Resto de rutas del sistema de estacionamiento
    {
        path: 'puestos',
        loadComponent: () => import('./clientes/components/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/disponibles',
        loadComponent: () => import('./clientes/components/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/ocupados',
        loadComponent: () => import('./clientes/components/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/ocupar',
        loadComponent: () => import('./clientes/components/ocuparPuesto/ocupar-puestos.component').then(m => m.OcuparPuestoComponent)
    },
    // NUEVA RUTA: Desocupar puesto desde gestión de puestos
    {
        path: 'puestos/desocupar',
        loadComponent: () => import('./clientes/components/desocuparPuestos/desocupar-puesto.component').then(m => m.DesocuparPuestosComponent)
    },
    {
        path: 'puestos/estadisticas',
        loadComponent: () => import('./clientes/components/estadisticas/estadisticas.component').then(m => m.EstadisticasComponent)
    },
    {
        path: 'puestos/buscar',
        loadComponent: () => import('./clientes/components/buscarPuesto/buscar-puestos.component').then(m => m.BuscarPuestosComponent)
    },
    {
        path: 'puestos/historial/:id',
        loadComponent: () => import('./clientes/components/historial/historial.component').then(m => m.HistorialComponent)
    },
    // RUTA CORREGIDA: Crear puesto
    {
        path: 'puestos/crear',
        loadComponent: () => import('./clientes/components/crearPuesto/crear-puesto.component').then(m => m.CrearPuestoComponent)
    },

    // Dashboard del sistema (home anterior)
    {
        path: 'dashboard',
        loadComponent: () => import('./clientes/components/home/home.component').then(m => m.HomeComponent)
    },

    // Redirecciones
    {
        path: 'clientes',
        redirectTo: 'registrar-cliente',
        pathMatch: 'full'
    },
    {
        path: 'perfiles',
        redirectTo: 'gestion-perfiles',
        pathMatch: 'full'
    },
    {
        path: 'reservas',
        redirectTo: 'gestion-reservas',
        pathMatch: 'full'
    },
    {
        path: 'home',
        redirectTo: 'inicio',
        pathMatch: 'full'
    },
    {
        path: 'estadisticas',
        redirectTo: 'puestos/estadisticas',
        pathMatch: 'full'
    },
    {
        path: 'buscar',
        redirectTo: 'puestos/buscar',
        pathMatch: 'full'
    },
    {
        path: 'gestion',
        redirectTo: 'gestion-puestos',
        pathMatch: 'full'
    },
    // NUEVA REDIRECCIÓN: Para liberar puestos
    {
        path: 'liberar',
        redirectTo: 'desocupar',
        pathMatch: 'full'
    },
    {
        path: 'puestos/liberar',
        redirectTo: 'puestos/desocupar',
        pathMatch: 'full'
    },
    // NUEVA REDIRECCIÓN: Para reportes desde gestión de puestos
    {
        path: 'puestos/reportes',
        redirectTo: 'reportes',
        pathMatch: 'full'
    },
    {
        path: 'gestion-puestos/reportes',
        redirectTo: 'reportes',
        pathMatch: 'full'
    },
    // NUEVA REDIRECCIÓN: Para consultar disponibilidad
    {
        path: 'disponibilidad',
        redirectTo: 'reservas/consultar-disponibilidad',
        pathMatch: 'full'
    },

    // Ruta comodín (si no encuentra ninguna ruta)
    {
        path: '**',
        redirectTo: ''
    }
];
