package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.GastoDTO;
import com.gestiongastos.sistema.dto.GastoResponseDTO;
import com.gestiongastos.sistema.dto.TipoGastoDTO;
import com.gestiongastos.sistema.dto.UsuarioDTO;
import com.gestiongastos.sistema.model.Gasto;
import com.gestiongastos.sistema.model.TipoGasto;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.GastoRepository;
import com.gestiongastos.sistema.repository.TipoGastoRepository;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.GastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final TipoGastoRepository tipoGastoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public GastoResponseDTO registrarGasto(GastoDTO gastoDTO, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Gasto gasto = new Gasto();
        gasto.setUsuario(usuario);
        gasto.setMonto(gastoDTO.getMonto());
        gasto.setDescripcion(gastoDTO.getDescripcion());
        gasto.setFechaGasto(gastoDTO.getFechaGasto());
        gasto.setFechaRegistro(LocalDateTime.now());

        TipoGasto tipoGasto = tipoGastoRepository.findById(gastoDTO.getTipoGastoId())
                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado"));
        gasto.setTipoGasto(tipoGasto);

        Gasto gastoGuardado = gastoRepository.save(gasto);
        return convertirAResponseDTO(gastoGuardado);
    }

    @Override
    @Transactional
    public GastoResponseDTO actualizarGasto(Long id, GastoDTO gastoDTO, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permiso para modificar este gasto");
        }

        gasto.setMonto(gastoDTO.getMonto());
        gasto.setDescripcion(gastoDTO.getDescripcion());
        gasto.setFechaGasto(gastoDTO.getFechaGasto());

        TipoGasto tipoGasto = tipoGastoRepository.findById(gastoDTO.getTipoGastoId())
                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado"));
        gasto.setTipoGasto(tipoGasto);

        Gasto gastoActualizado = gastoRepository.save(gasto);
        return convertirAResponseDTO(gastoActualizado);
    }

    @Override
    @Transactional
    public void eliminarGasto(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado"));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permiso para eliminar este gasto");
        }

        gastoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GastoResponseDTO> obtenerPorId(Long id) {
        return gastoRepository.findById(id)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuario(usuario).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYPeriodo(usuario, inicio, fin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosHormiga(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBase(usuario, false).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosHormigaPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBaseYPeriodo(usuario, false, inicio, fin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosHormiga(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.calcularTotalPorUsuarioYTipoGastoEsGastoBase(usuario, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosHormigaPorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBaseYPeriodo(usuario, false, inicio, fin).stream()
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosBase(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBase(usuario, true).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastosBasePorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBaseYPeriodo(usuario, true, inicio, fin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosBase(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.calcularTotalPorUsuarioYTipoGastoEsGastoBase(usuario, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalGastosBasePorPeriodo(UsuarioDTO usuarioDTO, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoRepository.buscarPorUsuarioYTipoGastoEsGastoBaseYPeriodo(usuario, true, inicio, fin).stream()
                .mapToDouble(Gasto::getMonto)
                .sum();
    }

    private GastoResponseDTO convertirAResponseDTO(Gasto gasto) {
        GastoResponseDTO dto = new GastoResponseDTO();
        dto.setId(gasto.getId());
        dto.setMonto(gasto.getMonto());
        dto.setDescripcion(gasto.getDescripcion());
        dto.setFechaGasto(gasto.getFechaGasto());
        dto.setFechaRegistro(gasto.getFechaRegistro().toLocalDate());
        
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(gasto.getUsuario().getId());
        usuarioDTO.setNombre(gasto.getUsuario().getNombre());
        dto.setUsuario(usuarioDTO);
        
        TipoGastoDTO tipoGastoDTO = new TipoGastoDTO();
        tipoGastoDTO.setCategoriaId(gasto.getTipoGasto().getCategoria().getId());
        tipoGastoDTO.setNombre(gasto.getTipoGasto().getNombre());
        tipoGastoDTO.setDescripcion(gasto.getTipoGasto().getDescripcion());
        tipoGastoDTO.setEsGastoBase(gasto.getTipoGasto().getEsGastoBase());
        tipoGastoDTO.setFrecuencia(gasto.getTipoGasto().getFrecuencia());
        dto.setTipoGasto(tipoGastoDTO);
        
        return dto;
    }
}