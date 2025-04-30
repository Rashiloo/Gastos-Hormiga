package com.gestiongastos.sistema.repository;

import com.gestiongastos.sistema.model.Categoria;
import com.gestiongastos.sistema.model.Gasto;
import com.gestiongastos.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> buscarPorUsuario(Usuario usuario);
    List<Gasto> buscarPorUsuarioYCategoria(Usuario usuario, Categoria categoria);
    List<Gasto> buscarPorUsuarioYPeriodo(Usuario usuario, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT g FROM Gasto g WHERE g.usuario = :usuario AND g.tipoGasto.esGastoBase = :esGastoBase")
    List<Gasto> buscarPorUsuarioYTipoGastoEsGastoBase(@Param("usuario") Usuario usuario, @Param("esGastoBase") boolean esGastoBase);
    
    @Query("SELECT g FROM Gasto g WHERE g.usuario = :usuario AND g.tipoGasto.esGastoBase = :esGastoBase AND g.fechaGasto BETWEEN :inicio AND :fin")
    List<Gasto> buscarPorUsuarioYTipoGastoEsGastoBaseYPeriodo(
            @Param("usuario") Usuario usuario,
            @Param("esGastoBase") boolean esGastoBase,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
    
    @Query("SELECT g FROM Gasto g " +
           "WHERE g.usuario.id = :usuarioId " +
           "AND g.periodoInicio IS NOT NULL " +
           "AND g.periodoFin IS NOT NULL")
    List<Gasto> buscarRecurrentesPorUsuario(Long usuarioId);
    
    @Query("SELECT g.tipoGasto.categoria, SUM(g.monto) FROM Gasto g " +
           "WHERE g.usuario = :usuario " +
           "AND g.fechaGasto BETWEEN :inicio AND :fin " +
           "GROUP BY g.tipoGasto.categoria")
    List<Object[]> buscarGastosPorCategoriaEnPeriodo(Usuario usuario, 
                                                  LocalDate inicio, 
                                                  LocalDate fin);
                                                  
    @Query("SELECT SUM(g.monto) FROM Gasto g " +
           "WHERE g.usuario = :usuario " +
           "AND g.fechaGasto BETWEEN :inicio AND :fin")
    Double calcularTotalGastosEnPeriodo(Usuario usuario, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT SUM(g.monto) FROM Gasto g " +
           "WHERE g.usuario = :usuario " +
           "AND g.tipoGasto.categoria.id = :categoriaId " +
           "AND g.fechaGasto BETWEEN :inicio AND :fin")
    Double calcularTotalGastosPorCategoriaYPeriodo(Usuario usuario, Long categoriaId, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT SUM(g.monto) FROM Gasto g " +
           "WHERE g.usuario = :usuario " +
           "AND g.tipoGasto.esGastoBase = :esGastoBase")
    Double calcularTotalPorUsuarioYTipoGastoEsGastoBase(Usuario usuario, Boolean esGastoBase);
}