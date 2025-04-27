package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.RegistroUsuarioDTO;
import com.gestiongastos.sistema.dto.UsuarioDTO;
import com.gestiongastos.sistema.model.Usuario;

public interface UsuarioService {
    UsuarioDTO registrarUsuario(RegistroUsuarioDTO registroDTO);
    UsuarioDTO obtenerUsuarioPorId(Long id);
    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO);
    void eliminarUsuario(Long id);
    UsuarioDTO obtenerUsuarioPorEmail(String email);
    void actualizarPresupuestoMensual(Long id, Double presupuesto);
    void actualizarPassword(Long id, String newPassword);
    Usuario findByEmail(String email);
    Usuario save(Usuario usuario);
}