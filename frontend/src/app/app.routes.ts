import { Routes } from '@angular/router';
import { RegistroClienteComponent } from './clientes/components/registro de cliente/registro-cliente.component';

export const routes: Routes = [
    // Página principal: Registro de cliente
    {
        path: '',
        component: RegistroClienteComponent
    },

    // Ruta alternativa para registro
    {
        path: 'registrar-cliente',
        component: RegistroClienteComponent
    },

    // Ocupar puestos como siguiente paso
    {
        path: 'ocupar',
        loadComponent: () => import('./clientes/components/ocuparPuesto/ocupar/ocupar-puestos.component').then(m => m.OcuparPuestoComponent)
    },

    // Resto de rutas del sistema de estacionamiento
    {
        path: 'puestos',
        loadComponent: () => import('./clientes/components/ocuparPuesto/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/disponibles',
        loadComponent: () => import('./clientes/components/ocuparPuesto/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/ocupados',
        loadComponent: () => import('./clientes/components/ocuparPuesto/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/ocupar',
        loadComponent: () => import('./clientes/components/ocuparPuesto/ocupar/ocupar-puestos.component').then(m => m.OcuparPuestoComponent)
    },
    {
        path: 'puestos/estadisticas',
        loadComponent: () => import('./clientes/components/ocuparPuesto/estadisticas/estadisticas.component').then(m => m.EstadisticasComponent)
    },
    {
        path: 'puestos/buscar',
        loadComponent: () => import('./clientes/components/ocuparPuesto/buscarPuesto/buscar-puestos.component').then(m => m.BuscarPuestosComponent)
    },
    {
        path: 'puestos/historial/:id',
        loadComponent: () => import('./clientes/components/ocuparPuesto/historial/historial.component').then(m => m.HistorialComponent)
    },

    // Ruta de inicio del sistema (dashboard)
    {
        path: 'inicio',
        loadComponent: () => import('./clientes/components/ocuparPuesto/home/home.component').then(m => m.HomeComponent)
    },

    // Redirecciones
    {
        path: 'clientes',
        redirectTo: 'registrar-cliente',
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

    // Ruta comodín (si no encuentra ninguna ruta)
    {
        path: '**',
        redirectTo: ''
    }
];
