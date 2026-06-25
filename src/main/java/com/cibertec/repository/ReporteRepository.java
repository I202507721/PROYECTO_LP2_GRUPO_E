package com.cibertec.repository;

import com.cibertec.dto.OcupacionSalaDTO;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para el procedimiento sp_reporte_ocupacion_salas, cuyo
 * resultado combina columnas de varias tablas (función, película, sala)
 * más un campo calculado (% de ocupación), por lo que no corresponde a
 * una única entidad JPA. Se usa JdbcClient (Spring 6 / Boot 3) para
 * invocarlo directamente con CALL y mapear cada fila a su DTO.
 */
@Repository
public class ReporteRepository {

    private final JdbcClient jdbcClient;

    public ReporteRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // CU09: Reporte de ocupación de salas (funciones del día actual)
    public List<OcupacionSalaDTO> obtenerOcupacionSalas() {
        return jdbcClient.sql("CALL sp_reporte_ocupacion_salas()")
                .query((rs, rowNum) -> new OcupacionSalaDTO(
                        rs.getInt("id_funcion"),
                        rs.getString("pelicula"),
                        rs.getInt("numero_sala"),
                        rs.getInt("capacidad_total"),
                        rs.getInt("asientos_disponibles"),
                        rs.getInt("asientos_vendidos"),
                        rs.getBigDecimal("porcentaje_ocupacion")
                ))
                .list();
    }
 // Consulta Maestro-Detalle: Ventas hechas para una función específica
    public List<com.cibertec.dto.FuncionVentaDetalleDTO> obtenerVentasPorFuncion(Integer idFuncion) {
        String sql = """
            SELECT 
                vc.id_venta,
                CONCAT('V001 - ', LPAD(vc.id_venta, 8, '0')) AS numVentaText,
                DATE_FORMAT(vc.fecha_venta, '%d/%m/%Y %h:%i:%s %p') AS fechaText,
                u.nombre AS taquillero,
                vd.cantidad_entradas,
                vd.subtotal,
                (SELECT GROUP_CONCAT(ao.codigo_asiento SEPARATOR ', ') 
                 FROM asiento_ocupado ao 
                 WHERE ao.id_venta = vc.id_venta AND ao.id_funcion = vd.id_funcion) AS asientos
            FROM venta_detalle vd
            INNER JOIN venta_cabecera vc ON vd.id_venta = vc.id_venta
            INNER JOIN usuario u ON vc.id_usuario = u.id_usuario
            WHERE vd.id_funcion = ?
            ORDER BY vc.id_venta DESC
        """;
        
        return jdbcClient.sql(sql)
                .param(idFuncion)
                .query((rs, rowNum) -> new com.cibertec.dto.FuncionVentaDetalleDTO(
                        rs.getInt("id_venta"),
                        rs.getString("numVentaText"),
                        rs.getString("fechaText"),
                        rs.getString("taquillero"),
                        rs.getInt("cantidad_entradas"),
                        rs.getBigDecimal("subtotal"),
                        rs.getString("asientos")
                ))
                .list();
    }
}
