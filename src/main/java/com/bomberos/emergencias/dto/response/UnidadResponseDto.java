package com.bomberos.emergencias.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnidadResponseDto {
    private Long id;
    private String codigo;
    private String tipo;
    private String estado;
    private String nombreEstacion;
}
