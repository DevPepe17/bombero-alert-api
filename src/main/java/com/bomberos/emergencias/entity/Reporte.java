package com.bomberos.emergencias.entity;

import com.bomberos.emergencias.entity.enums.EstadoReporte;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Reporte de emergencia enviado por un ciudadano.
 *
 * Coordenadas almacenadas como BigDecimal para máxima precisión decimal
 * y compatibilidad directa con Leaflet.js (WGS-84).
 *
 * Campos ia_confianza e ia_etiquetas reservados para la fase de IA (CU14/CU15).
 *
 * Casos de uso: CU03, CU04, CU06
 */
@Entity
@Table(
    name = "reportes",
    indexes = {
        @Index(name = "idx_reportes_usuario",   columnList = "id_usuario"),
        @Index(name = "idx_reportes_incidente", columnList = "id_incidente"),
        @Index(name = "idx_reportes_estado",    columnList = "estado"),
        @Index(name = "idx_reportes_timestamp", columnList = "timestamp")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Relaciones ────────────────────────────────────────────────────────────

    /** Usuario ciudadano que envía el reporte */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false,
                foreignKey = @ForeignKey(name = "fk_reportes_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /**
     * Incidente al que pertenece este reporte.
     * Puede ser nulo inicialmente; el sistema (CU13) lo vincula
     * automáticamente al detectar duplicados en la misma zona/tiempo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incidente",
                foreignKey = @ForeignKey(name = "fk_reportes_incidente"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Incidente incidente;

    // ─── Datos del Reporte ─────────────────────────────────────────────────────

    /**
     * Categoría del incidente seleccionada por el usuario.
     * Ejemplos: "Incendio", "Accidente", "Fuga de Gas", "Rescate"
     */
    @NotBlank
    @Column(name = "tipo_incidente", nullable = false, length = 100)
    private String tipoIncidente;

    /**
     * URL de la foto adjunta almacenada en Cloudinary.
     * Nulo hasta que el ciudadano adjunte evidencia fotográfica.
     * Se poblará cuando se integre Cloudinary en una fase posterior.
     */
    @Column(name = "foto_url", length = 512)
    private String fotoUrl;

    // ─── Geolocalización (WGS-84 / Leaflet compatible) ─────────────────────────

    /**
     * Latitud de la emergencia en grados decimales.
     * BigDecimal con 8 decimales ≈ precisión de ~1mm.
     * Rango válido: -90.0000000 a 90.0000000
     */
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud mínima: -90")
    @DecimalMax(value = "90.0",  message = "Latitud máxima: 90")
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal latitud;

    /**
     * Longitud de la emergencia en grados decimales.
     * BigDecimal con 8 decimales ≈ precisión de ~1mm.
     * Rango válido: -180.0000000 a 180.0000000
     */
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud mínima: -180")
    @DecimalMax(value = "180.0",  message = "Longitud máxima: 180")
    @Column(nullable = false, precision = 12, scale = 8)
    private BigDecimal longitud;

    // ─── Campos reservados para Fase IA (CU14 / CU15) ─────────────────────────

    /**
     * Porcentaje de confianza asignado por el modelo de visión artificial.
     * Rango: 0.0 (sin confianza) a 1.0 (certeza absoluta).
     * Nulo hasta que se integre Spring AI.
     */
    @Column(name = "ia_confianza")
    private Double iaConfianza;

    /**
     * Etiquetas JSON devueltas por el modelo de visión (ej: ["fuego","humo"]).
     * Almacenado como texto para flexibilidad; se parseará en la capa de servicio.
     */
    @Column(name = "ia_etiquetas", columnDefinition = "TEXT")
    private String iaEtiquetas;

    // ─── Auditoría ─────────────────────────────────────────────────────────────

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private EstadoReporte estado = EstadoReporte.EN_COLA;

    /** Descripción libre opcional escrita por el ciudadano */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 50)
    private String prioridad;

    /** Motivo de resolución agregado por el operador */
    @Column(name = "motivo_resolucion", columnDefinition = "TEXT")
    private String motivoResolucion;

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
