export interface Puesto {
  id: string;
  numero: string;
  ubicacion: string;
  usuarioOcupante: string | null;
  tipoPuesto: string; // Cambiado de TipoPuesto a string
  estadoPuesto: string; // Cambiado de EstadoPuesto a string
  fechaOcupacion: string | null;
  fechaCreacion: string;
  historialOcupacion: string[];
}

// Mantener los enums para uso interno si es necesario
export enum TipoPuesto {
  REGULAR = 'REGULAR',
  DISCAPACITADO = 'DISCAPACITADO',
  DOCENTE = 'DOCENTE',
  VISITANTE = 'VISITANTE',
  MOTOCICLETA = 'MOTOCICLETA'
}

export enum EstadoPuesto {
  DISPONIBLE = 'DISPONIBLE',
  OCUPADO = 'OCUPADO',
  RESERVADO = 'RESERVADO',
  BLOQUEADO = 'BLOQUEADO',
  MANTENIMIENTO = 'MANTENIMIENTO'
}

// Eliminar o comentar estas constantes si no existen
// export const TipoPuestoInfo = { ... };
// export const EstadoPuestoInfo = { ... };
