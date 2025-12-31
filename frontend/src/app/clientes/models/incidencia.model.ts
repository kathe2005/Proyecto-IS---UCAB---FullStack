export enum EstadoIncidencia {
  NO_ATENDIDA = 'NO_ATENDIDA',
  ATENDIDA = 'ATENDIDA',
  CANCELADA = 'CANCELADA'
}

export enum TipoProblema {
  PAGO = 'PAGO',
  RESERVA = 'RESERVA',
  PUESTO = 'PUESTO',
  OTROS = 'OTROS'
}

export interface Incidencia {
  id: number;
  clienteAfectado: string;
  estado: EstadoIncidencia;
  tipoProblema: TipoProblema;
  descripcion: string;
  fechaRegistro: string;
}
export interface IncidenciaResponse {
  mensaje: string;
  incidencia?: Incidencia;
  error?: string;
}