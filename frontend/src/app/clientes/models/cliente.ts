export interface Vehiculo {
    marca?: string;
    modelo?: string;
    placa?: string;
}

export interface Cliente {
    usuario: string;
    contrasena?: string;
    confirmarcontrasena?: string;
    nombre?: string;
    apellido?: string;
    cedula?: string;
    email?: string;
    tipoPersona?: string;
    direccion?: string;
    telefono?: string;
    vehiculos?: Vehiculo[]; // opcional: lista de vehículos del cliente
}