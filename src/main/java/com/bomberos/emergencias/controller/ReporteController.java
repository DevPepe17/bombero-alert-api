package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.dto.request.ReporteRequestDto;
import com.bomberos.emergencias.dto.response.ReporteResponseDto;
import com.bomberos.emergencias.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    // ─── Endpoints para el Ciudadano (CU03, CU04) ─────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('CIUDADANO')")
    public ResponseEntity<ReporteResponseDto> crearReporte(
            @Valid @RequestBody ReporteRequestDto dto,
            Authentication authentication) {
        // authentication.getName() devuelve el email del usuario autenticado
        ReporteResponseDto creado = reporteService.crearReporte(dto, authentication.getName());
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @GetMapping("/mis-reportes")
    @PreAuthorize("hasRole('CIUDADANO')")
    public ResponseEntity<List<ReporteResponseDto>> listarMisReportes(Authentication authentication) {
        return ResponseEntity.ok(reporteService.listarReportesPorCiudadano(authentication.getName()));
    }

    // ─── Endpoints para el Operador (CU05) ────────────────────────────────────

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<ReporteResponseDto>> listarReportesPendientes() {
        return ResponseEntity.ok(reporteService.listarReportesPendientesParaOperador());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<ReporteResponseDto> actualizarReporte(
            @PathVariable Long id,
            @RequestBody com.bomberos.emergencias.dto.request.ReporteUpdateRequestDto dto) {
        ReporteResponseDto actualizado = reporteService.actualizarReporte(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/todos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> eliminarTodosLosReportes() {
        reporteService.eliminarTodosLosReportes();
        return ResponseEntity.ok("Todos los reportes han sido eliminados.");
    }
}
