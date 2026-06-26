package com.cibertec.repository;

import com.cibertec.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Usuario findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username); 
}