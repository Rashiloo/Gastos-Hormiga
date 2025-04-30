package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.CategoriaDTO;
import com.gestiongastos.sistema.dto.CategoriaResponseDTO;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface CategoriaService {
    CategoriaResponseDTO registrarCategoria(CategoriaDTO categoriaDTO);
    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO);
    void eliminarCategoria(Long id);
    Optional<CategoriaResponseDTO> obtenerPorId(Long id);
    List<CategoriaResponseDTO> obtenerTodas();
    List<CategoriaResponseDTO> obtenerActivas();
    Double calcularTotalGastosPorCategoria(Long categoriaId);
    Double calcularTotalGastosPorCategoriaYPeriodo(Long categoriaId, LocalDate inicio, LocalDate fin);
    
    // Métodos específicos
    List<CategoriaResponseDTO> obtenerCategoriasPorUsuario(Long usuarioId);
    boolean existePorNombreYUsuario(String nombre, Long usuarioId);
}