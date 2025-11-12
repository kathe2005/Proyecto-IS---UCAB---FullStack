import { EstadoPuesto } from './puestos.model';
export interface OcuparPuestoRequest {
  puestoId: string;
  usuario: string;
  clienteId: string;
  tipoCliente: string;
}
