package com.gestiongastos.sistema.service;

import com.gestiongastos.sistema.model.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> obtenerTodasLasCategorias();
    Categoria obtenerCategoriaPorId(Long id);
}