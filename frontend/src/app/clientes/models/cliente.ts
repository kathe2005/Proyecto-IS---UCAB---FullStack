export interface ClienteRegistroDTO {
    usuario:String; 
    contrasena: String; 
    confirmcontrasena?: String;
    nombre: String; 
    apellido: String; 
    cedula: string;
    email: string;
    tipoPersona: string; 
    direccion: String;
    telefono: String; 
}