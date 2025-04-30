package com.gestiongastos.sistema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "tiene_presupuesto_inicial")
    private Boolean tienePresupuestoInicial = false;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Gasto> gastos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Presupuesto> presupuestos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<MetaAhorro> metasAhorro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        tienePresupuestoInicial = false;
    }
}