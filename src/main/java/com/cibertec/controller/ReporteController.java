package com.cibertec.controller;

import com.cibertec.repository.ReporteRepository;
import com.cibertec.service.ReporteJasperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/cine")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteRepository reporteRepository;
    private final ReporteJasperService reporteJasperService;

    // ==========================================
    // REPORTE: OCUPACIÓN DE SALAS (CU09 - HTML)
    // ==========================================
    @GetMapping("/reporte/ocupacion")
    public String reporteOcupacion(Model model) {
        log.info("CU09: Generando reporte de ocupación de salas del día.");
        model.addAttribute("ocupaciones", reporteRepository.obtenerOcupacionSalas());
        return "reporte/ocupacion";
    }

    // ==========================================
    // REPORTE: TICKET DE ENTRADA (JASPER - PDF)
    // ==========================================
    @GetMapping("/reporte/ticket")
    public void imprimirTicket(@RequestParam("idVenta") Integer idVenta, HttpServletResponse response) throws Exception {
        log.info("Generando Ticket PDF para la venta ID: {}", idVenta);

        // Ruta del diseño dentro de src/main/resources
        String reportPath = "/reporte/ticket_venta.jrxml";

        // Mapeo del parámetro definido en Jaspersoft Studio
        Map<String, Object> params = new HashMap<>();
        params.put("p_id_venta", idVenta);
        
        // Generación del objeto imprimible
        JasperPrint jasperPrint = reporteJasperService.getJasperPrint(params, reportPath);

        // Configuración de las cabeceras de respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("inline; filename=Ticket-Venta-%s.pdf", idVenta));

        // Transmisión del flujo de datos del PDF directamente al navegador
        OutputStream outputStream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        outputStream.flush();
        outputStream.close();
    }
}