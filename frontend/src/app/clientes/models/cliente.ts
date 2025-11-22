export interface Cliente {
    id?: string;
    usuario: string;
    contrasena: string;
    confirmarContrasena?: string | null;
    nombre: string;
    apellido: string;
    cedula: string;
    email: string;
    tipoPersona?: string; // 'UCAB' | 'VISITANTE'
    direccion?: string;
    telefono?: string;
    fechaRegistro?: string;
    estado?: string;
}
