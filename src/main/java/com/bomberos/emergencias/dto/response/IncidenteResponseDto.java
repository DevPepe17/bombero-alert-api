package com.bomberos.emergencias.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidenteResponseDto {
    private Long id;
    private String prioridadSugerida;
    private String prioridadFinal;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierre;
    private String observaciones;
    // Opcional: Para mostrar cuántos reportes están agrupados
    private int cantidadReportes;
}
