package com.gestiongastos.sistema.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MetaAhorroDTO {
    private String nombre;
    private Double montoObjetivo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double totalGastosHormigaPeriodo;
}