package com.gestiongastos.sistema.controller;

import com.gestiongastos.sistema.dto.GastoDTO;
import com.gestiongastos.sistema.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GastoController {

    private final GastoService gastoService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<GastoDTO> crearGasto(
            @PathVariable Long usuarioId,
            @Valid @RequestBody GastoDTO gastoDTO) {
        GastoDTO gastoCreado = gastoService.crearGasto(usuarioId, gastoDTO);
        return new ResponseEntity<>(gastoCreado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoDTO> obtenerGasto(@PathVariable Long id) {
        GastoDTO gasto = gastoService.obtenerGastoPorId(id);
        return ResponseEntity.ok(gasto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<GastoDTO>> obtenerGastosPorUsuario(@PathVariable Long usuarioId) {
        List<GastoDTO> gastos = gastoService.obtenerGastosPorUsuario(usuarioId);
        return ResponseEntity.ok(gastos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDTO> actualizarGasto(
            @PathVariable Long id,
            @Valid @RequestBody GastoDTO gastoDTO) {
        GastoDTO gastoActualizado = gastoService.actualizarGasto(id, gastoDTO);
        return ResponseEntity.ok(gastoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<GastoDTO>> obtenerGastosPorPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<GastoDTO> gastos = gastoService.obtenerGastosPorPeriodo(usuarioId, inicio, fin);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/usuario/{usuarioId}/evitables")
    public ResponseEntity<List<GastoDTO>> obtenerGastosEvitables(
            @PathVariable Long usuarioId,
            @RequestParam boolean esEvitable) {
        List<GastoDTO> gastos = gastoService.obtenerGastosEvitables(usuarioId, esEvitable);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/usuario/{usuarioId}/recurrentes")
    public ResponseEntity<List<GastoDTO>> obtenerGastosRecurrentes(@PathVariable Long usuarioId) {
        List<GastoDTO> gastos = gastoService.obtenerGastosRecurrentes(usuarioId);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/usuario/{usuarioId}/total-periodo")
    public ResponseEntity<Double> calcularTotalGastosPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        Double total = gastoService.calcularTotalGastosPeriodo(usuarioId, inicio, fin);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}/total-evitables")
    public ResponseEntity<Double> calcularTotalGastosEvitables(@PathVariable Long usuarioId) {
        Double total = gastoService.calcularTotalGastosEvitables(usuarioId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/usuario/{usuarioId}/categoria/{categoriaId}")
    public ResponseEntity<List<GastoDTO>> obtenerGastosPorCategoria(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId) {
        List<GastoDTO> gastos = gastoService.obtenerGastosPorCategoria(usuarioId, categoriaId);
        return ResponseEntity.ok(gastos);
    }
}