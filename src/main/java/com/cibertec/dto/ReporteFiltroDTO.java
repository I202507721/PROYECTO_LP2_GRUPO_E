package com.cibertec.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReporteFiltroDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tituloPelicula;
}