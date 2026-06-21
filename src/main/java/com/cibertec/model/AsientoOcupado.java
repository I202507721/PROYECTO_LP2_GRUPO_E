package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asiento_ocupado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoOcupado {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asiento")
    private Integer idAsiento;

    @Column(name = "id_funcion")
    private Integer idFuncion;

    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "codigo_asiento", nullable = false, length = 5)
    private String codigoAsiento;
}