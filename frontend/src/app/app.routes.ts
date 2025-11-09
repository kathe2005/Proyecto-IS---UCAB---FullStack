import { Routes } from '@angular/router';
import { RegistroClienteComponent } from './clientes/components/registro-cliente.component';

export const routes: Routes = [
    { 
        path: 'registrar-cliente', 
        component: RegistroClienteComponent 
    },
    // Si alguien va solo a localhost:4200, redirige al registro
    { 
        path: '', 
        redirectTo: 'registrar-cliente', 
        pathMatch: 'full' 
    }
];