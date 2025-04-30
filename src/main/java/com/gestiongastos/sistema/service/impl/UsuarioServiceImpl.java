package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.UsuarioDTO;
import com.gestiongastos.sistema.dto.UsuarioResponseDTO;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (existePorEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setTienePresupuestoInicial(false);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (usuarioDTO.getEmail() != null && !usuario.getEmail().equals(usuarioDTO.getEmail()) 
            && existePorEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuarioDTO.getEmail());
        }

        usuario.setNombre(usuarioDTO.getNombre());
        if (usuarioDTO.getEmail() != null) {
            usuario.setEmail(usuarioDTO.getEmail());
        }
        if (usuarioDTO.getTienePresupuestoInicial() != null) {
            usuario.setTienePresupuestoInicial(usuarioDTO.getTienePresupuestoInicial());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerPorNombre(String nombre) {
        return usuarioRepository.buscarPorNombre(nombre)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.existePorEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return usuarioRepository.existePorNombre(nombre);
    }

    @Override
    @Transactional
    public void actualizarContrasena(Long id, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO marcarPresupuestoInicialConfigurado(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setTienePresupuestoInicial(true);
        return convertirAResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email);
        if (usuarioOpt.isPresent() && passwordEncoder.matches(password, usuarioOpt.get().getPassword())) {
            return Optional.of(convertirAResponseDTO(usuarioOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .map(this::convertirAResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime obtenerFechaRegistro(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return usuario.getFechaRegistro();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tienePresupuestoInicial(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return usuario.getTienePresupuestoInicial();
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalGastos(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return usuario.getGastos().stream()
                .mapToDouble(gasto -> gasto.getMonto().doubleValue())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalPresupuestos(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return usuario.getPresupuestos().stream()
                .mapToDouble(presupuesto -> presupuesto.getIngresoMensual())
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalMetasAhorro(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return usuario.getMetasAhorro().stream()
                .mapToDouble(meta -> meta.getMontoObjetivo().doubleValue())
                .sum();
    }

    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setTienePresupuestoInicial(usuario.getTienePresupuestoInicial());
        return dto;
    }
}