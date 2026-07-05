package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Estacion}.
 */
@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {

    /** Buscar estación por nombre (para autocompletado en panel) */
    List<Estacion> findByNombreContainingIgnoreCase(String nombre);

    /** Buscar estaciones por distrito */
    List<Estacion> findByDistritoIgnoreCase(String distrito);

    /** Obtener estación con sus unidades cargadas (evitar N+1) */
    @Query("SELECT e FROM Estacion e LEFT JOIN FETCH e.unidades WHERE e.id = :id")
    Optional<Estacion> findByIdWithUnidades(@Param("id") Long id);

    /**
     * Encontrar las estaciones más cercanas a un punto dado.
     * Usa la fórmula de distancia euclidiana como aproximación rápida.
     * Para producción con alta precisión, usar PostGIS ST_Distance.
     *
     * @param latitud  Latitud del punto de referencia
     * @param longitud Longitud del punto de referencia
     * @param limite   Número máximo de estaciones a retornar
     */
    @Query("""
        SELECT e FROM Estacion e
        ORDER BY (
            (e.latitud  - :latitud)  * (e.latitud  - :latitud) +
            (e.longitud - :longitud) * (e.longitud - :longitud)
        ) ASC
        """)
    List<Estacion> findEstacionesMasCercanas(
        @Param("latitud")  Double latitud,
        @Param("longitud") Double longitud,
        org.springframework.data.domain.Pageable limite
    );
    @Query("SELECT DISTINCT e FROM Estacion e LEFT JOIN FETCH e.unidades")
    List<Estacion> findAllWithUnidades();
}
