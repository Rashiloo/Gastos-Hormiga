package com.gestiongastos.sistema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GastoDTO {
    private Long id;
    
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;
    
    private String descripcion;
    private LocalDate fechaGasto;
    private LocalDate fechaRegistro;
    private Long usuarioId;
    private Long tipoGastoId;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
}