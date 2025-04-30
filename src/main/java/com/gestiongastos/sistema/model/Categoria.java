package com.gestiongastos.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(length = 100)
    private String descripcion;
    
    @Column(nullable = false)
    private String color;
    
    @Column(nullable = false)
    private Boolean activa = true;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @OneToMany(mappedBy = "categoria")
    private List<TipoGasto> tiposGasto;
}