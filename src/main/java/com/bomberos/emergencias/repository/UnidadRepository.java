package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.Unidad;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Unidad}.
 *
 * Soporta el flujo de despacho de unidades (CU08).
 */
@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    /** Buscar unidad por código único (ej: "B-14") */
    Optional<Unidad> findByCodigo(String codigo);

    /** Verificar existencia de código (para validación en registro) */
    boolean existsByCodigo(String codigo);

    // ─── Por Estación ──────────────────────────────────────────────────────────

    /** Todas las unidades de una estación */
    List<Unidad> findByEstacionId(Long idEstacion);

    /** Unidades disponibles de una estación específica (CU08 - Asignación) */
    List<Unidad> findByEstacionIdAndEstado(Long idEstacion, EstadoUnidad estado);

    // ─── Por Estado (CU08 - Panel de despacho) ─────────────────────────────────

    /** Todas las unidades disponibles en el sistema */
    List<Unidad> findByEstadoOrderByCodigoAsc(EstadoUnidad estado);

    /**
     * Unidades disponibles con su estación cargada en una sola consulta.
     * Evita el problema N+1 al mostrar el panel de despacho.
     */
    @Query("SELECT u FROM Unidad u JOIN FETCH u.estacion WHERE u.estado = :estado ORDER BY u.codigo ASC")
    List<Unidad> findDisponiblesConEstacion(@Param("estado") EstadoUnidad estado);

    /** Contar unidades disponibles por estación */
    @Query("SELECT COUNT(u) FROM Unidad u WHERE u.estacion.id = :idEstacion AND u.estado = 'DISPONIBLE'")
    Long contarDisponiblesPorEstacion(@Param("idEstacion") Long idEstacion);

    // ─── Por Tipo ──────────────────────────────────────────────────────────────

    /** Buscar unidades por tipo (ej: "Autobomba") */
    List<Unidad> findByTipoContainingIgnoreCaseAndEstado(String tipo, EstadoUnidad estado);

    /** Contar unidades por estado (para estadísticas del admin) */
    long countByEstado(EstadoUnidad estado);
}
