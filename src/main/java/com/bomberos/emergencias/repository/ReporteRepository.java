package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.Reporte;
import com.bomberos.emergencias.entity.enums.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Reporte}.
 *
 * Contiene los métodos clave para:
 * - Seguimiento del estado del reporte por el ciudadano (CU04)
 * - Panel del operador (CU05)
 * - Detección de duplicados geográficos (CU13)
 * - Estadísticas por distrito (CU11)
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // ─── Por Usuario (CU04) ────────────────────────────────────────────────────

    /** Historial de reportes de un ciudadano, ordenado del más reciente al más antiguo */
    List<Reporte> findByUsuarioIdOrderByTimestampDesc(Long idUsuario);

    /** Reportes de un usuario filtrados por estado */
    List<Reporte> findByUsuarioIdAndEstado(Long idUsuario, EstadoReporte estado);

    // ─── Por Estado (CU05 - Panel del Operador) ────────────────────────────────

    /** Todos los reportes pendientes de revisión, más antiguos primero (FIFO) */
    List<Reporte> findByEstadoOrderByTimestampAsc(EstadoReporte estado);

    /** Todos los reportes en estados activos (no terminales) para el panel del operador */
    @Query("SELECT r FROM Reporte r WHERE r.estado NOT IN :excluidos ORDER BY r.timestamp ASC")
    List<Reporte> findReportesActivos(@Param("excluidos") List<EstadoReporte> excluidos);

    /** Reportes sin incidente asignado aún (huérfanos, pendientes de agrupación) */
    List<Reporte> findByIncidenteIsNullAndEstado(EstadoReporte estado);

    // ─── Por Incidente ─────────────────────────────────────────────────────────

    /** Todos los reportes vinculados a un incidente específico */
    List<Reporte> findByIncidenteId(Long idIncidente);

    // ─── Detección de Duplicados Geográficos (CU13) ────────────────────────────

    /**
     * Busca reportes recientes dentro de un radio geográfico aproximado.
     *
     * Utiliza una caja de coordenadas (bounding box) como primera aproximación.
     * Para radios precisos se recomienda PostGIS en fases posteriores.
     *
     * @param latMin  Latitud mínima del área de búsqueda
     * @param latMax  Latitud máxima del área de búsqueda
     * @param lonMin  Longitud mínima del área de búsqueda
     * @param lonMax  Longitud máxima del área de búsqueda
     * @param desde   Límite temporal (ej: últimos 30 minutos)
     * @param estado  Estado del reporte a considerar
     */
    @Query("""
        SELECT r FROM Reporte r
        WHERE r.latitud  BETWEEN :latMin AND :latMax
          AND r.longitud BETWEEN :lonMin AND :lonMax
          AND r.timestamp >= :desde
          AND r.estado = :estado
        ORDER BY r.timestamp DESC
        """)
    List<Reporte> findReportesEnZonaYTiempo(
        @Param("latMin")  BigDecimal latMin,
        @Param("latMax")  BigDecimal latMax,
        @Param("lonMin")  BigDecimal lonMin,
        @Param("lonMax")  BigDecimal lonMax,
        @Param("desde")   LocalDateTime desde,
        @Param("estado")  EstadoReporte estado
    );

    // ─── Estadísticas (CU11) ───────────────────────────────────────────────────

    /** Contar reportes por estado (para el dashboard de métricas) */
    Long countByEstado(EstadoReporte estado);

    /** Contar reportes en un rango de fechas */
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.timestamp BETWEEN :inicio AND :fin")
    Long contarReportesPorPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin")    LocalDateTime fin
    );

    /** Reportes de los últimos N días para el dashboard */
    @Query("SELECT r FROM Reporte r WHERE r.timestamp >= :desde ORDER BY r.timestamp DESC")
    List<Reporte> findReportesRecientes(@Param("desde") LocalDateTime desde);
}
