package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;

/**
 * DTO simplificado de actividad para enviar a IA-ENGINE en la solicitud de resumen diario.
 */
public record ActividadResumenDto(
        String titulo,
        String descripcion,
        String estado,
        String prioridad
) {
    public static ActividadResumenDto from(Actividad actividad) {
        return new ActividadResumenDto(
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getEstado() != null ? actividad.getEstado().name() : null,
                actividad.getPrioridad() != null ? actividad.getPrioridad().name() : null
        );
    }
}
