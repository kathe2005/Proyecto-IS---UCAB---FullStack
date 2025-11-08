export interface Cliente {
    usuario:String; 
    contrasena: String; 
    nombre: String; 
    apellido: String; 
    cedula: string;
    email: string;
    tipoPersona: string; // 'UCAB' o 'VISITANTE'
}