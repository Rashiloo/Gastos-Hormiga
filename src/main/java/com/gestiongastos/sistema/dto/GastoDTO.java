package com.gestiongastos.sistema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GastoDTO {
    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto;

    private LocalDateTime fechaGasto;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private String categoriaNombre;

    private boolean esEvitable;

    private boolean esRecurrente;
}