package com.bomberos.emergencias.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Entidad que representa una Estación de Bomberos.
 *
 * La ubicación geográfica (latitud/longitud) se almacena como Double,
 * compatible con Leaflet.js (coordenadas decimales WGS-84).
 */
@Entity
@Table(name = "estaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre oficial de la estación (ej: "Estación Central - Miraflores") */
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String nombre;

    /** Dirección física */
    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String direccion;

    /**
     * Latitud en grados decimales (WGS-84).
     * Rango válido: -90.0 a 90.0
     * Compatible con Leaflet.js sin conversión.
     */
    @NotNull
    @Column(nullable = false, precision = 9)
    private Double latitud;

    /**
     * Longitud en grados decimales (WGS-84).
     * Rango válido: -180.0 a 180.0
     * Compatible con Leaflet.js sin conversión.
     */
    @NotNull
    @Column(nullable = false, precision = 9)
    private Double longitud;

    /** Número de teléfono de la estación */
    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    /** Distrito donde se ubica la estación (útil para las estadísticas CU11) */
    @Size(max = 100)
    @Column(length = 100)
    private String distrito;

    /** Relación 1:N → una Estación tiene muchas Unidades */
    @OneToMany(mappedBy = "estacion", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Unidad> unidades;
}
