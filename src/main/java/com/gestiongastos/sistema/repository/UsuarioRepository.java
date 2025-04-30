package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorNombre(String nombre);
    boolean existePorEmail(String email);
    boolean existePorNombre(String nombre);
}