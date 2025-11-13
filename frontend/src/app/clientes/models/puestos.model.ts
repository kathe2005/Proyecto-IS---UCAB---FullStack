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
  [TipoPuesto.REGULAR]: { descripcion: 'Regular', color: '#007bff' },
  [TipoPuesto.DISCAPACITADO]: { descripcion: 'Discapacitado', color: '#6f42c1' },
  [TipoPuesto.DOCENTE]: { descripcion: 'Docente', color: '#28a745' },
  [TipoPuesto.VISITANTE]: { descripcion: 'Visitante', color: '#ffc107' },
  [TipoPuesto.MOTOCICLETA]: { descripcion: 'Motocicleta', color: '#fd7e14' }
};

export const EstadoPuestoInfo = {
  [EstadoPuesto.DISPONIBLE]: { descripcion: 'Disponible', color: '#28a745' },
  [EstadoPuesto.OCUPADO]: { descripcion: 'Ocupado', color: '#ffc107' },
  [EstadoPuesto.RESERVADO]: { descripcion: 'Reservado', color: '#17a2b8' },
  [EstadoPuesto.BLOQUEADO]: { descripcion: 'Bloqueado', color: '#6c757d' },
  [EstadoPuesto.MANTENIMIENTO]: { descripcion: 'En Mantenimiento', color: '#fd7e14' }
};
