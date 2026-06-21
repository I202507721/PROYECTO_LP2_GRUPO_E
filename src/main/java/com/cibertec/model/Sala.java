package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "sala")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;

    @Column(name = "numero_sala", nullable = false, unique = true)
    private Integer numeroSala;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(name = "tipo_proyeccion", nullable = false, length = 10)
    private String tipoProyeccion;
}