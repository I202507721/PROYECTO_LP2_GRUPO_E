package com.cibertec.repository;

import com.cibertec.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Integer> {
    // JpaRepository ya provee todos los métodos CRUD requeridos para las salas.
}