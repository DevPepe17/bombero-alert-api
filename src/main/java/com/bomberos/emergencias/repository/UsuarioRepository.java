package com.bomberos.emergencias.repository;

import com.bomberos.emergencias.entity.Usuario;
import com.bomberos.emergencias.entity.enums.EstadoUsuario;
import com.bomberos.emergencias.entity.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 *
 * Incluye métodos personalizados para los flujos de:
 * - Autenticación (Spring Security)
 * - Gestión de usuarios (CU10)
 * - Sistema de reputación para detección de falsas alarmas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ─── Búsquedas básicas ──────────────────────────────────────────────────────

    /** CU02: Login - Spring Security busca usuario por email */
    Optional<Usuario> findByEmail(String email);

    /** CU01: Registro - Validar unicidad de DNI */
    Optional<Usuario> findByDni(String dni);

    /** Verificar si existe un email (útil para validación en registro) */
    boolean existsByEmail(String email);

    /** Verificar si existe un DNI (útil para validación en registro) */
    boolean existsByDni(String dni);

    // ─── Búsquedas por estado / rol ────────────────────────────────────────────

    /** CU10: Listar usuarios por estado (activos, baneados, etc.) */
    List<Usuario> findByEstado(EstadoUsuario estado);

    /** CU10: Listar operadores y administradores del sistema */
    List<Usuario> findByRol(Rol rol);

    /** CU10: Listar usuarios baneados para moderación */
    List<Usuario> findByEstadoOrderByNombreAsc(EstadoUsuario estado);

    // ─── Sistema de reputación ─────────────────────────────────────────────────

    /**
     * Obtener ciudadanos con baja reputación (posibles abusadores del sistema).
     * El umbral de reputación puede configurarse como parámetro.
     */
    @Query("SELECT u FROM Usuario u WHERE u.reputacion < :umbral AND u.rol = 'CIUDADANO' ORDER BY u.reputacion ASC")
    List<Usuario> findCiudadanosConBajaReputacion(@Param("umbral") Integer umbral);

    /**
     * CU11: Contar reportes realizados por un usuario (para estadísticas).
     */
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.usuario.id = :idUsuario")
    Long contarReportesPorUsuario(@Param("idUsuario") Long idUsuario);
}
