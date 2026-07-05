package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;

public record ActividadResumenDto(
        String titulo,
        String descripcion,
        EstadoActividad estado
) {
    public static ActividadResumenDto from(Actividad actividad) {
        return new ActividadResumenDto(
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getEstado()
        );
    }
}
