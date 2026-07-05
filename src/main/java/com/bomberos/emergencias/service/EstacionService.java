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
                return estacionRepository.findAllWithUnidades().stream()
                                .map(e -> Map.<String, Object>of(
                                                "id", e.getId(),
                                                "nombre", e.getNombre(),
                                                "direccion", e.getDireccion(),
                                                "telefono", e.getTelefono(),
                                                "distrito", e.getDistrito(),
                                                "latitud", e.getLatitud(),
                                                "longitud", e.getLongitud(),
                                                "unidades", e.getUnidades().stream()
                                                                .map(u -> Map.<String, Object>of(
                                                                                "id", u.getId(),
                                                                                "codigo", u.getCodigo(),
                                                                                "tipo", u.getTipo(),
                                                                                "estado", u.getEstado().name()))
                                                                .toList()))
                                .toList();
        }
}