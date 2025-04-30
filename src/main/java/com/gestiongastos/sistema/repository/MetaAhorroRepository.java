package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.MetaAhorro;
import com.gestiongastos.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> buscarPorUsuario(Usuario usuario);
    List<MetaAhorro> buscarPorUsuarioYEstado(Usuario usuario, MetaAhorro.EstadoMeta estado);
    
    @Query("SELECT m FROM MetaAhorro m WHERE m.usuario = :usuario " +
           "AND m.estado = 'ACTIVA' " +
           "AND :fecha BETWEEN m.fechaInicio AND m.fechaFin")
    List<MetaAhorro> buscarMetasActivasEnFecha(Usuario usuario, LocalDate fecha);
    
    @Query("SELECT SUM(m.totalGastosHormigaPeriodo) FROM MetaAhorro m " +
           "WHERE m.usuario = :usuario AND m.estado = 'ACTIVA'")
    Double buscarTotalGastosHormigaActivos(Usuario usuario);
    
    @Query("SELECT COALESCE(SUM(ge.monto), 0) FROM GastoEvitado ge " +
           "WHERE ge.meta.id = :metaId")
    Double buscarTotalAhorradoPorMeta(Long metaId);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM Gasto g " +
           "INNER JOIN g.tipoGasto tg " +
           "WHERE g.usuario = :usuario " +
           "AND g.fechaGasto BETWEEN :inicio AND :fin " +
           "AND tg.esGastoBase = false")
    Double calcularTotalGastosHormigaPeriodo(
            @Param("usuario") Usuario usuario,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
}