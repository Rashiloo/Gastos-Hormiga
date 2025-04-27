package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.GastoDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface GastoService {
    GastoDTO crearGasto(Long usuarioId, GastoDTO gastoDTO);
    GastoDTO obtenerGastoPorId(Long id);
    List<GastoDTO> obtenerGastosPorUsuario(Long usuarioId);
    GastoDTO actualizarGasto(Long id, GastoDTO gastoDTO);
    void eliminarGasto(Long id);
    List<GastoDTO> obtenerGastosPorPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    List<GastoDTO> obtenerGastosEvitables(Long usuarioId, boolean esEvitable);
    List<GastoDTO> obtenerGastosRecurrentes(Long usuarioId);
    Double calcularTotalGastosPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    Double calcularTotalGastosEvitables(Long usuarioId);
    List<GastoDTO> obtenerGastosPorCategoria(Long usuarioId, Long categoriaId);
}