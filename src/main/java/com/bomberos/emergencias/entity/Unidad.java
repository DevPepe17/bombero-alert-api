package com.bomberos.emergencias.entity;

import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Entidad que representa una Unidad (vehículo/equipo) de bomberos.
 * Pertenece a una Estación y puede ser asignada a múltiples Incidentes
 * a través de la entidad intermedia {@link AsignacionUnidad}.
 *
 * Caso de uso: CU08 - Gestión de Unidades
 */
@Entity
@Table(
    name = "unidades",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_unidades_codigo", columnNames = "codigo")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único de identificación de la unidad.
     * Formato ejemplo: "B-14", "CM-03", "AE-01"
     */
    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String codigo;

    /**
     * Tipo de unidad de bomberos.
     * Ejemplos: "Autobomba", "Cisterna", "Escalera Aérea",
     *           "Unidad de Rescate", "Ambulancia"
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 25)
    private EstadoUnidad estado = EstadoUnidad.DISPONIBLE;

    /** Estación base a la que pertenece esta unidad */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estacion", nullable = false,
                foreignKey = @ForeignKey(name = "fk_unidades_estacion"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Estacion estacion;

    /**
     * Historial de asignaciones de esta unidad a incidentes.
     * Relación N:M gestionada mediante entidad intermedia explícita.
     */
    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AsignacionUnidad> asignaciones;
}
