package com.gestiongastos.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetaAhorroDTO {
    private Long id;

    @NotBlank(message = "El nombre de la meta es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El monto objetivo es obligatorio")
    @Positive(message = "El monto objetivo debe ser positivo")
    private Double montoObjetivo;

    private Double montoActual;

    private LocalDate fechaInicio;

    @NotNull(message = "La fecha objetivo es obligatoria")
    private LocalDate fechaObjetivo;

    private Double porcentajeCompletado;
}