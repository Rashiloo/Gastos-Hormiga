package com.gestiongastos.sistema.controller;

import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }
}