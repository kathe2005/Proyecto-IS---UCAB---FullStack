export interface Pago {
  id: string;
  reservaId: string;
  clienteId: string;
  monto: number;
  metodoPago: string;  // Cambiado de MetodoPago a string
  estado: string;      // Cambiado de EstadoPago a string
  fechaPago: string;
  referencia: string;
  descripcion?: string;
}

export interface PagoRequest {
  reservaId: string;
  clienteId: string;
  monto: number;
  metodoPago: string;  // Cambiado de MetodoPago a string
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

// Si no usas los enums, puedes eliminarlos o comentarlos
// export enum MetodoPago {
//   EFECTIVO = 'EFECTIVO',
//   TARJETA_CREDITO = 'TARJETA_CREDITO',
//   TARJETA_DEBITO = 'TARJETA_DEBITO',
//   TRANSFERENCIA = 'TRANSFERENCIA',
//   PAGO_MOVIL = 'PAGO_MOVIL'
// }

// export enum EstadoPago {
//   PENDIENTE = 'PENDIENTE',
//   COMPLETADO = 'COMPLETADO',
//   RECHAZADO = 'RECHAZADO',
//   CANCELADO = 'CANCELADO'
// }
