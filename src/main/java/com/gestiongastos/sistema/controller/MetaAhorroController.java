package com.gestiongastos.sistema.controller;

import com.gestiongastos.sistema.dto.MetaAhorroDTO;
import com.gestiongastos.sistema.service.MetaAhorroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas-ahorro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetaAhorroController {

    private final MetaAhorroService metaAhorroService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<MetaAhorroDTO> crearMetaAhorro(
            @PathVariable Long usuarioId,
            @Valid @RequestBody MetaAhorroDTO metaAhorroDTO) {
        MetaAhorroDTO metaCreada = metaAhorroService.crearMetaAhorro(usuarioId, metaAhorroDTO);
        return new ResponseEntity<>(metaCreada, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaAhorroDTO> obtenerMetaAhorro(@PathVariable Long id) {
        MetaAhorroDTO meta = metaAhorroService.obtenerMetaAhorroPorId(id);
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MetaAhorroDTO>> obtenerMetasAhorroPorUsuario(@PathVariable Long usuarioId) {
        List<MetaAhorroDTO> metas = metaAhorroService.obtenerMetasAhorroPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaAhorroDTO> actualizarMetaAhorro(
            @PathVariable Long id,
            @Valid @RequestBody MetaAhorroDTO metaAhorroDTO) {
        MetaAhorroDTO metaActualizada = metaAhorroService.actualizarMetaAhorro(id, metaAhorroDTO);
        return ResponseEntity.ok(metaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMetaAhorro(@PathVariable Long id) {
        metaAhorroService.eliminarMetaAhorro(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/monto-actual")
    public ResponseEntity<MetaAhorroDTO> actualizarMontoActual(
            @PathVariable Long id,
            @RequestParam Double montoActual) {
        MetaAhorroDTO metaActualizada = metaAhorroService.actualizarMontoActual(id, montoActual);
        return ResponseEntity.ok(metaActualizada);
    }

    @GetMapping("/usuario/{usuarioId}/incompletas")
    public ResponseEntity<List<MetaAhorroDTO>> obtenerMetasIncompletas(@PathVariable Long usuarioId) {
        List<MetaAhorroDTO> metasIncompletas = metaAhorroService.obtenerMetasIncompletas(usuarioId);
        return ResponseEntity.ok(metasIncompletas);
    }

    @GetMapping("/{id}/porcentaje-completado")
    public ResponseEntity<Double> calcularPorcentajeCompletado(@PathVariable Long id) {
        Double porcentaje = metaAhorroService.calcularPorcentajeCompletado(id);
        return ResponseEntity.ok(porcentaje);
    }
}