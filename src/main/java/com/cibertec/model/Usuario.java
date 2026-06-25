package com.cibertec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(columnDefinition = "BIT(1) DEFAULT b'1'")
    private Boolean activo;
    
    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private TipoUsuario tipoUsuario;

    public String getRol() {
        return tipoUsuario != null ? tipoUsuario.getDescripcion() : "Sin Rol";
    }
}