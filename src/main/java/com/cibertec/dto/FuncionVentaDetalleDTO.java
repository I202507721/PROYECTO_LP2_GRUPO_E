package com.cibertec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionVentaDetalleDTO {
    private Integer idVenta;
    private String numVentaText;
    private String fechaText;
    private String taquillero;
    private Integer cantidadEntradas;
    private BigDecimal subtotal;
    private String asientos;
}