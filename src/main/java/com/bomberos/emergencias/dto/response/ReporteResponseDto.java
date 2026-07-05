package com.bomberos.emergencias.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteResponseDto {
    private Long id;
    private String tipoIncidente;
    private String estado;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String descripcion;
    private String prioridad;
    private String fotoUrl;
    private LocalDateTime timestamp;
    private String motivoResolucion;

    // Datos del Ciudadano
    private String nombreCiudadano;
    private String telefonoCiudadano;
    private String correoCiudadano;

    // Información opcional sobre a qué incidente pertenece (si ya fue agrupado)
    private Long idIncidente;
    private List<UnidadDespachadaDto> unidadesDespachadas;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnidadDespachadaDto {
        private Long idAsignacion;
        private Long idUnidad;
        private String codigo;
        private String tipo;
        private String estadoUnidad;
        private String estadoAsignacion;
        private LocalDateTime fechaAsignacion;
    }

    // Unidad asignada al incidente
    private String unidadAsignada;
    private String tipoUnidadAsignada;
    private String estadoUnidadAsignada;
}
