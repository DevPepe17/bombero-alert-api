package com.bomberos.emergencias.entity;

import com.bomberos.emergencias.entity.enums.EstadoAsignacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad intermedia que representa la asignación de una Unidad a un Incidente.
 *
 * Se utiliza en lugar de un @ManyToMany simple para poder registrar:
 * - Quién realizó la asignación (operador)
 * - Cuándo fue asignada y cuándo fue liberada
 * - El estado actual de la asignación
 *
 * Esto es fundamental para el despacho de emergencias y el historial
 * operativo del cuerpo de bomberos.
 *
 * Caso de uso: CU08 - Gestión de Unidades
 */
@Entity
@Table(
    name = "asignaciones_unidades",
    indexes = {
        @Index(name = "idx_asignacion_incidente", columnList = "id_incidente"),
        @Index(name = "idx_asignacion_unidad",    columnList = "id_unidad"),
        @Index(name = "idx_asignacion_estado",    columnList = "estado_asignacion")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionUnidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Claves foráneas principales ───────────────────────────────────────────

    /** Incidente al que se asigna la unidad */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incidente", nullable = false,
                foreignKey = @ForeignKey(name = "fk_asignacion_incidente"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Incidente incidente;

    /** Unidad asignada */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = false,
                foreignKey = @ForeignKey(name = "fk_asignacion_unidad"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Unidad unidad;

    // ─── Campos de auditoría ───────────────────────────────────────────────────

    /** Operador que realizó la asignación */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador",
                foreignKey = @ForeignKey(name = "fk_asignacion_operador"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario operador;

    /** Momento en que se realizó la asignación */
    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    /** Momento en que la unidad fue liberada del incidente */
    @Column(name = "fecha_liberacion")
    private LocalDateTime fechaLiberacion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "estado_asignacion", nullable = false, length = 15)
    private EstadoAsignacion estadoAsignacion = EstadoAsignacion.ACTIVA;

    /**
     * Notas adicionales del operador sobre esta asignación específica.
     * Ej: "Unidad llegó en 4 min", "Requirió apoyo aéreo"
     */
    @Column(columnDefinition = "TEXT")
    private String notas;

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}
