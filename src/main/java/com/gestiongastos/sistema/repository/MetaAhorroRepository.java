package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.MetaAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuarioId(Long usuarioId);

    List<MetaAhorro> findByUsuarioIdAndFechaObjetivoBefore(Long usuarioId, LocalDate fecha);

    @Query("SELECT m FROM MetaAhorro m WHERE m.usuario.id = :usuarioId AND m.montoActual < m.montoObjetivo")
    List<MetaAhorro> findByUsuarioIdAndMontoActualLessThanMontoObjetivo(@Param("usuarioId") Long usuarioId);
}