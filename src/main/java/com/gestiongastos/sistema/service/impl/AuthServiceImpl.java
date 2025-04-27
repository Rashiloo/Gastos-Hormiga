package com.gestiongastos.sistema.service.impl;

import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.dto.RegistroUsuarioDTO;
import com.gestiongastos.sistema.model.Usuario;
import com.gestiongastos.sistema.repository.UsuarioRepository;
import com.gestiongastos.sistema.security.JwtTokenProvider;
import com.gestiongastos.sistema.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(LoginDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public Usuario register(RegistroUsuarioDTO registroRequest) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroRequest.getNombre());
        usuario.setApellido(registroRequest.getApellido());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));

        // Guardar el usuario
        return usuarioRepository.save(usuario);
    }
}