package com.cibertec.service;

import org.springframework.stereotype.Service;

import com.cibertec.dto.AutenticacionFilter;
import com.cibertec.model.Usuario;
import com.cibertec.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacionService {
    private final UsuarioRepository usuarioRepository;
    
    public Usuario login(AutenticacionFilter filter) {
        return usuarioRepository.findByUsernameAndPassword(filter.getUsername(), filter.getPassword());
    }
}