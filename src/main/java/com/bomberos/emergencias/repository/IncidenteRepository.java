package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.Incidente;
import com.bomberos.emergencias.entity.enums.EstadoIncidente;
import com.bomberos.emergencias.entity.enums.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Incidente}.
 *
 * Soporta el panel de monitoreo en tiempo real (CU05),
 * la gestión de prioridades (CU07) y el cierre de casos (CU09).
 */
@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    // ─── Por Estado (CU05 - Mapa en tiempo real) ───────────────────────────────

    /** Incidentes activos para mostrar en el mapa del operador */
    List<Incidente> findByEstadoOrderByFechaInicioDesc(EstadoIncidente estado);

    /** Incidentes activos o en atención (todos los no cerrados) */
    @Query("SELECT i FROM Incidente i WHERE i.estado IN ('ACTIVO', 'EN_ATENCION') ORDER BY i.fechaInicio DESC")
    List<Incidente> findIncidentesAbiertos();

    // ─── Por Prioridad (CU07) ──────────────────────────────────────────────────

    /** Incidentes filtrados por prioridad final */
    List<Incidente> findByPrioridadFinalOrderByFechaInicioDesc(Prioridad prioridad);

    /** Incidentes críticos activos (máxima urgencia) */
    @Query("""
        SELECT i FROM Incidente i
        WHERE i.prioridadFinal = 'CRITICA'
          AND i.estado IN ('ACTIVO', 'EN_ATENCION')
        ORDER BY i.fechaInicio ASC
        """)
    List<Incidente> findIncidentesCriticosActivos();

    // ─── Por Operador ──────────────────────────────────────────────────────────

    /** Incidentes asignados a un operador específico */
    List<Incidente> findByOperadorIdOrderByFechaInicioDesc(Long idOperador);

    // ─── Estadísticas (CU11) ───────────────────────────────────────────────────

    /** Contar incidentes por estado */
    Long countByEstado(EstadoIncidente estado);

    /** Incidentes cerrados en un rango de fechas (para reportes históricos) */
    @Query("""
        SELECT i FROM Incidente i
        WHERE i.estado = 'CERRADO'
          AND i.fechaCierre BETWEEN :inicio AND :fin
        ORDER BY i.fechaCierre DESC
        """)
    List<Incidente> findIncidentesCerradosEnPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin")    LocalDateTime fin
    );

    /** Tiempo promedio de atención (en minutos) para estadísticas */
    @Query(value = """
        SELECT AVG(EXTRACT(EPOCH FROM (fecha_cierre - fecha_inicio)) / 60)
        FROM incidentes
        WHERE estado = 'CERRADO'
          AND fecha_cierre BETWEEN :inicio AND :fin
        """, nativeQuery = true)
    Double calcularTiempoPromedioAtencion(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin")    LocalDateTime fin
    );
}
