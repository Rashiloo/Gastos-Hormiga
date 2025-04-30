package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.CategoriaDTO;
import com.gestiongastos.sistema.dto.CategoriaResponseDTO;
import com.gestiongastos.sistema.model.Categoria;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.CategoriaRepository;
import com.gestiongastos.sistema.repository.GastoRepository;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final GastoRepository gastoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CategoriaResponseDTO registrarCategoria(CategoriaDTO categoriaDTO) {
        validarCategoriaDTO(categoriaDTO);
        
        Usuario usuario = usuarioRepository.findById(categoriaDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setColor("#000000"); // Color por defecto
        categoria.setActiva(true);
        categoria.setUsuario(usuario);

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return convertirAResponseDTO(categoriaGuardada);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        validarCategoriaDTO(categoriaDTO);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!categoria.getUsuario().getId().equals(categoriaDTO.getUsuarioId())) {
            throw new RuntimeException("No tiene permiso para modificar esta categoría");
        }

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return convertirAResponseDTO(categoriaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!categoria.getTiposGasto().isEmpty()) {
            throw new RuntimeException("No se puede eliminar una categoría que tiene tipos de gasto asociados");
        }

        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaResponseDTO> obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerActivas() {
        return categoriaRepository.buscarActivas().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosPorCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return gastoRepository.buscarPorUsuarioYCategoria(categoria.getUsuario(), categoria).stream()
                .mapToDouble(gasto -> gasto.getMonto())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosPorCategoriaYPeriodo(Long categoriaId, LocalDate inicio, LocalDate fin) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return gastoRepository.buscarPorUsuarioYPeriodo(categoria.getUsuario(), inicio, fin).stream()
                .filter(gasto -> gasto.getTipoGasto().getCategoria().equals(categoria))
                .mapToDouble(gasto -> gasto.getMonto())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerCategoriasPorUsuario(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreYUsuario(String nombre, Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId).stream()
                .anyMatch(categoria -> categoria.getNombre().equals(nombre));
    }

    private CategoriaResponseDTO convertirAResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setUsuarioId(categoria.getUsuario().getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setColor(categoria.getColor());
        dto.setActiva(categoria.getActiva());
        dto.setCantidadTiposGasto(categoria.getTiposGasto().size());
        dto.setTotalGastado(calcularTotalGastosPorCategoria(categoria.getId()));
        return dto;
    }

    private void validarCategoriaDTO(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null) {
            throw new RuntimeException("La categoría no puede ser nula");
        }
        if (categoriaDTO.getUsuarioId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }
        if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
    }
}