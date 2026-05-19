package com.bomberos.emergencias.entity.enums;

/**
 * Estados posibles de un Ticket de emergencia.
 * Flujo: EN_COLA → ACTIVO → RESUELTO | CANCELADO
 *        EN_COLA | ACTIVO → PENDIENTE → ACTIVO → RESUELTO | CANCELADO
 *
 * Valores legacy mantenidos para compatibilidad con datos existentes en BD.
 */
public enum EstadoReporte {
    EN_COLA,    // Estado inicial cuando el ciudadano crea el reporte
    ACTIVO,     // El operador aceptó y está atendiendo el incidente
    PENDIENTE,  // El operador marcó el ticket como pendiente de revisión
    RESUELTO,   // El incidente fue resuelto exitosamente
    CANCELADO,  // El ticket fue cancelado (falsa alarma, duplicado, etc.)

    // ── Legacy (compatibilidad con datos anteriores en BD) ──────────────────
    RECIBIDO,
    EN_CAMINO,
    ATENDIDO,
    FALSA_ALARMA
}
