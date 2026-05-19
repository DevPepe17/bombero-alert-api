package com.bomberos.emergencias.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta para el panel de estadísticas del Administrador.
 */
@Data
@Builder
public class AdminStatsDto {

    // ── Contadores por estado ──────────────────────────────────────────────
    private long totalReportes;
    private long enCola;
    private long activos;
    private long pendientes;
    private long resueltos;
    private long cancelados;

    // ── Reportes de hoy ───────────────────────────────────────────────────
    private long reportesHoy;

    // ── Distribución por tipo de incidente ────────────────────────────────
    private Map<String, Long> porTipoIncidente;

    // ── Distribución por prioridad ────────────────────────────────────────
    private Map<String, Long> porPrioridad;

    // ── Resumen de unidades ───────────────────────────────────────────────
    private long unidadesDisponibles;
    private long unidadesTotales;

    // ── Últimos 5 reportes ────────────────────────────────────────────────
    private List<ReporteResponseDto> reportesRecientes;
}
