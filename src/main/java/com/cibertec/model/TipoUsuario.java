package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tipo_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuario {

    @Id
    @Column(name = "id_tipo")
    private Integer idTipo;

    @Column(nullable = false, length = 20)
    private String descripcion;
}