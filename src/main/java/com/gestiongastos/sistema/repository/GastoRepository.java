package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByUsuarioId(Long usuarioId);

    List<Gasto> findByUsuarioIdAndFechaGastoBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    List<Gasto> findByUsuarioIdAndEsEvitable(Long usuarioId, boolean esEvitable);

    List<Gasto> findByUsuarioIdAndEsRecurrente(Long usuarioId, boolean esRecurrente);

    List<Gasto> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE g.usuario.id = ?1 AND g.fechaGasto BETWEEN ?2 AND ?3")
    Double sumMontoByUsuarioIdAndFechaGastoBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE g.usuario.id = ?1 AND g.esEvitable = true")
    Double sumMontoGastosEvitablesByUsuarioId(Long usuarioId);
}