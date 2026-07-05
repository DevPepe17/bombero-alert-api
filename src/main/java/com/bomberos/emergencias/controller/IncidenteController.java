package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.dto.response.IncidenteResponseDto;
import com.bomberos.emergencias.service.IncidenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@RestController
@RequestMapping("/api/incidentes")
@RequiredArgsConstructor
public class IncidenteController {

    private final IncidenteService incidenteService;

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<IncidenteResponseDto>> listarIncidentesActivos() {
        return ResponseEntity.ok(incidenteService.listarIncidentesActivosMap());
    }

    @PostMapping("/reportes/{idReporte}/despachar/{idUnidad}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> despacharUnidad(
            @PathVariable Long idReporte,
            @PathVariable Long idUnidad) {
        incidenteService.despacharUnidad(idReporte, idUnidad);
        return ResponseEntity.ok().build();
    }
}
