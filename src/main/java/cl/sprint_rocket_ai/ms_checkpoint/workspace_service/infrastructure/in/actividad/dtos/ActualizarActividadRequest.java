package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para actualizar una actividad existente")
public record ActualizarActividadRequest(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2 v2")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Actualizar flujo OAuth2")
        String descripcion,

        @Schema(description = "Estado de la actividad", example = "EN_PROCESO")
        EstadoActividad estado

) {
    public void applyTo(Actividad target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.descripcion != null) target.setDescripcion(this.descripcion);
        if (this.estado != null) target.setEstado(this.estado);
    }
}
