package com.gestiongastos.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gastos")
public class Gasto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "tipo_gasto_id", nullable = false)
    private TipoGasto tipoGasto;
    
    @Column(nullable = false)
    private Double monto;
    
    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(length = 100)
    private String descripcion;
    
    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;
    
    @Column(name = "periodo_fin")
    private LocalDate periodoFin;
}