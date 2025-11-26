/* 

package com.ucab.estacionamiento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ucab.estacionamiento.model.interfaces.PuestoService;

@Controller
public class MainPuestoController {

    @Autowired
    private PuestoService puestoService;

    @GetMapping("/")
    public String index(Model model) {
        int total = puestoService.obtenerPuestos().size();
        int disponibles = puestoService.contarPuestosDisponibles();
        int ocupados = puestoService.contarPuestosOcupados();
        
        model.addAttribute("totalPuestos", total);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("ocupados", ocupados);
        
        return "index";
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }
}

*/