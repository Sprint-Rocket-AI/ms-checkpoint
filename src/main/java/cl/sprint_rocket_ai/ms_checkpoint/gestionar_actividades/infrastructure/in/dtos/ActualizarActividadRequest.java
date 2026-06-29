package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para actualizar una actividad existente")
public record ActualizarActividadRequest(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2 v2")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Actualizar flujo OAuth2")
        String descripcion,

        @Schema(description = "Tipo de actividad", example = "TAREA")
        TipoActividad tipo,

        @Schema(description = "Prioridad de la actividad", example = "MEDIA")
        Prioridad prioridad,

        @Schema(description = "Estado de la actividad", example = "EN_PROCESO")
        EstadoActividad estado,


        @Schema(description = "Fecha de actualizacion de la actividad", example = "2024-06-20")
        LocalDateTime fechaActualizacion

) {
    public void applyTo(Actividad target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.descripcion != null) target.setDescripcion(this.descripcion);
        if (this.tipo != null) target.setTipo(this.tipo);
        if (this.prioridad != null) target.setPrioridad(this.prioridad);
        if (this.estado != null) target.setEstado(this.estado);
        if (this.fechaActualizacion != null) target.setFechaActualizacion(this.fechaActualizacion);

    }
}
