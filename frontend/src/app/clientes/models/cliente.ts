export interface Cliente {
    id?: string;
    usuario: string;
    contrasena: string;
    confirmarContrasena: string;
    nombre: string;
    apellido: string;
    cedula: string;
    email: string;
    tipoPersona: string; // 'UCAB' o 'VISITANTE'
    direccion: string;
    telefono: string;
}
