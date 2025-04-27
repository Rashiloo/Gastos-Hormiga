package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.MetaAhorroDTO;
import com.gestiongastos.sistema.model.MetaAhorro;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.MetaAhorroRepository;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.MetaAhorroService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MetaAhorroServiceImpl implements MetaAhorroService {

    private final MetaAhorroRepository metaAhorroRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public MetaAhorroDTO crearMetaAhorro(Long usuarioId, MetaAhorroDTO metaAhorroDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        MetaAhorro metaAhorro = new MetaAhorro();
        metaAhorro.setNombre(metaAhorroDTO.getNombre());
        metaAhorro.setDescripcion(metaAhorroDTO.getDescripcion());
        metaAhorro.setMontoObjetivo(metaAhorroDTO.getMontoObjetivo());
        metaAhorro.setMontoActual(0.0);
        metaAhorro.setFechaObjetivo(metaAhorroDTO.getFechaObjetivo());
        metaAhorro.setUsuario(usuario);

        MetaAhorro metaAhorroGuardada = metaAhorroRepository.save(metaAhorro);
        return convertirADTO(metaAhorroGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaAhorroDTO obtenerMetaAhorroPorId(Long id) {
        return metaAhorroRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("Meta de ahorro no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorroDTO> obtenerMetasAhorroPorUsuario(Long usuarioId) {
        return metaAhorroRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public MetaAhorroDTO actualizarMetaAhorro(Long id, MetaAhorroDTO metaAhorroDTO) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta de ahorro no encontrada"));

        metaAhorro.setNombre(metaAhorroDTO.getNombre());
        metaAhorro.setDescripcion(metaAhorroDTO.getDescripcion());
        metaAhorro.setMontoObjetivo(metaAhorroDTO.getMontoObjetivo());
        metaAhorro.setFechaObjetivo(metaAhorroDTO.getFechaObjetivo());

        MetaAhorro metaAhorroActualizada = metaAhorroRepository.save(metaAhorro);
        return convertirADTO(metaAhorroActualizada);
    }

    @Override
    public void eliminarMetaAhorro(Long id) {
        if (!metaAhorroRepository.existsById(id)) {
            throw new EntityNotFoundException("Meta de ahorro no encontrada");
        }
        metaAhorroRepository.deleteById(id);
    }

    @Override
    public MetaAhorroDTO actualizarMontoActual(Long id, Double montoActual) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta de ahorro no encontrada"));

        metaAhorro.setMontoActual(montoActual);
        MetaAhorro metaAhorroActualizada = metaAhorroRepository.save(metaAhorro);
        return convertirADTO(metaAhorroActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorroDTO> obtenerMetasIncompletas(Long usuarioId) {
        return metaAhorroRepository.findByUsuarioIdAndMontoActualLessThanMontoObjetivo(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularPorcentajeCompletado(Long id) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meta de ahorro no encontrada"));

        if (metaAhorro.getMontoObjetivo() == 0) {
            return 0.0;
        }

        return (metaAhorro.getMontoActual() / metaAhorro.getMontoObjetivo()) * 100;
    }

    private MetaAhorroDTO convertirADTO(MetaAhorro metaAhorro) {
        MetaAhorroDTO dto = new MetaAhorroDTO();
        dto.setId(metaAhorro.getId());
        dto.setNombre(metaAhorro.getNombre());
        dto.setDescripcion(metaAhorro.getDescripcion());
        dto.setMontoObjetivo(metaAhorro.getMontoObjetivo());
        dto.setMontoActual(metaAhorro.getMontoActual());
        dto.setFechaInicio(metaAhorro.getFechaInicio());
        dto.setFechaObjetivo(metaAhorro.getFechaObjetivo());
        dto.setPorcentajeCompletado(calcularPorcentajeCompletado(metaAhorro.getId()));
        return dto;
    }
}