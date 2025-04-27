package com.gestiongastos.sistema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String email;

    public JwtResponseDTO(String token, Long id, String email) {
        this.token = token;
        this.id = id;
        this.email = email;
    }
}