export interface Cliente {
    id?: String; 
    usuario:String; 
    contrasena: String; 
    confirmarContrasena: String;
    nombre: String; 
    apellido: String; 
    cedula: string;
    email: string;
    tipoPersona: string; // 'UCAB' o 'VISITANTE'
    direccion: String;
    telefono: String; 
}