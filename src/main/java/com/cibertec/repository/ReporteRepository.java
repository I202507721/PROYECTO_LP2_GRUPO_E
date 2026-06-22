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
}
