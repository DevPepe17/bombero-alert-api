package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.dto.response.UnidadResponseDto;
import com.bomberos.emergencias.service.UnidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
public class UnidadController {

    private final UnidadService unidadService;

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<UnidadResponseDto>> listarUnidadesDisponibles() {
        return ResponseEntity.ok(unidadService.listarUnidadesDisponibles());
    }
}
