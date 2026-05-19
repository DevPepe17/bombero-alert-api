package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.AsignacionUnidad;
import com.bomberos.emergencias.entity.enums.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link AsignacionUnidad}.
 *
 * Gestiona el historial de despacho de unidades (CU08).
 */
@Repository
public interface AsignacionUnidadRepository extends JpaRepository<AsignacionUnidad, Long> {

    // ─── Por Incidente ─────────────────────────────────────────────────────────

    /** Todas las asignaciones de un incidente */
    List<AsignacionUnidad> findByIncidenteId(Long idIncidente);

    /** Asignaciones activas de un incidente (unidades actualmente en campo) */
    List<AsignacionUnidad> findByIncidenteIdAndEstadoAsignacion(
        Long idIncidente, EstadoAsignacion estado);

    // ─── Por Unidad ────────────────────────────────────────────────────────────

    /** Historial de asignaciones de una unidad específica */
    List<AsignacionUnidad> findByUnidadIdOrderByFechaAsignacionDesc(Long idUnidad);

    /** Asignación activa actual de una unidad (si existe) */
    Optional<AsignacionUnidad> findByUnidadIdAndEstadoAsignacion(
        Long idUnidad, EstadoAsignacion estado);

    /** Verificar si una unidad ya está activamente asignada a algún incidente */
    boolean existsByUnidadIdAndEstadoAsignacion(Long idUnidad, EstadoAsignacion estado);

    // ─── Consultas combinadas ──────────────────────────────────────────────────

    /**
     * Obtener asignaciones activas con unidad e incidente cargados.
     * Usado en el panel de despacho para mostrar el estado operativo completo.
     */
    @Query("""
        SELECT a FROM AsignacionUnidad a
        JOIN FETCH a.unidad u
        JOIN FETCH u.estacion
        JOIN FETCH a.incidente
        WHERE a.estadoAsignacion = 'ACTIVA'
        ORDER BY a.fechaAsignacion DESC
        """)
    List<AsignacionUnidad> findAsignacionesActivasCompletas();

    // ─── Estadísticas (CU11) ───────────────────────────────────────────────────

    /** Contar asignaciones por estado en un período */
    @Query("""
        SELECT COUNT(a) FROM AsignacionUnidad a
        WHERE a.estadoAsignacion = :estado
          AND a.fechaAsignacion BETWEEN :inicio AND :fin
        """)
    Long contarPorEstadoYPeriodo(
        @Param("estado") EstadoAsignacion estado,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin")    LocalDateTime fin
    );

    /** Tiempo promedio de respuesta por unidad (minutos) */
    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (fecha_liberacion - fecha_asignacion)) / 60)
        FROM asignaciones_unidades
        WHERE id_unidad = :idUnidad
          AND estado_asignacion = 'LIBERADA'
        """, nativeQuery = true)
    Double calcularTiempoPromedioRespuestaPorUnidad(@Param("idUnidad") Long idUnidad);
}
