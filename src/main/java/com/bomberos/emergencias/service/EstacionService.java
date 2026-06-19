package com.bomberos.emergencias.service;

import com.bomberos.emergencias.repository.EstacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstacionService {

    private final EstacionRepository estacionRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarTodas() {
        return estacionRepository.findAll().stream()
                .map(e -> Map.<String, Object>of(
                        "id", e.getId(),
                        "nombre", e.getNombre(),
                        "direccion", e.getDireccion(),
                        "distrito", e.getDistrito(),
                        "latitud", e.getLatitud(),
                        "longitud", e.getLongitud()
                ))
                .toList();
    }
}