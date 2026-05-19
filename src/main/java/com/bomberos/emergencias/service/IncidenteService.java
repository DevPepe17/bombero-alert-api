package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.response.IncidenteResponseDto;
import com.bomberos.emergencias.entity.Incidente;
import com.bomberos.emergencias.entity.enums.EstadoIncidente;
import com.bomberos.emergencias.repository.IncidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;

    @Transactional(readOnly = true)
    public List<IncidenteResponseDto> listarIncidentesActivosMap() {
        // Obtenemos los incidentes activos para el panel
        List<Incidente> incidentes = incidenteRepository.findIncidentesAbiertos();

        return incidentes.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private IncidenteResponseDto mapearADto(Incidente incidente) {
        int cantidadReportes = (incidente.getReportes() != null) ? incidente.getReportes().size() : 0;
        
        return IncidenteResponseDto.builder()
                .id(incidente.getId())
                .estado(incidente.getEstado().name())
                .prioridadSugerida(incidente.getPrioridadSugerida() != null ? incidente.getPrioridadSugerida().name() : null)
                .prioridadFinal(incidente.getPrioridadFinal() != null ? incidente.getPrioridadFinal().name() : null)
                .fechaInicio(incidente.getFechaInicio())
                .fechaCierre(incidente.getFechaCierre())
                .observaciones(incidente.getObservaciones())
                .cantidadReportes(cantidadReportes)
                .build();
    }
}
