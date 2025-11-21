export interface ReporteOcupacion {
  fecha: string;
  turno: string;
  totalPuestos: number;
  puestosOcupados: number;
  puestosDisponibles: number;
  porcentajeOcupacion: number;
  ocupacionPorTipo: { [key: string]: number };
  ocupacionPorUbicacion: { [key: string]: number };
}

export interface ReporteDetallado {
  resumen: ReporteOcupacion;
  detalleOcupacion: DetalleOcupacion[];
  totalRegistros: number;
  fechaGeneracion: string;
}

export interface DetalleOcupacion {
  puestoId: string;
  numeroPuesto: string;
  tipoPuesto: string;
  ubicacion: string;
  fechaOcupacion: string;
  cliente?: {
    nombre: string;
    apellido: string;
    cedula: string;
    tipoPersona: string;
    email: string;
  };
}

export interface EstadisticasRapidas {
  totalPuestos: number;
  puestosOcupados: number;
  puestosDisponibles: number;
  porcentajeOcupacion: number;
  ocupacionPorTipo: { [key: string]: number };
  fecha: string;
}
