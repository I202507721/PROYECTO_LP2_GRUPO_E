package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pelicula")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer idPelicula;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 50)
    private String genero;

    @Column(nullable = false)
    private Integer duracion;

    @Column(nullable = false, length = 10)
    private String clasificacion;

    @Column(columnDefinition = "BIT(1) DEFAULT b'1'")
    private Boolean estado; 
}