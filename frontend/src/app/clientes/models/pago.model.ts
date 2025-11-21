export interface Pago {
  id: string;
  reservaId: string;
  clienteId: string;
  monto: number;
  metodoPago: MetodoPago;
  estado: EstadoPago;
  fechaPago: string;
  referencia: string;
  descripcion?: string;
}

export interface PagoRequest {
  reservaId: string;
  clienteId: string;
  monto: number;
  metodoPago: MetodoPago;
  referencia: string;
  descripcion?: string;
}

export interface ReservaConCliente {
  id: string;
  puestoId: string;
  clienteId: string;
  usuario: string;
  fecha: string;
  turno: string;
  estado: string;
  cliente: {
    nombre: string;
    apellido: string;
    cedula: string;
  };
  puesto: {
    numero: string;
    ubicacion: string;
    tipoPuesto: string;
  };
}

export enum MetodoPago {
  EFECTIVO = 'EFECTIVO',
  TARJETA_CREDITO = 'TARJETA_CREDITO',
  TARJETA_DEBITO = 'TARJETA_DEBITO',
  TRANSFERENCIA = 'TRANSFERENCIA',
  PAGO_MOVIL = 'PAGO_MOVIL'
}

export enum EstadoPago {
  PENDIENTE = 'PENDIENTE',
  COMPLETADO = 'COMPLETADO',
  RECHAZADO = 'RECHAZADO',
  CANCELADO = 'CANCELADO'
}
