package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.GastoDTO;
import com.gestiongastos.sistema.model.Categoria;
import com.gestiongastos.sistema.model.Gasto;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.CategoriaRepository;
import com.gestiongastos.sistema.repository.GastoRepository;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.GastoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public GastoDTO crearGasto(Long usuarioId, GastoDTO gastoDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(gastoDTO.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        Gasto gasto = new Gasto();
        gasto.setDescripcion(gastoDTO.getDescripcion());
        gasto.setMonto(gastoDTO.getMonto());
        gasto.setCategoria(categoria);
        gasto.setEsEvitable(gastoDTO.isEsEvitable());
        gasto.setEsRecurrente(gastoDTO.isEsRecurrente());
        gasto.setUsuario(usuario);

        Gasto gastoGuardado = gastoRepository.save(gasto);
        return convertirADTO(gastoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public GastoDTO obtenerGastoPorId(Long id) {
        return gastoRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> obtenerGastosPorUsuario(Long usuarioId) {
        return gastoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public GastoDTO actualizarGasto(Long id, GastoDTO gastoDTO) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gasto no encontrado"));

        Categoria categoria = categoriaRepository.findById(gastoDTO.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        gasto.setDescripcion(gastoDTO.getDescripcion());
        gasto.setMonto(gastoDTO.getMonto());
        gasto.setCategoria(categoria);
        gasto.setEsEvitable(gastoDTO.isEsEvitable());
        gasto.setEsRecurrente(gastoDTO.isEsRecurrente());

        Gasto gastoActualizado = gastoRepository.save(gasto);
        return convertirADTO(gastoActualizado);
    }

    @Override
    public void eliminarGasto(Long id) {
        if (!gastoRepository.existsById(id)) {
            throw new EntityNotFoundException("Gasto no encontrado");
        }
        gastoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> obtenerGastosPorPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        return gastoRepository.findByUsuarioIdAndFechaGastoBetween(usuarioId, inicio, fin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> obtenerGastosEvitables(Long usuarioId, boolean esEvitable) {
        return gastoRepository.findByUsuarioIdAndEsEvitable(usuarioId, esEvitable).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> obtenerGastosRecurrentes(Long usuarioId) {
        return gastoRepository.findByUsuarioIdAndEsRecurrente(usuarioId, true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosPeriodo(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        return gastoRepository.sumMontoByUsuarioIdAndFechaGastoBetween(usuarioId, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosEvitables(Long usuarioId) {
        return gastoRepository.sumMontoGastosEvitablesByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoDTO> obtenerGastosPorCategoria(Long usuarioId, Long categoriaId) {
        return gastoRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private GastoDTO convertirADTO(Gasto gasto) {
        GastoDTO dto = new GastoDTO();
        dto.setId(gasto.getId());
        dto.setDescripcion(gasto.getDescripcion());
        dto.setMonto(gasto.getMonto());
        dto.setFechaGasto(gasto.getFechaGasto());
        dto.setCategoriaId(gasto.getCategoria().getId());
        dto.setCategoriaNombre(gasto.getCategoria().getNombre());
        dto.setEsEvitable(gasto.isEsEvitable());
        dto.setEsRecurrente(gasto.isEsRecurrente());
        return dto;
    }
}