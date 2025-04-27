package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.dto.RegistroUsuarioDTO;
import com.gestiongastos.sistema.model.Usuario;

public interface AuthService {
    String login(LoginDTO loginRequest);
    Usuario register(RegistroUsuarioDTO registroRequest);
}