package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para actualizar un recordatorio existente")
public record ActualizarRecordatorioRequest(

        @Schema(description = "Título del recordatorio", example = "Sincronización vespertina")
        String titulo,

        @Schema(description = "Indica si el recordatorio está activo", example = "false")
        Boolean activo,

        @Schema(description = "Fecha de expiración del recordatorio", example = "2025-06-30")
        LocalDateTime fechaExpiracion
) {
    public void applyTo(Recordatorio target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.activo != null) target.setActivo(this.activo);
        if (this.fechaExpiracion != null) target.setFechaExpiracion(this.fechaExpiracion);
    }
}
