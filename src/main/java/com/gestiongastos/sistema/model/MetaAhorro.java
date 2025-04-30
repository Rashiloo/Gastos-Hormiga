package com.gestiongastos.sistema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El monto objetivo es obligatorio")
    @Positive(message = "El monto objetivo debe ser positivo")
    @Column(name = "monto_objetivo", nullable = false)
    private Double montoObjetivo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMeta estado = EstadoMeta.ACTIVA;

    @Column(name = "total_gastos_hormiga_periodo", nullable = false)
    private Double totalGastosHormigaPeriodo = 0.0;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
    private List<GastoEvitado> gastosEvitados;

    public enum EstadoMeta {
        ACTIVA, COMPLETADA, CANCELADA
    }
}