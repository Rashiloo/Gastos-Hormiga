package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.JwtDTO;
import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.dto.RegisterDTO;
import com.gestiongastos.sistema.dto.UsuarioResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Optional;

public interface AuthService extends UserDetailsService {
    JwtDTO iniciarSesion(LoginDTO loginDTO);
    UsuarioResponseDTO registrarUsuario(RegisterDTO registerDTO);
    void cerrarSesion(JwtDTO jwtDTO);
    Optional<UsuarioResponseDTO> obtenerUsuarioPorToken(JwtDTO jwtDTO);
    boolean validarToken(JwtDTO jwtDTO);
    void cambiarPassword(Long usuarioId, String passwordActual, String passwordNueva);
    void recuperarPassword(String email);
    void resetearPassword(JwtDTO jwtDTO, String passwordNueva);
}