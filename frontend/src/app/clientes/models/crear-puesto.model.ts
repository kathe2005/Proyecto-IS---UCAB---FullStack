// crear-puesto.model.ts - CORREGIDO
import { TipoPuesto, EstadoPuesto, Puesto } from './puestos.model';

export interface CrearPuestoRequest {
  numero: string;
  ubicacion: string;
  tipoPuesto: TipoPuesto;
  estadoPuesto: EstadoPuesto;
}

export interface CrearPuestoResponse {
  exito: boolean;
  mensaje: string;
  puesto: Puesto | null;
  errores?: string[];
}
