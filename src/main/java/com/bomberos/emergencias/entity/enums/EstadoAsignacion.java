package com.bomberos.emergencias.entity.enums;

/**
 * Estado de la asignación de una unidad a un incidente.
 */
public enum EstadoAsignacion {
    ACTIVA,       // Unidad actualmente asignada al incidente
    LIBERADA,     // Unidad liberada tras atender el incidente
    CANCELADA     // Asignación cancelada por el operador
}
