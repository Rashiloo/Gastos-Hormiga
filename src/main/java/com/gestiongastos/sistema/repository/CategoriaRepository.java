package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> buscarActivas();
    boolean existePorNombre(String nombre);
    List<Categoria> findByUsuarioId(Long usuarioId);
}