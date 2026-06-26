package com.cibertec.repository;

import com.cibertec.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Integer> {

    // CU04: Lista las funciones programadas a partir de una fecha específica (ej: Hoy)
    List<Funcion> findByFechaGreaterThanEqual(LocalDate fecha);

    // Busca funciones activas por el ID de la película
    @Query("SELECT f FROM Funcion f WHERE f.pelicula.idPelicula = :idPelicula AND f.fecha >= CURRENT_DATE")
    List<Funcion> findActivasByPelicula(@Param("idPelicula") Integer idPelicula);
    
    // CU06: Invocación directa y segura al Stored Procedure transaccional de MySQL
    @Procedure(procedureName = "sp_registrar_venta_completa")
    void registrarVentaCompleta(
        @Param("p_id_usuario") Integer idUsuario,
        @Param("p_id_funcion") Integer idFuncion,
        @Param("p_cantidad_entradas") Integer cantidadEntradas,
        @Param("p_asientos_codigos") String asientosCodigos
    );
}