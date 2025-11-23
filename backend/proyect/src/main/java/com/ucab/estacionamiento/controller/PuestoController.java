package com.ucab.estacionamiento.controller;

import com.ucab.estacionamiento.model.archivosJson.JsonManager;
import com.ucab.estacionamiento.model.clases.OcuparPuestoRequest;
import com.ucab.estacionamiento.model.clases.Puesto;
import com.ucab.estacionamiento.model.clases.ResultadoOcupacion;
import com.ucab.estacionamiento.model.enums.EstadoPuesto;
import com.ucab.estacionamiento.model.enums.TipoPuesto;
import com.ucab.estacionamiento.model.interfaces.PuestoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/puestos")
public class PuestoController {

    @Autowired
    private PuestoService puestoService;

    @GetMapping
    public String mostrarTodosLosPuestos(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Todos los Puestos");
        return "puestos/lista";
    }

    @GetMapping("/disponibles")
    public String mostrarPuestosDisponibles(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos Disponibles");
        return "puestos/lista";
    }

    @GetMapping("/ocupados")
    public String mostrarPuestosOcupados(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.OCUPADO);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos Ocupados");
        return "puestos/lista";
    }

    @GetMapping("/bloqueados")
    public String mostrarPuestosBloqueados(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.BLOQUEADO);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos Bloqueados");
        return "puestos/lista";
    }

    @GetMapping("/mantenimiento")
    public String mostrarPuestosMantenimiento(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos en Mantenimiento");
        return "puestos/lista";
    }

    @GetMapping("/ocupar")
    public String mostrarFormularioOcupar(Model model) {
        List<Puesto> disponibles = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
        model.addAttribute("puestosDisponibles", disponibles);
        model.addAttribute("ocuparRequest", new OcuparPuestoRequest());
        return "puestos/ocupar";
    }

    @PostMapping("/ocupar")
    public String ocuparPuesto(@ModelAttribute OcuparPuestoRequest request, Model model) {
        ResultadoOcupacion resultado = puestoService.ocuparPuesto(request.getPuestoId(), request.getUsuario());
        model.addAttribute("resultado", resultado);
        
        if (resultado.isExito()) {
            return "redirect:/puestos";
        } else {
            List<Puesto> disponibles = puestoService.obtenerPuestosPorEstado(EstadoPuesto.DISPONIBLE);
            model.addAttribute("puestosDisponibles", disponibles);
            return "puestos/ocupar";
        }
    }

    @GetMapping("/asignar-manual")
    public String mostrarFormularioAsignarManual(Model model) {
        List<Puesto> puestos = puestoService.obtenerPuestos();
        model.addAttribute("puestos", puestos);
        model.addAttribute("asignarRequest", new OcuparPuestoRequest());
        return "puestos/asignar-manual";
    }

    @PostMapping("/asignar-manual")
    public String asignarPuestoManual(@ModelAttribute OcuparPuestoRequest request, Model model) {
        ResultadoOcupacion resultado = puestoService.asignarPuestoManual(request.getPuestoId(), request.getUsuario());
        model.addAttribute("resultado", resultado);
        
        if (resultado.isExito()) {
            return "redirect:/puestos";
        } else {
            List<Puesto> puestos = puestoService.obtenerPuestos();
            model.addAttribute("puestos", puestos);
            return "puestos/asignar-manual";
        }
    }

    @PostMapping("/liberar/{id}")
    public String liberarPuesto(@PathVariable String id) {
        puestoService.liberarPuesto(id);
        return "redirect:/puestos";
    }

    @PostMapping("/bloquear/{id}")
    public String bloquearPuesto(@PathVariable String id) {
        puestoService.bloquearPuesto(id);
        return "redirect:/puestos";
    }

    @PostMapping("/desbloquear/{id}")
    public String desbloquearPuesto(@PathVariable String id) {
        puestoService.desbloquearPuesto(id);
        return "redirect:/puestos";
    }

    @PostMapping("/mantenimiento/{id}")
    public String ponerEnMantenimiento(@PathVariable String id) {
        puestoService.ponerPuestoEnMantenimiento(id);
        return "redirect:/puestos";
    }

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        int total = puestoService.obtenerPuestos().size();
        int disponibles = puestoService.contarPuestosDisponibles();
        int ocupados = puestoService.contarPuestosOcupados();
        int bloqueados = puestoService.contarPuestosBloqueados();
        int mantenimiento = puestoService.obtenerPuestosPorEstado(EstadoPuesto.MANTENIMIENTO).size();
        
        model.addAttribute("total", total);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("ocupados", ocupados);
        model.addAttribute("bloqueados", bloqueados);
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("porcentajeOcupacion", total > 0 ? (ocupados * 100.0) / total : 0);
        
        return "puestos/estadisticas";
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("tiposPuesto", TipoPuesto.values());
        model.addAttribute("estadosPuesto", EstadoPuesto.values());
        return "puestos/buscar";
    }

    @GetMapping("/buscar/estado")
    public String buscarPorEstado(@RequestParam String estado, Model model) {
        try {
            EstadoPuesto estadoPuesto = EstadoPuesto.valueOf(estado.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorEstado(estadoPuesto);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos - Estado: " + estadoPuesto.getDescripcion());
            model.addAttribute("criterioBusqueda", "Estado: " + estadoPuesto.getDescripcion());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Estado no válido: " + estado);
        }
        return "puestos/lista";
    }

    @GetMapping("/buscar/tipo")
    public String buscarPorTipo(@RequestParam String tipo, Model model) {
        try {
            TipoPuesto tipoPuesto = TipoPuesto.valueOf(tipo.toUpperCase());
            List<Puesto> puestos = puestoService.obtenerPuestosPorTipo(tipoPuesto);
            model.addAttribute("puestos", puestos);
            model.addAttribute("titulo", "Puestos - Tipo: " + tipoPuesto.getDescripcion());
            model.addAttribute("criterioBusqueda", "Tipo: " + tipoPuesto.getDescripcion());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Tipo no válido: " + tipo);
        }
        return "puestos/lista";
    }

    @GetMapping("/buscar/ubicacion")
    public String buscarPorUbicacion(@RequestParam String ubicacion, Model model) {
        List<Puesto> puestos = puestoService.filtrarPuestosPorUbicacion(ubicacion);
        model.addAttribute("puestos", puestos);
        model.addAttribute("titulo", "Puestos - Ubicación: " + ubicacion);
        model.addAttribute("criterioBusqueda", "Ubicación: " + ubicacion);
        return "puestos/lista";
    }

    @GetMapping("/historial/{id}")
    public String mostrarHistorial(@PathVariable String id, Model model) {
        List<String> historial = puestoService.obtenerHistorial(id);
        Puesto puesto = puestoService.obtenerPuestoPorId(id).orElse(null);
        
        model.addAttribute("historial", historial);
        model.addAttribute("puesto", puesto);
        
        return "puestos/historial";
    }

    @GetMapping("/json")
    public String mostrarJson(Model model) {
        JsonManager.mostrarArchivoJSON();
        model.addAttribute("mensaje", "Contenido del archivo JSON mostrado en la consola del servidor");
        return "puestos/json";
    }

    @GetMapping("/reasignar/{id}")
    public String mostrarFormularioReasignar(@PathVariable String id, Model model) {
        Puesto puesto = puestoService.obtenerPuestoPorId(id).orElse(null);
        if (puesto == null) {
            return "redirect:/puestos";
        }
        model.addAttribute("puesto", puesto);
        return "puestos/reasignar";
    }

    @PostMapping("/reasignar/{id}")
    public String reasignarPuesto(@PathVariable String id, @RequestParam String nuevaUbicacion, Model model) {
        try {
            Puesto puestoActualizado = puestoService.reasignarPuesto(id, nuevaUbicacion);
            model.addAttribute("mensaje", "Puesto reasignado exitosamente a: " + nuevaUbicacion);
            model.addAttribute("puesto", puestoActualizado);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Puesto puesto = puestoService.obtenerPuestoPorId(id).orElse(null);
            model.addAttribute("puesto", puesto);
        }
        return "puestos/reasignar";
    }
}
