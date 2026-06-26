package com.cibertec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO que representa una fila del reporte de ocupación de salas
 * (resultado del procedimiento sp_reporte_ocupacion_salas).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionSalaDTO {

	private Integer idFuncion;
    private String pelicula;
    private LocalDate fecha;     
    private LocalTime horaInicio;
    private Integer numeroSala;
    private Integer capacidadTotal;
    private Integer asientosDisponibles;
    private Integer asientosVendidos;
    private BigDecimal porcentajeOcupacion;
}
