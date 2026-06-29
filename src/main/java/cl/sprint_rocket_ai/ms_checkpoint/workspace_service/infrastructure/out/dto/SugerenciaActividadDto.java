package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;

/**
 * DTO de transporte para una sugerencia de actividad recibida de ms-ai-engine.
 * Se convierte al value object de dominio {@link SugerenciaActividad} via {@link #toDomain()}.
 */
public record SugerenciaActividadDto(
        String titulo,
        String descripcion,
        String prioridad,
        String razon
) {
    public SugerenciaActividad toDomain() {
        return new SugerenciaActividad(titulo, descripcion, prioridad, razon);
    }
}
