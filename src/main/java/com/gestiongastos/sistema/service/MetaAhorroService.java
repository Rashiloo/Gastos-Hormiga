package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.MetaAhorroDTO;

import java.util.List;

public interface MetaAhorroService {
    MetaAhorroDTO crearMetaAhorro(Long usuarioId, MetaAhorroDTO metaAhorroDTO);
    MetaAhorroDTO obtenerMetaAhorroPorId(Long id);
    List<MetaAhorroDTO> obtenerMetasAhorroPorUsuario(Long usuarioId);
    MetaAhorroDTO actualizarMetaAhorro(Long id, MetaAhorroDTO metaAhorroDTO);
    void eliminarMetaAhorro(Long id);
    MetaAhorroDTO actualizarMontoActual(Long id, Double montoActual);
    List<MetaAhorroDTO> obtenerMetasIncompletas(Long usuarioId);
    Double calcularPorcentajeCompletado(Long id);
}