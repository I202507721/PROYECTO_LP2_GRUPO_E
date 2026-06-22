package com.cibertec.controller;

import com.cibertec.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/cine")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteRepository reporteRepository;

    // ==========================================
    // REPORTE: OCUPACIÓN DE SALAS (CU09)
    // ==========================================
    @GetMapping("/reporte/ocupacion")
    public String reporteOcupacion(Model model) {
        log.info("CU09: Generando reporte de ocupación de salas del día.");
        model.addAttribute("ocupaciones", reporteRepository.obtenerOcupacionSalas());
        return "reporte/ocupacion";
    }
}
