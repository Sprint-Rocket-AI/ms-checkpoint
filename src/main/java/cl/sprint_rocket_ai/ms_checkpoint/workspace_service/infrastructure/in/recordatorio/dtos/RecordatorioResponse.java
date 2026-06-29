package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "Respuesta con los datos de un recordatorio")
public record RecordatorioResponse(

        @Schema(description = "Identificador único del recordatorio", example = "665a1b2c3d4e5f6a7b8c9d0f")
        String id,

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        String userId,

        @Schema(description = "Título del recordatorio", example = "Sincronización matutina")
        String titulo,

        @Schema(description = "Indica si el recordatorio está activo", example = "true")
        boolean activo,

        @Schema(description = "Fecha de expiración del recordatorio", example = "2024-12-31")
        LocalDateTime fechaExpiracion,

        @Schema(description = "Fecha de última actualización")
        LocalDateTime fechaCreacion
) {
    public static RecordatorioResponse from(Recordatorio modelo) {
        return new RecordatorioResponse(
                modelo.getId(),
                modelo.getUserId(),
                modelo.getTitulo(),
                modelo.isActivo(),
                modelo.getFechaExpiracion(),
                modelo.getFechaCreacion()
        );
    }
}
