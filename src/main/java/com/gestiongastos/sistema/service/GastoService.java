package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.GastoDTO;
import com.gestiongastos.sistema.dto.GastoResponseDTO;
import com.gestiongastos.sistema.dto.UsuarioDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GastoService {
    // Métodos CRUD básicos
    GastoResponseDTO registrarGasto(GastoDTO gastoDTO, UsuarioDTO usuarioDTO);
    GastoResponseDTO actualizarGasto(Long id, GastoDTO gastoDTO, UsuarioDTO usuarioDTO);
    void eliminarGasto(Long id, UsuarioDTO usuarioDTO);
    Optional<GastoResponseDTO> obtenerPorId(Long id);
    List<GastoResponseDTO> obtenerGastosUsuario(UsuarioDTO usuarioDTO);
    List<GastoResponseDTO> obtenerGastosPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin);

    // Métodos específicos para gastos hormiga
    List<GastoResponseDTO> obtenerGastosHormiga(UsuarioDTO usuarioDTO);
    List<GastoResponseDTO> obtenerGastosHormigaPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin);
    Double calcularTotalGastosHormiga(UsuarioDTO usuarioDTO);
    Double calcularTotalGastosHormigaPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin);

    // Métodos para gastos base
    List<GastoResponseDTO> obtenerGastosBase(UsuarioDTO usuarioDTO);
    List<GastoResponseDTO> obtenerGastosBasePorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin);
    Double calcularTotalGastosBase(UsuarioDTO usuarioDTO);
    Double calcularTotalGastosBasePorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin);
}