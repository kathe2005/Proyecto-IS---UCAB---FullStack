import { Puesto } from './puestos.model';

export interface ResultadoOcupacion {
  exito: boolean;
  mensaje: string;
  puesto: Puesto | null;
  codigoError?: string;
}
