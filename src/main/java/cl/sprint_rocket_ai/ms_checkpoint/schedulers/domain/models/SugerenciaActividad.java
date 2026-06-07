package cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models;

/**
 * Value object que representa una actividad sugerida por la IA.
 * Devuelta como parte del {@link ResumenDiarioResult}.
 */
public record SugerenciaActividad(
        String titulo,
        String descripcion,
        String prioridad,
        String razon
) {
}
