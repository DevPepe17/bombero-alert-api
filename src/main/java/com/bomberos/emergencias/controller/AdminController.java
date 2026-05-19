package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.dto.response.AdminStatsDto;
import com.bomberos.emergencias.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AdminStatsDto> obtenerEstadisticas() {
        return ResponseEntity.ok(adminService.obtenerEstadisticas());
    }
}
