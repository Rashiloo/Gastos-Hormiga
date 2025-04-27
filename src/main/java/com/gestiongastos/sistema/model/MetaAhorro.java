package com.gestiongastos.sistema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la meta es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El monto objetivo es obligatorio")
    @Positive(message = "El monto objetivo debe ser positivo")
    @Column(name = "monto_objetivo")
    private Double montoObjetivo;

    @Column(name = "monto_actual")
    private Double montoActual = 0.0;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_objetivo")
    private LocalDate fechaObjetivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        fechaInicio = LocalDate.now();
    }
}