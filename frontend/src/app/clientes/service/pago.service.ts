import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pago, PagoRequest, ReservaConCliente, MetodoPago, EstadoPago } from '../models/pago.model';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = 'http://localhost:8080/api/pagos';

  constructor(private http: HttpClient) {}

  // Registrar nuevo pago
  registrarPago(pagoRequest: PagoRequest): Observable<Pago> {
    return this.http.post<Pago>(this.apiUrl, pagoRequest);
  }

  // Obtener reservas pendientes de pago
  obtenerReservasPendientesPago(): Observable<ReservaConCliente[]> {
    return this.http.get<ReservaConCliente[]>(`${this.apiUrl}/reservas-pendientes`);
  }

  // Obtener pago por ID
  obtenerPagoPorId(id: string): Observable<Pago> {
    return this.http.get<Pago>(`${this.apiUrl}/${id}`);
  }

  // Obtener pagos por cliente
  obtenerPagosPorCliente(clienteId: string): Observable<Pago[]> {
    return this.http.get<Pago[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  // Obtener métodos de pago
  obtenerMetodosPago(): { value: MetodoPago; label: string }[] {
    return [
      { value: MetodoPago.EFECTIVO, label: 'Efectivo' },
      { value: MetodoPago.TARJETA_CREDITO, label: 'Tarjeta de Crédito' },
      { value: MetodoPago.TARJETA_DEBITO, label: 'Tarjeta de Débito' },
      { value: MetodoPago.TRANSFERENCIA, label: 'Transferencia Bancaria' },
      { value: MetodoPago.PAGO_MOVIL, label: 'Pago Móvil' }
    ];
  }

  // Calcular monto según tipo de puesto y turno
  calcularMonto(tipoPuesto: string, turno: string): number {
    // Tarifas base (puedes ajustar estos valores)
    const tarifas: { [key: string]: { [key: string]: number } } = {
      'REGULAR': { 'MAÑANA': 5, 'TARDE': 7, 'NOCHE': 10 },
      'DISCAPACITADO': { 'MAÑANA': 3, 'TARDE': 5, 'NOCHE': 7 },
      'DOCENTE': { 'MAÑANA': 4, 'TARDE': 6, 'NOCHE': 8 },
      'VISITANTE': { 'MAÑANA': 8, 'TARDE': 12, 'NOCHE': 15 },
      'MOTOCICLETA': { 'MAÑANA': 3, 'TARDE': 4, 'NOCHE': 5 }
    };

    const tarifa = tarifas[tipoPuesto]?.[turno];
    return tarifa || 5; // Valor por defecto
  }
}
