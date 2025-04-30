package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.UsuarioDTO;
import com.gestiongastos.sistema.dto.UsuarioResponseDTO;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Interfaz que define las operaciones disponibles para el servicio de usuarios.
 * Proporciona métodos para la gestión completa de usuarios en el sistema.
 */
public interface UsuarioService {
    // Operaciones CRUD básicas
    /**
     * Crea un nuevo usuario en el sistema
     * @param usuarioDTO Datos del usuario a crear
     * @return UsuarioResponseDTO con los datos del usuario creado
     */
    UsuarioResponseDTO crearUsuario(UsuarioDTO usuarioDTO);

    /**
     * Obtiene todos los usuarios registrados
     * @return Lista de usuarios
     */
    List<UsuarioResponseDTO> obtenerTodos();

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioResponseDTO> obtenerPorId(Long id);

    /**
     * Actualiza los datos de un usuario existente
     * @param id ID del usuario a actualizar
     * @param usuarioDTO Nuevos datos del usuario
     * @return UsuarioResponseDTO con los datos actualizados
     */
    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO);

    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     */
    void eliminarPorId(Long id);
    
    // Métodos de búsqueda
    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioResponseDTO> obtenerPorEmail(String email);

    /**
     * Busca un usuario por su nombre
     * @param nombre Nombre del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioResponseDTO> obtenerPorNombre(String nombre);

    /**
     * Verifica si existe un usuario con el email especificado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre especificado
     * @param nombre Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorNombre(String nombre);
    
    // Métodos de autenticación
    /**
     * Autentica un usuario con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Optional con el usuario si la autenticación es exitosa
     */
    Optional<UsuarioResponseDTO> autenticarUsuario(String email, String password);

    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioResponseDTO> buscarPorEmail(String email);
    
    // Métodos de gestión
    /**
     * Actualiza la contraseña de un usuario
     * @param id ID del usuario
     * @param nuevaContrasena Nueva contraseña
     */
    void actualizarContrasena(Long id, String nuevaContrasena);

    /**
     * Marca que un usuario ha configurado su presupuesto inicial
     * @param id ID del usuario
     * @return UsuarioResponseDTO con el estado actualizado
     */
    UsuarioResponseDTO marcarPresupuestoInicialConfigurado(Long id);

    /**
     * Obtiene la fecha de registro de un usuario
     * @param id ID del usuario
     * @return Fecha de registro
     */
    LocalDateTime obtenerFechaRegistro(Long id);

    /**
     * Verifica si un usuario tiene presupuesto inicial configurado
     * @param id ID del usuario
     * @return true si tiene presupuesto inicial, false en caso contrario
     */
    boolean tienePresupuestoInicial(Long id);

    /**
     * Obtiene el total de gastos de un usuario
     * @param id ID del usuario
     * @return Total de gastos
     */
    Double obtenerTotalGastos(Long id);

    /**
     * Obtiene el total de presupuestos de un usuario
     * @param id ID del usuario
     * @return Total de presupuestos
     */
    Double obtenerTotalPresupuestos(Long id);

    /**
     * Obtiene el total de metas de ahorro de un usuario
     * @param id ID del usuario
     * @return Total de metas de ahorro
     */
    Double obtenerTotalMetasAhorro(Long id);
} 