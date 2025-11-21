import { Puesto } from './puestos.model'; // Importar Puesto desde puestos.model

export interface Reserva {
  id: string;
  puestoId: string;
  clienteId: string;
  usuario: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  turno: string; // MAÑANA, TARDE, NOCHE
  estado: EstadoReserva;
  fechaCreacion: string;
}

export enum EstadoReserva {
  PENDIENTE = 'PENDIENTE',
  CONFIRMADA = 'CONFIRMADA',
  ACTIVA = 'ACTIVA',
  COMPLETADA = 'COMPLETADA',
  CANCELADA = 'CANCELADA',
  NO_SHOW = 'NO_SHOW'
}

export interface ReservaRequest {
  puestoId: string;
  clienteId: string;
  usuario: string;
  fecha: string;
  turno: string;
}

export interface PuestosDisponiblesResponse {
  fecha: string;
  turno: string;
  totalPuestos: number;
  puestosDisponibles: number;
  puestos: Puesto[]; // Usar Puesto importado
  mensaje: string;
}
