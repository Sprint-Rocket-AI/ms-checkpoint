package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para actualizar la fecha de finalización de un recordatorio")
public record ActualizarRecordatorioFechaFinalizacionRequest(

        @Schema(description = "Nueva fecha de finalización / expiración", example = "2026-07-31T23:59:59")
        LocalDateTime fechaExpiracion

) {
    public void applyTo(Recordatorio target) {
        if (this.fechaExpiracion != null) target.setFechaExpiracion(this.fechaExpiracion);
    }
}

