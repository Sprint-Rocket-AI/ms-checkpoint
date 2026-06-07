package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models;

import java.util.List;

/**
 * Resultado del análisis diario de actividades realizado por IA-ENGINE.
 * Contiene el resumen ejecutivo y las sugerencias de continuidad.
 */
public record ResumenDiarioResult(
        String resumen,
        List<SugerenciaActividad> sugerencias
) {
}
