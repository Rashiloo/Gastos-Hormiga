package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.MetaAhorroDTO;
import com.gestiongastos.sistema.dto.MetaAhorroResponseDTO;
import com.gestiongastos.sistema.dto.UsuarioDTO;
import com.gestiongastos.sistema.model.GastoEvitado;
import com.gestiongastos.sistema.model.MetaAhorro;
import com.gestiongastos.sistema.model.TipoGasto;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.GastoEvitadoRepository;
import com.gestiongastos.sistema.repository.MetaAhorroRepository;
import com.gestiongastos.sistema.repository.TipoGastoRepository;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.MetaAhorroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetaAhorroServiceImpl implements MetaAhorroService {

    private final MetaAhorroRepository metaAhorroRepository;
    private final TipoGastoRepository tipoGastoRepository;
    private final GastoEvitadoRepository gastoEvitadoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public MetaAhorroResponseDTO registrarMetaAhorro(MetaAhorroDTO metaAhorroDTO, UsuarioDTO usuarioDTO) {
        validarUsuario(usuarioDTO);
        validarFechasMeta(metaAhorroDTO.getFechaInicio(), metaAhorroDTO.getFechaFin());
        validarMontoObjetivo(metaAhorroDTO.getMontoObjetivo());

        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el monto objetivo no exceda el total de gastos hormiga del período
        Double totalGastosHormiga = metaAhorroRepository.calcularTotalGastosHormigaPeriodo(
                usuario,
                metaAhorroDTO.getFechaInicio().minusMonths(1),
                metaAhorroDTO.getFechaInicio()
        );

        if (metaAhorroDTO.getMontoObjetivo() > totalGastosHormiga) {
            throw new RuntimeException("El monto objetivo no puede ser mayor que el total de gastos hormiga del período anterior");
        }

        MetaAhorro metaAhorro = new MetaAhorro();
        metaAhorro.setUsuario(usuario);
        metaAhorro.setNombre(metaAhorroDTO.getNombre());
        metaAhorro.setMontoObjetivo(metaAhorroDTO.getMontoObjetivo());
        metaAhorro.setFechaInicio(metaAhorroDTO.getFechaInicio());
        metaAhorro.setFechaFin(metaAhorroDTO.getFechaFin());
        metaAhorro.setEstado(MetaAhorro.EstadoMeta.ACTIVA);
        metaAhorro.setTotalGastosHormigaPeriodo(totalGastosHormiga);
        metaAhorro.setFechaRegistro(LocalDateTime.now());

        MetaAhorro metaAhorroGuardada = metaAhorroRepository.save(metaAhorro);
        return convertirAResponseDTO(metaAhorroGuardada);
    }

    @Override
    @Transactional
    public MetaAhorroResponseDTO actualizarMetaAhorro(Long id, MetaAhorroDTO metaAhorroDTO, UsuarioDTO usuarioDTO) {
        validarUsuario(usuarioDTO);
        validarFechasMeta(metaAhorroDTO.getFechaInicio(), metaAhorroDTO.getFechaFin());
        validarMontoObjetivo(metaAhorroDTO.getMontoObjetivo());

        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MetaAhorro metaAhorro = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        if (!metaAhorro.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permiso para modificar esta meta de ahorro");
        }

        if (metaAhorro.getEstado() != MetaAhorro.EstadoMeta.ACTIVA) {
            throw new RuntimeException("No se puede modificar una meta que no está activa");
        }

        // Validar que el monto objetivo no exceda el total de gastos hormiga del período
        Double totalGastosHormiga = metaAhorroRepository.calcularTotalGastosHormigaPeriodo(
                usuario,
                metaAhorroDTO.getFechaInicio().minusMonths(1),
                metaAhorroDTO.getFechaInicio()
        );

        if (metaAhorroDTO.getMontoObjetivo() > totalGastosHormiga) {
            throw new RuntimeException("El monto objetivo no puede ser mayor que el total de gastos hormiga del período anterior");
        }

        metaAhorro.setNombre(metaAhorroDTO.getNombre());
        metaAhorro.setMontoObjetivo(metaAhorroDTO.getMontoObjetivo());
        metaAhorro.setFechaInicio(metaAhorroDTO.getFechaInicio());
        metaAhorro.setFechaFin(metaAhorroDTO.getFechaFin());
        metaAhorro.setTotalGastosHormigaPeriodo(totalGastosHormiga);

        MetaAhorro metaAhorroActualizada = metaAhorroRepository.save(metaAhorro);
        return convertirAResponseDTO(metaAhorroActualizada);
    }

    @Override
    @Transactional
    public void eliminarMetaAhorro(Long id, UsuarioDTO usuarioDTO) {
        validarUsuario(usuarioDTO);

        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MetaAhorro metaAhorro = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        if (!metaAhorro.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permiso para eliminar esta meta de ahorro");
        }

        if (metaAhorro.getEstado() != MetaAhorro.EstadoMeta.ACTIVA) {
            throw new RuntimeException("No se puede eliminar una meta que no está activa");
        }

        metaAhorroRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MetaAhorroResponseDTO> obtenerPorId(Long id) {
        return metaAhorroRepository.findById(id)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorroResponseDTO> obtenerMetasAhorroUsuario(UsuarioDTO usuarioDTO) {
        validarUsuario(usuarioDTO);
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return metaAhorroRepository.buscarPorUsuario(usuario).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetaAhorroResponseDTO> obtenerMetasAhorroActivas(UsuarioDTO usuarioDTO) {
        validarUsuario(usuarioDTO);
        Usuario usuario = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return metaAhorroRepository.buscarPorUsuarioYEstado(usuario, MetaAhorro.EstadoMeta.ACTIVA).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularProgresoMetaAhorro(Long metaId) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        Double totalAhorrado = metaAhorroRepository.buscarTotalAhorradoPorMeta(metaId);

        return (totalAhorrado / metaAhorro.getMontoObjetivo()) * 100;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularAhorroDiarioNecesario(Long metaId) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        Double totalAhorrado = metaAhorroRepository.buscarTotalAhorradoPorMeta(metaId);

        Double montoRestante = metaAhorro.getMontoObjetivo() - totalAhorrado;
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), metaAhorro.getFechaFin());

        if (diasRestantes <= 0) {
            return 0.0;
        }

        return montoRestante / diasRestantes;
    }

    @Override
    @Transactional
    public void registrarGastoEvitado(Long metaId, Long tipoGastoId, Double monto, String descripcion, String evidenciaAhorro) {
        validarMontoGastoEvitado(monto);
        validarDescripcionGastoEvitado(descripcion);

        MetaAhorro metaAhorro = metaAhorroRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        if (metaAhorro.getEstado() != MetaAhorro.EstadoMeta.ACTIVA) {
            throw new RuntimeException("No se pueden registrar gastos evitados en una meta que no está activa");
        }

        TipoGasto tipoGasto = tipoGastoRepository.findById(tipoGastoId)
                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado"));

        if (tipoGasto.getEsGastoBase()) {
            throw new RuntimeException("No se pueden registrar gastos base como evitados");
        }

        GastoEvitado gastoEvitado = new GastoEvitado();
        gastoEvitado.setMeta(metaAhorro);
        gastoEvitado.setTipoGasto(tipoGasto);
        gastoEvitado.setMonto(monto);
        gastoEvitado.setDescripcion(descripcion);
        gastoEvitado.setEvidenciaAhorro(evidenciaAhorro);
        gastoEvitado.setFechaRegistro(LocalDate.now());

        gastoEvitadoRepository.save(gastoEvitado);
        verificarMetaCompletada(metaId);
    }

    @Override
    @Transactional
    public void verificarMetaCompletada(Long metaId) {
        MetaAhorro metaAhorro = metaAhorroRepository.findById(metaId)
                .orElseThrow(() -> new RuntimeException("Meta de ahorro no encontrada"));

        Double totalAhorrado = metaAhorroRepository.buscarTotalAhorradoPorMeta(metaId);

        if (totalAhorrado >= metaAhorro.getMontoObjetivo()) {
            metaAhorro.setEstado(MetaAhorro.EstadoMeta.COMPLETADA);
            metaAhorroRepository.save(metaAhorro);
        }
    }

    /**
     * Convierte una entidad MetaAhorro a su DTO de respuesta.
     * 
     * @param metaAhorro La entidad MetaAhorro a convertir
     * @return El DTO de respuesta con todos los campos calculados
     */
    private MetaAhorroResponseDTO convertirAResponseDTO(MetaAhorro metaAhorro) {
        MetaAhorroResponseDTO dto = new MetaAhorroResponseDTO();
        dto.setId(metaAhorro.getId());
        dto.setUsuarioId(metaAhorro.getUsuario().getId());
        dto.setNombreUsuario(metaAhorro.getUsuario().getNombre());
        dto.setNombre(metaAhorro.getNombre());
        dto.setMontoObjetivo(metaAhorro.getMontoObjetivo());
        dto.setFechaInicio(metaAhorro.getFechaInicio());
        dto.setFechaFin(metaAhorro.getFechaFin());
        dto.setEstado(metaAhorro.getEstado().toString());
        dto.setTotalGastosHormigaPeriodo(metaAhorro.getTotalGastosHormigaPeriodo());
        dto.setFechaRegistro(metaAhorro.getFechaRegistro());

        // Calcular campos adicionales
        Double totalAhorrado = metaAhorroRepository.buscarTotalAhorradoPorMeta(metaAhorro.getId());
        dto.setMontoActual(totalAhorrado);
        dto.setPorcentajeCompletado((totalAhorrado / metaAhorro.getMontoObjetivo()) * 100);
        return dto;
    }

    /**
     * Valida que el usuario exista y esté activo.
     * 
     * @param usuarioDTO El DTO del usuario a validar
     * @throws RuntimeException si el usuario no existe o no está activo
     */
    private void validarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null || usuarioDTO.getId() == null) {
            throw new RuntimeException("El usuario no puede ser nulo");
        }
    }

    /**
     * Valida las fechas de una meta de ahorro.
     * 
     * @param fechaInicio La fecha de inicio
     * @param fechaFin La fecha de fin
     * @throws RuntimeException si las fechas no son válidas
     */
    private void validarFechasMeta(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
    }

    /**
     * Valida el monto objetivo de una meta de ahorro.
     * 
     * @param montoObjetivo El monto objetivo a validar
     * @throws RuntimeException si el monto no es válido
     */
    private void validarMontoObjetivo(Double montoObjetivo) {
        if (montoObjetivo == null || montoObjetivo <= 0) {
            throw new RuntimeException("El monto objetivo debe ser mayor que cero");
        }
    }

    /**
     * Valida el monto de un gasto evitado.
     * 
     * @param monto El monto a validar
     * @throws RuntimeException si el monto no es válido
     */
    private void validarMontoGastoEvitado(Double monto) {
        if (monto == null || monto <= 0) {
            throw new RuntimeException("El monto del gasto evitado debe ser mayor que cero");
        }
    }

    /**
     * Valida la descripción de un gasto evitado.
     * 
     * @param descripcion La descripción a validar
     * @throws RuntimeException si la descripción no es válida
     */
    private void validarDescripcionGastoEvitado(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new RuntimeException("La descripción del gasto evitado no puede estar vacía");
        }
    }
}