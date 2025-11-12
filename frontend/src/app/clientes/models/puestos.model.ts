export interface Puesto {
  id: string;
  numero: string;
  ubicacion: string;
  usuarioOcupante: string | null;
  tipoPuesto: TipoPuesto;
  estadoPuesto: EstadoPuesto;
  fechaOcupacion: string | null;
  fechaCreacion: string;
  historialOcupacion: string[];
}

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

export const TipoPuestoInfo = {
  [TipoPuesto.REGULAR]: { descripcion: 'Regular', color: 'blue' },
  [TipoPuesto.DISCAPACITADO]: { descripcion: 'Discapacitado', color: 'purple' },
  [TipoPuesto.DOCENTE]: { descripcion: 'Docente', color: 'green' },
  [TipoPuesto.VISITANTE]: { descripcion: 'Visitante', color: 'yellow' },
  [TipoPuesto.MOTOCICLETA]: { descripcion: 'Motocicleta', color: 'orange' }
};

export const EstadoPuestoInfo = {
  [EstadoPuesto.DISPONIBLE]: { descripcion: 'Disponible', color: 'green' },
  [EstadoPuesto.OCUPADO]: { descripcion: 'Ocupado', color: 'red' },
  [EstadoPuesto.RESERVADO]: { descripcion: 'Reservado', color: 'yellow' },
  [EstadoPuesto.BLOQUEADO]: { descripcion: 'Bloqueado', color: 'gray' },
  [EstadoPuesto.MANTENIMIENTO]: { descripcion: 'En Mantenimiento', color: 'orange' }
};
