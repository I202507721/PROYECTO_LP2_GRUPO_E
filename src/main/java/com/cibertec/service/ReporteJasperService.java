package com.cibertec.service;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ReporteJasperService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JasperPrint getJasperPrint(Map<String, Object> params, String reportPath) throws Exception {
        Connection conn = jdbcTemplate.getDataSource().getConnection();

        try {
            // Establece la región del reporte en español de Perú para formatos de moneda y fechas
            params.put(JRParameter.REPORT_LOCALE, new Locale("es", "PE"));
            
            // Carga el archivo .jrxml desde el classpath (resources)
            InputStream reportStream = this.getClass().getResourceAsStream(reportPath);
            if (reportStream == null) {
                throw new IllegalArgumentException("No se encontró el archivo del reporte en la ruta: " + reportPath);
            }
            
            // Compila el archivo .jrxml a un objeto JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Llena el reporte con los parámetros y la conexión a la base de datos
            return JasperFillManager.fillReport(jasperReport, params, conn);
        } finally {
            // Asegura el cierre de la conexión a la base de datos
            conn.close();
        }
    }
}