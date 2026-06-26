package com.cibertec.controller;

import com.cibertec.dto.ReporteFiltroDTO;
import com.cibertec.repository.FuncionRepository;
import com.cibertec.repository.ReporteRepository;
import com.cibertec.service.ReporteJasperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	private final FuncionRepository funcionRepository;

	// ==========================================
	// REPORTE: OCUPACIÓN DE SALAS (CU09 - HTML)
	// ==========================================
	@GetMapping("/reporte/ocupacion")
	public String reporteOcupacion(@ModelAttribute ReporteFiltroDTO filter, Model model) {
		log.info("CU09: Generando reporte de ocupación de salas con filtros aplicados.");

		model.addAttribute("ocupaciones", reporteRepository.obtenerOcupacionSalas(filter));


		model.addAttribute("filter", filter);
		return "reporte/ocupacion";
	}

	//ENDPOINT: Detalle de ventas por función seleccionada
	@GetMapping("/reporte/funcion-ventas")
	public String verVentasPorFuncion(@RequestParam("idFuncion") Integer idFuncion, Model model) {
		log.info("Cargando detalle de boletas de la función ID: {}", idFuncion);

		// Obtenemos los datos de la función (Película, Sala, Horario) de forma segura
		var funcion = funcionRepository.findById(idFuncion).orElseThrow();

		model.addAttribute("funcion", funcion);
		model.addAttribute("ventas", reporteRepository.obtenerVentasPorFuncion(idFuncion));
		return "reporte/ventas_funcion";
	}

	// Impresión de Ticket PDF con Jaspersoft
	@org.springframework.web.bind.annotation.ResponseBody 
    @GetMapping("/reporte/boleta")
    public void imprimirBoleta(@RequestParam("idVenta") Integer idVenta,
                               jakarta.servlet.http.HttpServletResponse response) throws Exception {
        String reportPath = "/reporte/boleta.jrxml"; // O la ruta que te haya funcionado
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        
        //Debe coincidir con Jaspersoft
        params.put("pNumBoleta", idVenta); 

        net.sf.jasperreports.engine.JasperPrint jasperPrint = reporteJasperService.getJasperPrint(params, reportPath);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("inline; filename=Ticket-Venta-%s.pdf", idVenta));

        java.io.OutputStream outputStream = response.getOutputStream();
        net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}