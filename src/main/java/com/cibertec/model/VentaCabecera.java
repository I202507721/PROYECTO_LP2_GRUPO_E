package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_cabecera")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaCabecera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "fecha_venta", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaVenta;

    @Column(name = "total_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagado;
}