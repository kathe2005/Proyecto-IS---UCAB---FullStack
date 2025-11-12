import { Routes } from '@angular/router';
import { RegistroClienteComponent } from './clientes/components/registro-cliente.component';

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

    // Ocupar puestos como siguiente paso (eliminamos la ruta 'inicio')
    {
        path: 'ocupar',
        loadComponent: () => import('./clientes/components/ocuparPuestos/ocupar/ocupar-puestos.component').then(m => m.OcuparPuestoComponent)
    },

    // Resto de rutas del sistema de estacionamiento
    {
        path: 'puestos',
        loadComponent: () => import('./clientes/components/ocuparPuestos/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/disponibles',
        loadComponent: () => import('./clientes/components/ocuparPuestos/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'puestos/ocupados',
        loadComponent: () => import('./clientes/components/ocuparPuestos/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
    },
    {
        path: 'estadisticas',
        loadComponent: () => import('./clientes/components/ocuparPuestos/estadisticas/estadisticas.component').then(m => m.EstadisticasComponent)
    },
    {
        path: 'buscar',
        loadComponent: () => import('./clientes/components/ocuparPuestos/buscar/buscar-puestos.component').then(m => m.BuscarPuestosComponent)
    },
    {
        path: 'historial/:id',
        loadComponent: () => import('./clientes/components/ocuparPuestos/historial/historial.component').then(m => m.HistorialComponent)
    },
    {
        path: 'crear',
        loadComponent: () => import('./clientes/components/ocuparPuestos/crear/crear-puestos.component').then(m => m.CrearPuestosComponent)
    },
    // Redirecciones
    {
        path: 'clientes',
        redirectTo: 'registrar-cliente',
        pathMatch: 'full'
    },

    // Ruta comodín (si no encuentra ninguna ruta)
    {
        path: '**',
        redirectTo: ''
    }
];
