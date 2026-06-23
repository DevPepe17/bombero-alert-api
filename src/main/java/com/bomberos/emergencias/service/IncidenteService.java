package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.response.IncidenteResponseDto;
import com.bomberos.emergencias.entity.Incidente;
import com.bomberos.emergencias.repository.IncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bomberos.emergencias.entity.AsignacionUnidad;
import com.bomberos.emergencias.entity.Reporte;
import com.bomberos.emergencias.entity.Unidad;
import com.bomberos.emergencias.entity.enums.EstadoAsignacion;
import com.bomberos.emergencias.entity.enums.EstadoReporte;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import com.bomberos.emergencias.repository.AsignacionUnidadRepository;
import com.bomberos.emergencias.repository.ReporteRepository;
import com.bomberos.emergencias.repository.UnidadRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final ReporteRepository reporteRepository;
    private final UnidadRepository unidadRepository;
    private final AsignacionUnidadRepository asignacionUnidadRepository;

    @Transactional(readOnly = true)
    public List<IncidenteResponseDto> listarIncidentesActivosMap() {
        // Obtenemos los incidentes activos para el panel
        List<Incidente> incidentes = incidenteRepository.findIncidentesAbiertos();

        return incidentes.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void despacharUnidad(Long idReporte, Long idUnidad) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        Unidad unidad = unidadRepository.findById(idUnidad)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        if (unidad.getEstado() != EstadoUnidad.DISPONIBLE) {
            throw new RuntimeException("La unidad no está disponible");
        }

        if (asignacionUnidadRepository.existsByUnidadIdAndEstadoAsignacion(
                idUnidad,
                EstadoAsignacion.ACTIVA)) {
            throw new RuntimeException("La unidad ya tiene una asignación activa");
        }

        Incidente incidente = reporte.getIncidente();

        if (incidente == null) {
            incidente = Incidente.builder()
                    .observaciones("Incidente generado desde reporte #" + reporte.getId())
                    .build();

            incidente = incidenteRepository.save(incidente);
            reporte.setIncidente(incidente);
        }

        AsignacionUnidad asignacion = AsignacionUnidad.builder()
                .incidente(incidente)
                .unidad(unidad)
                .estadoAsignacion(EstadoAsignacion.ACTIVA)
                .build();

        asignacionUnidadRepository.save(asignacion);

        unidad.setEstado(EstadoUnidad.OCUPADA);
        unidadRepository.save(unidad);

        reporte.setEstado(EstadoReporte.ACTIVO);
        reporteRepository.save(reporte);
    }

    private IncidenteResponseDto mapearADto(Incidente incidente) {
        int cantidadReportes = (incidente.getReportes() != null) ? incidente.getReportes().size() : 0;

        return IncidenteResponseDto.builder()
                .id(incidente.getId())
                .estado(incidente.getEstado().name())
                .prioridadSugerida(
                        incidente.getPrioridadSugerida() != null ? incidente.getPrioridadSugerida().name() : null)
                .prioridadFinal(incidente.getPrioridadFinal() != null ? incidente.getPrioridadFinal().name() : null)
                .fechaInicio(incidente.getFechaInicio())
                .fechaCierre(incidente.getFechaCierre())
                .observaciones(incidente.getObservaciones())
                .cantidadReportes(cantidadReportes)
                .build();
    }
}
