package com.cibertec.repository;

import com.cibertec.dto.OcupacionSalaDTO;
import com.cibertec.dto.ReporteFiltroDTO;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para el procedimiento sp_reporte_ocupacion_salas, cuyo resultado
 * combina columnas de varias tablas (función, película, sala) más un campo
 * calculado (% de ocupación), por lo que no corresponde a una única entidad
 * JPA. Se usa JdbcClient (Spring 6 / Boot 3) para invocarlo directamente con
 * CALL y mapear cada fila a su DTO.
 */
@Repository
public class ReporteRepository {

	private final JdbcClient jdbcClient;

	public ReporteRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	// CU09: Reporte de ocupación de salas (funciones del día actual)
	public List<OcupacionSalaDTO> obtenerOcupacionSalas(ReporteFiltroDTO filter) {
		StringBuilder sql = new StringBuilder("""
				    SELECT
				        f.id_funcion,
				        p.titulo AS pelicula,
				        f.fecha,
				        f.hora_inicio,
				        s.numero_sala,
				        s.capacidad AS capacidad_total,
				        f.asientos_disponibles,
				        (s.capacidad - f.asientos_disponibles) AS asientos_vendidos,
				        ROUND(((s.capacidad - f.asientos_disponibles) / s.capacidad) * 100, 2) AS porcentaje_ocupacion
				    FROM funcion f
				    INNER JOIN pelicula p ON f.id_pelicula = p.id_pelicula
				    INNER JOIN sala s ON f.id_sala = s.id_sala
				    WHERE 1=1
				""");

		if (filter.getFechaInicio() != null) {
			sql.append(" AND f.fecha >= :fechaInicio ");
		}
		if (filter.getFechaFin() != null) {
			sql.append(" AND f.fecha <= :fechaFin ");
		}
		if (filter.getTituloPelicula() != null && !filter.getTituloPelicula().trim().isEmpty()) {
			sql.append(" AND p.titulo LIKE :titulo ");
		}

		sql.append(" ORDER BY f.fecha DESC, f.hora_inicio DESC");

		var query = jdbcClient.sql(sql.toString());

		if (filter.getFechaInicio() != null)
			query.param("fechaInicio", filter.getFechaInicio());
		if (filter.getFechaFin() != null)
			query.param("fechaFin", filter.getFechaFin());
		if (filter.getTituloPelicula() != null && !filter.getTituloPelicula().trim().isEmpty()) {
			query.param("titulo", "%" + filter.getTituloPelicula().trim() + "%");
		}

		return query.query((rs, rowNum) -> new OcupacionSalaDTO(rs.getInt("id_funcion"), rs.getString("pelicula"),
				rs.getObject("fecha", java.time.LocalDate.class),
				rs.getObject("hora_inicio", java.time.LocalTime.class),
				rs.getInt("numero_sala"), 
				rs.getInt("capacidad_total"), 
				rs.getInt("asientos_disponibles"),
				rs.getInt("asientos_vendidos"), 
				rs.getBigDecimal("porcentaje_ocupacion"))).list();
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

		return jdbcClient.sql(sql).param(idFuncion)
				.query((rs, rowNum) -> new com.cibertec.dto.FuncionVentaDetalleDTO(rs.getInt("id_venta"),
						rs.getString("numVentaText"), rs.getString("fechaText"), rs.getString("taquillero"),
						rs.getInt("cantidad_entradas"), rs.getBigDecimal("subtotal"), rs.getString("asientos")))
				.list();
	}
}
