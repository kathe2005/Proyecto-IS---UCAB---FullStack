package com.ucab.estacionamiento.model.implement;

import org.springframework.stereotype.Service;

import com.ucab.estacionamiento.model.archivosJson.UnifiedJsonRepository;
import com.ucab.estacionamiento.model.clases.Cliente;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.interfaces.ReporteService;
import com.ucab.estacionamiento.model.interfaces.ReporteOcupacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final UnifiedJsonRepository repository;

    public ReporteServiceImpl(UnifiedJsonRepository repository) {
        this.repository = repository;
        System.out.println("✅ ReporteServiceImpl inicializado con UnifiedJsonRepository");
    }

    @Override
    public ReporteOcupacion generarReporteOcupacion(LocalDate fecha, String turno) {
        System.out.println("📊 Generando reporte para fecha: " + fecha + ", turno: " + turno);
        
        // Cargar puestos desde el repositorio unificado
        List<Puesto> todosLosPuestos = repository.obtenerTodosLosPuestos();
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

        return new ReporteOcupacionImpl.Builder()
                .fecha(fecha)
                .turno(turno)
                .totalPuestos(todosLosPuestos.size())
                .puestosOcupados(puestosOcupados)
                .ocupacionPorTipo(ocupacionPorTipo)
                .ocupacionPorUbicacion(ocupacionPorUbicacion)
                .build();
    }

    @Override
    public ReporteOcupacion generarReporteDiario(LocalDate fecha) {
        System.out.println("📊 Generando reporte diario para: " + fecha);
        
        // Cargar puestos desde el repositorio unificado
        List<Puesto> todosLosPuestos = repository.obtenerTodosLosPuestos();
        if (todosLosPuestos == null) {
            todosLosPuestos = new ArrayList<>();
        }
        
        int puestosOcupados = (int) todosLosPuestos.stream()
                .filter(puesto -> fueOcupadoEnFecha(puesto, fecha))
                .count();

        Map<String, Integer> ocupacionPorTipo = calcularOcupacionPorTipoDiario(todosLosPuestos, fecha);
        Map<String, Integer> ocupacionPorUbicacion = calcularOcupacionPorUbicacionDiario(todosLosPuestos, fecha);

        System.out.println("✅ Reporte diario generado: " + puestosOcupados + "/" + todosLosPuestos.size() + " puestos ocupados");

        return new ReporteOcupacionImpl.Builder()
                .fecha(fecha)
                .turno("COMPLETO")
                .totalPuestos(todosLosPuestos.size())
                .puestosOcupados(puestosOcupados)
                .ocupacionPorTipo(ocupacionPorTipo)
                .ocupacionPorUbicacion(ocupacionPorUbicacion)
                .build();
    }

    @Override
    public List<ReporteOcupacion> generarReporteTendencia() {
        System.out.println("📈 Generando reporte de tendencia (últimos 7 días)");
        
        List<ReporteOcupacion> tendencia = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            ReporteOcupacion reporteDiario = generarReporteDiario(fecha);
            tendencia.add(reporteDiario);
        }
        
        System.out.println("✅ Tendencia generada para " + tendencia.size() + " días");
        return tendencia;
    }

    @Override
    public ReporteOcupacion generarReporteHoy() {
        return generarReporteDiario(LocalDate.now());
    }

    // Método adicional para reporte con datos de clientes
    public Map<String, Object> generarReporteDetallado(LocalDate fecha, String turno) {
        System.out.println("📋 Generando reporte detallado para: " + fecha + ", turno: " + turno);
        
        // Obtener reporte base
        ReporteOcupacion reporteBase = generarReporteOcupacion(fecha, turno);
        
        // Cargar clientes desde el repositorio unificado
        List<Cliente> todosLosClientes = repository.findAll();
        
        // Cargar puestos para información detallada
        List<Puesto> todosLosPuestos = repository.obtenerTodosLosPuestos();
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
                    detalle.put("ubicacion", puesto.getUbicacion());
                    detalle.put("fechaOcupacion", puesto.getFechaOcupacion());
                    
                    // Buscar información del cliente si está disponible
                    if (puesto.getUsuarioOcupante() != null) {
                        Optional<Cliente> cliente = todosLosClientes.stream()
                                .filter(c -> c.getUsuario().equals(puesto.getUsuarioOcupante()))
                                .findFirst();
                        
                        if (cliente.isPresent()) {
                            Cliente c = cliente.get();
                            detalle.put("cliente", Map.of(
                                "nombre", c.getNombre(),
                                "apellido", c.getApellido(),
                                "cedula", c.getCedula(),
                                "tipoPersona", c.getTipoPersona(),
                                "email", c.getEmail()
                            ));
                        }
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
        
        System.out.println("✅ Reporte detallado generado con " + detalleOcupacion.size() + " registros");
        
        return reporteDetallado;
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
}