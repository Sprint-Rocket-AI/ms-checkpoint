package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models;

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
