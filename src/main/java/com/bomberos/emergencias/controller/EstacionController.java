package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.entity.Estacion;
import com.bomberos.emergencias.service.EstacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estaciones")
@RequiredArgsConstructor
public class EstacionController {
    
    private final EstacionService estacionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR', 'CIUDADANO')")
    public ResponseEntity<List<Map<String, Object>>> listarEstaciones() {
        return ResponseEntity.ok(estacionService.listarTodas());
    }
}
