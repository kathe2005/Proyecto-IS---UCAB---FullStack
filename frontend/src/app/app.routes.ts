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
        loadComponent: () => import('./clientes/components/gestionEstacionamiento/gestionPuestos/gestion-puestos.component').then(m => m.GestionPuestosComponent)
    },

    // Gestión de perfiles - Menú principal de perfiles
    {
        path: 'gestion-perfiles',
        loadComponent: () => import('./clientes/components/gestionEstacionamiento/gestionPerfiles/gestion-perfiles.component').then(m => m.GestionPerfilesComponent)
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

    {
        path: 'perfiles/modificar',
        loadComponent: () => import('./clientes/components/modificarPerfiles/modificar-perfil.component').then(m => m.ModificarPerfilesComponent)
    },

    /*
    {
        path: 'perfiles/historial',
        loadComponent: () => import('./clientes/components/gestionPerfiles/historial-reservas/historial-reservas.component').then(m => m.HistorialReservasComponent)
    },
    {
        path: 'perfiles/eliminar',
        loadComponent: () => import('./clientes/components/gestionPerfiles/eliminar-perfil/eliminar-perfil.component').then(m => m.EliminarPerfilComponent)
    },
    */

    // Ocupar puestos
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
    {
        path: 'puestos/crear',
        loadComponent: () => import('./clientes/components/ocuparPuesto/listaPuestos/lista-puestos.component').then(m => m.ListaPuestosComponent)
        // Nota: Deberías crear un componente específico para crear puestos
    },

    // Dashboard del sistema (home anterior)
    {
        path: 'dashboard',
        loadComponent: () => import('./clientes/components/ocuparPuesto/home/home.component').then(m => m.HomeComponent)
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

    // Ruta comodín (si no encuentra ninguna ruta)
    {
        path: '**',
        redirectTo: ''
    }
];
