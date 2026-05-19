package com.bomberos.emergencias.dto.request;

import lombok.Data;

@Data
public class ReporteUpdateRequestDto {
    private String descripcion;
    private String prioridad;
    private String motivoResolucion;
    private String unidadAsignada;
    private String estado; // Para cambios desde el panel de Actividades
}
