package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.response.UnidadResponseDto;
import com.bomberos.emergencias.entity.Unidad;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import com.bomberos.emergencias.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadService {

    private final UnidadRepository unidadRepository;

    @Transactional(readOnly = true)
    public List<UnidadResponseDto> listarUnidadesDisponibles() {
        List<Unidad> disponibles = unidadRepository.findDisponiblesConEstacion(EstadoUnidad.DISPONIBLE);
        
        return disponibles.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private UnidadResponseDto mapearADto(Unidad unidad) {
        return UnidadResponseDto.builder()
                .id(unidad.getId())
                .codigo(unidad.getCodigo())
                .tipo(unidad.getTipo())
                .estado(unidad.getEstado().name())
                .nombreEstacion(unidad.getEstacion() != null ? unidad.getEstacion().getNombre() : "Sin Estación")
                .build();
    }
}
