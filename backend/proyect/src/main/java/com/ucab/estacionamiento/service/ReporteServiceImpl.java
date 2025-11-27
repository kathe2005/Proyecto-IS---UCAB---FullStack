package com.ucab.estacionamiento.service;

import com.ucab.estacionamiento.model.archivosJson.JsonManagerCliente;
import com.ucab.estacionamiento.model.archivosJson.JsonManagerPuesto;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.clases.Puesto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl {

    private final JsonManagerPuesto jsonManagerPuesto;
    private final JsonManagerCliente jsonManagerCliente;

    public ReporteServiceImpl() {
        this.jsonManagerPuesto = new JsonManagerPuesto();
        this.jsonManagerCliente = new JsonManagerCliente();
        System.out.println("✅ ReporteServiceImpl inicializado con gestores JSON");
        System.out.println("📊 Puestos disponibles para reportes: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        System.out.println("👥 Clientes disponibles para reportes: " + jsonManagerCliente.obtenerTodosClientes().size());
    }

    public ReporteOcupacionImpl generarReporteOcupacion(LocalDate fecha, String turno) {
        System.out.println("📊 Generando reporte para fecha: " + fecha + ", turno: " + turno);
        
        // Cargar puestos desde el gestor JSON
        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        if (todosLosPuestos == null) {
            todosLosPuestos = new ArrayList<>();
        }
        
        LocalDateTime[] rangoTurno = obtenerRangoTurno(fecha, turno);
        
        int puestosOcupados = (int) todosLosPuestos.stream()
                .filter(puesto -> fueOcupadoEnTurno(puesto, rangoTurno[0], rangoTurno[1]))
                .count();

        Map<String, Integer> ocupacionPorTipo = calcularOcupacionPorTipo(todosLosPuestos, rangoTurno[0], rangoTurno[1]);
        Map<String, Integer> ocupacionPorUbicacion = calcularOcupacionPorUbicacion(todosLosPuestos, rangoTurno[0], rangoTurno[1]);

        System.out.println("✅ Reporte generado: " + puestosOcupados + "/" + todosLosPuestos.size() + " puestos ocupados en turno " + turno);
        System.out.println("📈 Porcentaje de ocupación: " + String.format("%.1f%%", (puestosOcupados * 100.0) / todosLosPuestos.size()));

        return new ReporteOcupacionImpl.Builder()
                .fecha(fecha)
                .turno(turno)
                .totalPuestos(todosLosPuestos.size())
                .puestosOcupados(puestosOcupados)
                .ocupacionPorTipo(ocupacionPorTipo)
                .ocupacionPorUbicacion(ocupacionPorUbicacion)
                .build();
    }

    public ReporteOcupacionImpl generarReporteDiario(LocalDate fecha) {
        System.out.println("📊 Generando reporte diario para: " + fecha);
        
        // Cargar puestos desde el gestor JSON
        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        if (todosLosPuestos == null) {
            todosLosPuestos = new ArrayList<>();
        }
        
        int puestosOcupados = (int) todosLosPuestos.stream()
                .filter(puesto -> fueOcupadoEnFecha(puesto, fecha))
                .count();

        Map<String, Integer> ocupacionPorTipo = calcularOcupacionPorTipoDiario(todosLosPuestos, fecha);
        Map<String, Integer> ocupacionPorUbicacion = calcularOcupacionPorUbicacionDiario(todosLosPuestos, fecha);

        double porcentajeOcupacion = todosLosPuestos.size() > 0 ? (puestosOcupados * 100.0) / todosLosPuestos.size() : 0;
        
        System.out.println("✅ Reporte diario generado: " + puestosOcupados + "/" + todosLosPuestos.size() + " puestos ocupados");
        System.out.println("📈 Porcentaje de ocupación: " + String.format("%.1f%%", porcentajeOcupacion));

        return new ReporteOcupacionImpl.Builder()
                .fecha(fecha)
                .turno("COMPLETO")
                .totalPuestos(todosLosPuestos.size())
                .puestosOcupados(puestosOcupados)
                .ocupacionPorTipo(ocupacionPorTipo)
                .ocupacionPorUbicacion(ocupacionPorUbicacion)
                .build();
    }

    public List<ReporteOcupacionImpl> generarReporteTendencia() {
        System.out.println("📈 Generando reporte de tendencia (últimos 7 días)");
        
        List<ReporteOcupacionImpl> tendencia = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            System.out.println("📅 Procesando fecha: " + fecha);
            ReporteOcupacionImpl reporteDiario = generarReporteDiario(fecha);
            tendencia.add(reporteDiario);
        }
        
        // Calcular estadísticas de tendencia
        double promedioOcupacion = tendencia.stream()
                .mapToDouble(ReporteOcupacionImpl::getPorcentajeOcupacion)
                .average()
                .orElse(0.0);
        
        System.out.println("✅ Tendencia generada para " + tendencia.size() + " días");
        System.out.println("📊 Ocupación promedio: " + String.format("%.1f%%", promedioOcupacion));
        
        return tendencia;
    }

    public ReporteOcupacionImpl generarReporteHoy() {
        LocalDate hoy = LocalDate.now();
        System.out.println("📅 Generando reporte para hoy: " + hoy);
        return generarReporteDiario(hoy);
    }

    // Método adicional para reporte con datos de clientes
    public Map<String, Object> generarReporteDetallado(LocalDate fecha, String turno) {
        System.out.println("📋 Generando reporte detallado para: " + fecha + ", turno: " + turno);
        
        // Obtener reporte base
        ReporteOcupacionImpl reporteBase = generarReporteOcupacion(fecha, turno);
        
        // Cargar clientes desde el gestor JSON
        List<Cliente> todosLosClientes = jsonManagerCliente.obtenerTodosClientes();
        
        // Cargar puestos para información detallada
        List<Puesto> todosLosPuestos = jsonManagerPuesto.obtenerTodosPuestos();
        if (todosLosPuestos == null) {
            todosLosPuestos = new ArrayList<>();
        }
        
        LocalDateTime[] rangoTurno = obtenerRangoTurno(fecha, turno);
        
        // Obtener puestos ocupados en el turno con información de clientes
        List<Map<String, Object>> detalleOcupacion = todosLosPuestos.stream()
                .filter(puesto -> fueOcupadoEnTurno(puesto, rangoTurno[0], rangoTurno[1]))
                .map(puesto -> {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("puestoId", puesto.getId());
                    detalle.put("numeroPuesto", puesto.getNumero());
                    detalle.put("tipoPuesto", puesto.getTipoPuesto().name());
                    detalle.put("tipoPuestoDesc", puesto.getTipoPuesto().getDescripcion());
                    detalle.put("ubicacion", puesto.getUbicacion());
                    detalle.put("fechaOcupacion", puesto.getFechaOcupacion());
                    detalle.put("estadoPuesto", puesto.getEstadoPuesto().name());
                    detalle.put("estadoPuestoDesc", puesto.getEstadoPuesto().getDescripcion());
                    
                    // Buscar información del cliente si está disponible
                    if (puesto.getUsuarioOcupante() != null) {
                        Optional<Cliente> cliente = todosLosClientes.stream()
                                .filter(c -> c.getUsuario().equals(puesto.getUsuarioOcupante()))
                                .findFirst();
                        
                        if (cliente.isPresent()) {
                            Cliente c = cliente.get();
                            Map<String, Object> clienteInfo = new HashMap<>();
                            clienteInfo.put("nombre", c.getNombre());
                            clienteInfo.put("apellido", c.getApellido());
                            clienteInfo.put("cedula", c.getCedula());
                            clienteInfo.put("tipoPersona", c.getTipoPersona());
                            clienteInfo.put("email", c.getEmail());
                            clienteInfo.put("telefono", c.getTelefono());
                            detalle.put("cliente", clienteInfo);
                        } else {
                            detalle.put("cliente", Map.of(
                                "usuario", puesto.getUsuarioOcupante(),
                                "info", "Cliente no encontrado en base de datos"
                            ));
                        }
                    } else {
                        detalle.put("cliente", Map.of("info", "Sin información de cliente"));
                    }
                    
                    return detalle;
                })
                .collect(Collectors.toList());

        // Construir reporte detallado
        Map<String, Object> reporteDetallado = new HashMap<>();
        reporteDetallado.put("resumen", reporteBase);
        reporteDetallado.put("detalleOcupacion", detalleOcupacion);
        reporteDetallado.put("totalRegistros", detalleOcupacion.size());
        reporteDetallado.put("fechaGeneracion", LocalDateTime.now().toString());
        reporteDetallado.put("parametros", Map.of(
            "fecha", fecha.toString(),
            "turno", turno,
            "rangoTurno", rangoTurno[0] + " a " + rangoTurno[1]
        ));
        
        System.out.println("✅ Reporte detallado generado con " + detalleOcupacion.size() + " registros");
        
        return reporteDetallado;
    }

    // Nuevos métodos para reportes especializados

    public Map<String, Object> generarReportePorTipoPuesto(LocalDate fecha) {
        System.out.println("🎯 Generando reporte por tipo de puesto para: " + fecha);
        
        List<Puesto> puestos = jsonManagerPuesto.obtenerTodosPuestos();
        List<Puesto> puestosOcupados = puestos.stream()
                .filter(puesto -> fueOcupadoEnFecha(puesto, fecha))
                .collect(Collectors.toList());

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("fecha", fecha);
        reporte.put("totalPuestos", puestos.size());
        reporte.put("puestosOcupados", puestosOcupados.size());
        
        // Estadísticas por tipo
        Map<String, Map<String, Object>> statsPorTipo = new HashMap<>();
        for (var tipo : jsonManagerPuesto.obtenerTodosPuestos().stream()
                .map(Puesto::getTipoPuesto)
                .distinct()
                .collect(Collectors.toList())) {
            
            long totalTipo = puestos.stream()
                    .filter(p -> p.getTipoPuesto() == tipo)
                    .count();
            
            long ocupadosTipo = puestosOcupados.stream()
                    .filter(p -> p.getTipoPuesto() == tipo)
                    .count();
            
            double porcentaje = totalTipo > 0 ? (ocupadosTipo * 100.0) / totalTipo : 0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalTipo);
            stats.put("ocupados", ocupadosTipo);
            stats.put("disponibles", totalTipo - ocupadosTipo);
            stats.put("porcentajeOcupacion", porcentaje);
            stats.put("descripcion", tipo.getDescripcion());
            
            statsPorTipo.put(tipo.name(), stats);
        }
        
        reporte.put("estadisticasPorTipo", statsPorTipo);
        reporte.put("fechaGeneracion", LocalDateTime.now());
        
        System.out.println("✅ Reporte por tipo generado con " + statsPorTipo.size() + " tipos analizados");
        
        return reporte;
    }

    public Map<String, Object> generarReporteComparativo(LocalDate fecha1, LocalDate fecha2) {
        System.out.println("📊 Generando reporte comparativo entre " + fecha1 + " y " + fecha2);
        
        ReporteOcupacionImpl reporte1 = generarReporteDiario(fecha1);
        ReporteOcupacionImpl reporte2 = generarReporteDiario(fecha2);
        
        Map<String, Object> comparativo = new HashMap<>();
        comparativo.put("fecha1", fecha1.toString());
        comparativo.put("fecha2", fecha2.toString());
        comparativo.put("reporteFecha1", reporte1);
        comparativo.put("reporteFecha2", reporte2);
        
        // Calcular diferencias
        double diferenciaPorcentaje = reporte2.getPorcentajeOcupacion() - reporte1.getPorcentajeOcupacion();
        int diferenciaOcupados = reporte2.getPuestosOcupados() - reporte1.getPuestosOcupados();
        
        comparativo.put("diferenciaPorcentaje", diferenciaPorcentaje);
        comparativo.put("diferenciaOcupados", diferenciaOcupados);
        comparativo.put("tendencia", diferenciaPorcentaje > 0 ? "ALTA" : diferenciaPorcentaje < 0 ? "BAJA" : "ESTABLE");
        comparativo.put("fechaGeneracion", LocalDateTime.now());
        
        System.out.println("✅ Reporte comparativo generado");
        System.out.println("📈 Tendencia: " + comparativo.get("tendencia"));
        System.out.println("🔢 Diferencia en ocupación: " + String.format("%+.1f%%", diferenciaPorcentaje));
        
        return comparativo;
    }

    // Métodos auxiliares privados
    private LocalDateTime[] obtenerRangoTurno(LocalDate fecha, String turno) {
        LocalDateTime inicio, fin;
        
        switch (turno.toUpperCase()) {
            case "MAÑANA":
                inicio = LocalDateTime.of(fecha, LocalTime.of(6, 0));
                fin = LocalDateTime.of(fecha, LocalTime.of(14, 0));
                break;
            case "TARDE":
                inicio = LocalDateTime.of(fecha, LocalTime.of(14, 0));
                fin = LocalDateTime.of(fecha, LocalTime.of(22, 0));
                break;
            case "NOCHE":
                inicio = LocalDateTime.of(fecha, LocalTime.of(22, 0));
                fin = LocalDateTime.of(fecha.plusDays(1), LocalTime.of(6, 0));
                break;
            default:
                throw new IllegalArgumentException("Turno no válido: " + turno);
        }
        
        System.out.println("⏰ Rango de turno: " + inicio + " - " + fin);
        return new LocalDateTime[]{inicio, fin};
    }

    private boolean fueOcupadoEnTurno(Puesto puesto, LocalDateTime inicio, LocalDateTime fin) {
        if (puesto.getFechaOcupacion() == null) {
            return false;
        }
        
        LocalDateTime fechaOcupacion = puesto.getFechaOcupacion();
        return !fechaOcupacion.isBefore(inicio) && fechaOcupacion.isBefore(fin);
    }

    private boolean fueOcupadoEnFecha(Puesto puesto, LocalDate fecha) {
        return puesto.getFechaOcupacion() != null && 
               puesto.getFechaOcupacion().toLocalDate().equals(fecha);
    }

    private Map<String, Integer> calcularOcupacionPorTipo(List<Puesto> puestos, LocalDateTime inicio, LocalDateTime fin) {
        return puestos.stream()
                .filter(puesto -> fueOcupadoEnTurno(puesto, inicio, fin))
                .collect(Collectors.groupingBy(
                    puesto -> puesto.getTipoPuesto().name(),
                    Collectors.summingInt(p -> 1)
                ));
    }

    private Map<String, Integer> calcularOcupacionPorUbicacion(List<Puesto> puestos, LocalDateTime inicio, LocalDateTime fin) {
        return puestos.stream()
                .filter(puesto -> fueOcupadoEnTurno(puesto, inicio, fin))
                .collect(Collectors.groupingBy(
                    Puesto::getUbicacion,
                    Collectors.summingInt(p -> 1)
                ));
    }

    private Map<String, Integer> calcularOcupacionPorTipoDiario(List<Puesto> puestos, LocalDate fecha) {
        return puestos.stream()
                .filter(puesto -> fueOcupadoEnFecha(puesto, fecha))
                .collect(Collectors.groupingBy(
                    puesto -> puesto.getTipoPuesto().name(),
                    Collectors.summingInt(p -> 1)
                ));
    }

    private Map<String, Integer> calcularOcupacionPorUbicacionDiario(List<Puesto> puestos, LocalDate fecha) {
        return puestos.stream()
                .filter(puesto -> fueOcupadoEnFecha(puesto, fecha))
                .collect(Collectors.groupingBy(
                    Puesto::getUbicacion,
                    Collectors.summingInt(p -> 1)
                ));
    }

    // Método de diagnóstico
    public void diagnostico() {
        System.out.println("🩺 DIAGNÓSTICO DEL SERVICIO REPORTES");
        System.out.println("📊 Total puestos disponibles: " + jsonManagerPuesto.obtenerTodosPuestos().size());
        System.out.println("👥 Total clientes disponibles: " + jsonManagerCliente.obtenerTodosClientes().size());
        
        // Reporte rápido de hoy
        ReporteOcupacionImpl reporteHoy = generarReporteHoy();
        System.out.println("📈 Ocupación hoy: " + reporteHoy.getPuestosOcupados() + "/" + reporteHoy.getTotalPuestos() + 
                          " (" + String.format("%.1f%%", reporteHoy.getPorcentajeOcupacion()) + ")");
        
        // Estadísticas de tipos
        Map<String, Integer> ocupacionPorTipo = reporteHoy.getOcupacionPorTipo();
        System.out.println("🎯 Distribución por tipo: " + ocupacionPorTipo);
    }
}