package com.bomberos.emergencias.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Migración de base de datos para eliminar el CHECK CONSTRAINT
 * antiguo en la columna 'estado' de la tabla 'reportes'.
 *
 * El constraint original solo aceptaba valores del enum viejo
 * (PENDIENTE, EN_CAMINO, etc.) y rechaza los nuevos (EN_COLA, ACTIVO...).
 *
 * Se ejecuta antes que el DatabaseSeeder gracias a @Order(0).
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(0)
public class DatabaseMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Ejecutando migración de base de datos...");
        eliminarCheckConstraintEstado();
        log.info("Migración completada.");
    }

    /**
     * Elimina TODOS los check constraints de la tabla reportes
     * para que la columna 'estado' acepte cualquier valor del enum actual.
     */
    private void eliminarCheckConstraintEstado() {
        try {
            // Obtener todos los check constraints de la tabla reportes
            var constraints = jdbcTemplate.queryForList(
                "SELECT conname FROM pg_constraint " +
                "WHERE conrelid = 'reportes'::regclass AND contype = 'c'"
            );

            if (constraints.isEmpty()) {
                log.info("No se encontraron check constraints en 'reportes'. OK.");
                return;
            }

            for (var c : constraints) {
                String constraintName = (String) c.get("conname");
                try {
                    jdbcTemplate.execute(
                        "ALTER TABLE reportes DROP CONSTRAINT IF EXISTS \"" + constraintName + "\""
                    );
                    log.info("Check constraint eliminado: {}", constraintName);
                } catch (Exception e) {
                    log.warn("No se pudo eliminar constraint '{}': {}", constraintName, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Error durante migración de constraints: {}", e.getMessage());
        }
    }
}
