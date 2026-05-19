package com.bomberos.emergencias.entity;

import com.bomberos.emergencias.entity.enums.EstadoIncidente;
import com.bomberos.emergencias.entity.enums.Prioridad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que agrupa uno o más Reportes del mismo evento de emergencia.
 *
 * El sistema puede crear un Incidente automáticamente al recibir el primer
 * Reporte verificado (CU13 - Verificación de Duplicados). Cada Incidente
 * gestiona su propia prioridad, operador asignado y ciclo de vida.
 *
 * Casos de uso: CU05, CU06, CU07, CU09
 */
@Entity
@Table(name = "incidentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Prioridad sugerida por el sistema (IA / densidad de reportes).
     * Puede diferir de la prioridad final asignada por el operador.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_sugerida", length = 10)
    private Prioridad prioridadSugerida;

    /**
     * Prioridad final validada y asignada manualmente por el operador.
     * Caso de uso CU07.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad_final", length = 10)
    private Prioridad prioridadFinal;

    /**
     * Operador de bomberos responsable del incidente.
     * FK a Usuario (rol = OPERADOR).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador",
                foreignKey = @ForeignKey(name = "fk_incidentes_operador"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario operador;

    /** Timestamp de apertura del incidente */
    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    /** Timestamp de cierre del incidente (CU09) */
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private EstadoIncidente estado = EstadoIncidente.ACTIVO;

    /**
     * Notas u observaciones del operador sobre el incidente.
     * Útil para el historial y las estadísticas (CU11).
     */
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    /** Reportes ciudadanos vinculados a este incidente */
    @OneToMany(mappedBy = "incidente", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reporte> reportes;

    /** Unidades asignadas a este incidente (N:M explícita) */
    @OneToMany(mappedBy = "incidente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AsignacionUnidad> asignaciones;

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.fechaInicio = LocalDateTime.now();
    }
}
