package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.dto.MetaAhorroDTO;
import com.gestiongastos.sistema.dto.MetaAhorroResponseDTO;
import com.gestiongastos.sistema.dto.UsuarioDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de metas de ahorro.
 * Define los métodos para gestionar las metas de ahorro de los usuarios.
 */
public interface MetaAhorroService {
    
    /**
     * Crea una nueva meta de ahorro para un usuario.
     * 
     * @param metaDTO DTO de la meta a crear
     * @param usuarioDTO DTO del usuario para el que se crea la meta
     * @return La meta de ahorro creada
     */
    MetaAhorroResponseDTO registrarMetaAhorro(MetaAhorroDTO metaDTO, UsuarioDTO usuarioDTO);
    
    /**
     * Actualiza una meta de ahorro existente.
     * 
     * @param id ID de la meta a actualizar
     * @param metaDTO DTO de la meta actualizada
     * @param usuarioDTO DTO del usuario del que se actualiza la meta
     * @return La meta de ahorro actualizada
     */
    MetaAhorroResponseDTO actualizarMetaAhorro(Long id, MetaAhorroDTO metaDTO, UsuarioDTO usuarioDTO);
    
    /**
     * Elimina una meta de ahorro por su ID.
     * 
     * @param id ID de la meta a eliminar
     * @param usuarioDTO DTO del usuario del que se elimina la meta
     */
    void eliminarMetaAhorro(Long id, UsuarioDTO usuarioDTO);
    
    /**
     * Obtiene una meta de ahorro específica por su ID.
     * 
     * @param id ID de la meta a obtener
     * @return La meta de ahorro o vacío si no existe
     */
    Optional<MetaAhorroResponseDTO> obtenerPorId(Long id);
    
    /**
     * Obtiene todas las metas de ahorro de un usuario.
     * 
     * @param usuarioDTO DTO del usuario del que se quieren obtener las metas
     * @return Lista de todas las metas de ahorro
     */
    List<MetaAhorroResponseDTO> obtenerMetasAhorroUsuario(UsuarioDTO usuarioDTO);
    
    /**
     * Obtiene todas las metas de ahorro activas de un usuario.
     * 
     * @param usuarioDTO DTO del usuario del que se quieren obtener las metas activas
     * @return Lista de todas las metas de ahorro activas
     */
    List<MetaAhorroResponseDTO> obtenerMetasAhorroActivas(UsuarioDTO usuarioDTO);
    
    /**
     * Calcula el progreso actual de una meta de ahorro.
     * 
     * @param metaId ID de la meta
     * @return Porcentaje de progreso (0-100)
     */
    Double calcularProgresoMetaAhorro(Long metaId);
    
    /**
     * Calcula el ahorro diario necesario para alcanzar una meta.
     * 
     * @param metaId ID de la meta
     * @return Cantidad que se debe ahorrar diariamente
     */
    Double calcularAhorroDiarioNecesario(Long metaId);
    
    /**
     * Registra un gasto evitado asociado a una meta de ahorro.
     * 
     * @param metaId ID de la meta
     * @param tipoGastoId ID del tipo de gasto evitado
     * @param monto Monto del gasto evitado
     * @param descripcion Descripción del gasto evitado
     * @param evidenciaAhorro Evidencia del ahorro (por ejemplo, una imagen o un archivo)
     */
    void registrarGastoEvitado(Long metaId, Long tipoGastoId, Double monto, String descripcion, String evidenciaAhorro);
    
    /**
     * Verifica si una meta de ahorro está completada.
     * 
     * @param metaId ID de la meta
     */
    void verificarMetaCompletada(Long metaId);
}