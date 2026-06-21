package com.cibertec.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cibertec.model.Pelicula;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    java.util.List<Pelicula> findByEstado(Integer estado);
    
 //  procedimiento almacenado de filtrado 
    @Query(value = "CALL sp_filtrar_pelicula_por_genero(:p_genero)", nativeQuery = true)
    List<Pelicula> filtrarPorGenero(@Param("p_genero") String genero);
}