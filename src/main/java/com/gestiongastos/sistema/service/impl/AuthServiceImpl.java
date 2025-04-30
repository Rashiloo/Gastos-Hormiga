package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.JwtDTO;
import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.dto.RegisterDTO;
import com.gestiongastos.sistema.dto.UsuarioResponseDTO;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.security.UsuarioDetailsAdapter;
import com.gestiongastos.sistema.service.AuthService;
import com.gestiongastos.sistema.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public JwtDTO iniciarSesion(LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.buscarPorEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return jwtService.generateToken(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(RegisterDTO registerDTO) {
        if (usuarioRepository.existePorEmail(registerDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registerDTO.getNombre());
        usuario.setEmail(registerDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());

        return convertToResponseDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void cerrarSesion(JwtDTO jwtDTO) {
        // La invalidación del token se maneja en el cliente
        // No es necesario hacer nada aquí ya que JWT es stateless
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> obtenerUsuarioPorToken(JwtDTO jwtDTO) {
        String email = jwtService.extractUsername(jwtDTO);
        return usuarioRepository.buscarPorEmail(email)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarToken(JwtDTO jwtDTO) {
        try {
            String email = jwtService.extractUsername(jwtDTO);
            UserDetails userDetails = loadUserByUsername(email);
            return jwtService.validateToken(jwtDTO, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void cambiarPassword(Long usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void recuperarPassword(String email) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Aquí iría la lógica para enviar el email de recuperación
        // Por ahora solo lanzamos una excepción
        throw new RuntimeException("Funcionalidad no implementada");
    }

    @Override
    @Transactional
    public void resetearPassword(JwtDTO jwtDTO, String passwordNueva) {
        String email = jwtService.extractUsername(jwtDTO);
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        return new UsuarioDetailsAdapter(usuario);
    }

    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        return dto;
    }
}