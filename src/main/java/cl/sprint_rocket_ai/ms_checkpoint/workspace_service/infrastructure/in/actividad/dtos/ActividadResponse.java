package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta con los datos de una actividad")
public record ActividadResponse(

        @Schema(description = "Identificador único de la actividad", example = "665a1b2c3d4e5f6a7b8c9d0e")
        String id,

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        String userId,

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Configurar el flujo OAuth2")
        String descripcion,

        @Schema(description = "Estado de la actividad", example = "Pendiente / En Curso / Completada / Cancelada")
        EstadoActividad estado,

        @Schema(description = "Fecha de creación de la actividad")
        LocalDateTime fechaCreacion

) {
    public static ActividadResponse from(Actividad modelo) {

        return new ActividadResponse(
                modelo.getId(),
                modelo.getUserId(),
                modelo.getTitulo(),
                modelo.getDescripcion(),
                modelo.getEstado(),
                modelo.getFechaCreacion()
        );
    }
}
