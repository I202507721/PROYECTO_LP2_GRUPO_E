package com.cibertec.repository;
import com.cibertec.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    java.util.List<Pelicula> findByEstado(Integer estado);
}