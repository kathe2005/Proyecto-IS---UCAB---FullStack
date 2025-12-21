import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pago, PagoRequest, ReservaConCliente } from '../models/pago.model';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = 'http://localhost:8080/reservas/api/pagos';

  constructor(private http: HttpClient) {}

  registrarPago(pagoRequest: PagoRequest): Observable<any> {
    console.log('💰 Enviando pago al backend:', pagoRequest);

    const requestParaBackend = {
      reservaId: pagoRequest.reservaId,
      clienteId: pagoRequest.clienteId,
      monto: pagoRequest.monto,
      metodoPago: pagoRequest.metodoPago,
      referencia: pagoRequest.referencia,
      descripcion: pagoRequest.descripcion || ''
    };

    return this.http.post(this.apiUrl, requestParaBackend);
  }

  registrarPagoSimple(reservaId: string, clienteId: string, monto: number,
                      metodoPago: string, referencia: string, descripcion?: string): Observable<any> {
    const params = new HttpParams()
      .set('reservaId', reservaId)
      .set('clienteId', clienteId)
      .set('monto', monto.toString())
      .set('metodoPago', metodoPago)
      .set('referencia', referencia)
      .set('descripcion', descripcion || '');

    return this.http.post(`${this.apiUrl}/registrar`, {}, { params });
  }

  obtenerReservasPendientesPago(): Observable<ReservaConCliente[]> {
    console.log('📥 Obteniendo reservas pendientes de pago...');
    return this.http.get<ReservaConCliente[]>(`${this.apiUrl}/reservas-pendientes`);
  }

  obtenerPagoPorId(id: string): Observable<Pago> {
    return this.http.get<Pago>(`${this.apiUrl}/${id}`);
  }

  obtenerTodosLosPagos(): Observable<Pago[]> {
    return this.http.get<Pago[]>(this.apiUrl);
  }

  calcularTarifa(tipoPuesto: string, turno: string): Observable<any> {
    const params = new HttpParams()
      .set('tipoPuesto', tipoPuesto)
      .set('turno', turno);

    return this.http.get(`${this.apiUrl}/calcular-tarifa`, { params });
  }

  obtenerMetodosPago(): { value: string; label: string }[] {
    return [
      { value: 'EFECTIVO', label: 'Efectivo' },
      { value: 'TARJETA_CREDITO', label: 'Tarjeta de Crédito' },
      { value: 'TARJETA_DEBITO', label: 'Tarjeta de Débito' },
      { value: 'TRANSFERENCIA', label: 'Transferencia Bancaria' },
      { value: 'PAGO_MOVIL', label: 'Pago Móvil' }
    ];
  }

  calcularMontoFrontend(tipoPuesto: string, turno: string): number {
    const tarifas: { [key: string]: { [key: string]: number } } = {
      'REGULAR': { 'MAÑANA': 5, 'TARDE': 7, 'NOCHE': 10 },
      'DISCAPACITADO': { 'MAÑANA': 3, 'TARDE': 5, 'NOCHE': 7 },
      'DOCENTE': { 'MAÑANA': 4, 'TARDE': 6, 'NOCHE': 8 },
      'VISITANTE': { 'MAÑANA': 8, 'TARDE': 12, 'NOCHE': 15 },
      'MOTOCICLETA': { 'MAÑANA': 3, 'TARDE': 4, 'NOCHE': 5 }
    };

    const tarifa = tarifas[tipoPuesto]?.[turno.toUpperCase()];
    return tarifa || 5;
  }
}
