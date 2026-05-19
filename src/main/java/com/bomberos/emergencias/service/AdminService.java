package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.response.AdminStatsDto;
import com.bomberos.emergencias.dto.response.ReporteResponseDto;
import com.bomberos.emergencias.entity.Reporte;
import com.bomberos.emergencias.entity.enums.EstadoReporte;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import com.bomberos.emergencias.repository.ReporteRepository;
import com.bomberos.emergencias.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReporteRepository reporteRepository;
    private final UnidadRepository unidadRepository;

    @Transactional(readOnly = true)
    public AdminStatsDto obtenerEstadisticas() {

        // ── Contadores por estado ──────────────────────────────────────────
        long total    = reporteRepository.count();
        long enCola   = reporteRepository.countByEstado(EstadoReporte.EN_COLA);
        long activos  = reporteRepository.countByEstado(EstadoReporte.ACTIVO);
        long pend     = reporteRepository.countByEstado(EstadoReporte.PENDIENTE);
        long resuelto = reporteRepository.countByEstado(EstadoReporte.RESUELTO);
        long cancel   = reporteRepository.countByEstado(EstadoReporte.CANCELADO);

        // ── Reportes de hoy ───────────────────────────────────────────────
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        long hoy = reporteRepository.contarReportesPorPeriodo(inicioHoy, LocalDateTime.now());

        // ── Por tipo de incidente ─────────────────────────────────────────
        List<Reporte> todos = reporteRepository.findAll();
        Map<String, Long> porTipo = todos.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getTipoIncidente() != null ? r.getTipoIncidente() : "OTROS",
                        Collectors.counting()
                ));

        // ── Por prioridad ─────────────────────────────────────────────────
        Map<String, Long> porPrioridad = new LinkedHashMap<>();
        porPrioridad.put("P1 - Alta",  todos.stream().filter(r -> "P1".equals(r.getPrioridad())).count());
        porPrioridad.put("P2 - Media", todos.stream().filter(r -> "P2".equals(r.getPrioridad())).count());
        porPrioridad.put("P3 - Baja",  todos.stream().filter(r -> "P3".equals(r.getPrioridad())).count());
        porPrioridad.put("Sin asignar", todos.stream().filter(r -> r.getPrioridad() == null).count());

        // ── Unidades ──────────────────────────────────────────────────────
        long unidadesDisp  = unidadRepository.countByEstado(EstadoUnidad.DISPONIBLE);
        long unidadesTotal = unidadRepository.count();

        // ── Últimos 5 reportes ────────────────────────────────────────────
        List<ReporteResponseDto> recientes = reporteRepository
                .findReportesRecientes(LocalDateTime.now().minusDays(30))
                .stream()
                .limit(5)
                .map(r -> ReporteResponseDto.builder()
                        .id(r.getId())
                        .tipoIncidente(r.getTipoIncidente())
                        .estado(r.getEstado().name())
                        .descripcion(r.getDescripcion())
                        .prioridad(r.getPrioridad())
                        .timestamp(r.getTimestamp())
                        .nombreCiudadano(r.getUsuario() != null
                                ? r.getUsuario().getNombre() + " " + r.getUsuario().getApellido()
                                : "Anónimo")
                        .build())
                .collect(Collectors.toList());

        return AdminStatsDto.builder()
                .totalReportes(total)
                .enCola(enCola)
                .activos(activos)
                .pendientes(pend)
                .resueltos(resuelto)
                .cancelados(cancel)
                .reportesHoy(hoy)
                .porTipoIncidente(porTipo)
                .porPrioridad(porPrioridad)
                .unidadesDisponibles(unidadesDisp)
                .unidadesTotales(unidadesTotal)
                .reportesRecientes(recientes)
                .build();
    }
}
