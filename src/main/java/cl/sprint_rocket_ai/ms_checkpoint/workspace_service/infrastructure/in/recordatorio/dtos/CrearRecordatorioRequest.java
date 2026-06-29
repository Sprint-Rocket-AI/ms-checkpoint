package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para crear un nuevo recordatorio")
public record CrearRecordatorioRequest(

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        @NotBlank(message = "El userId es obligatorio")
        String userId,

        @Schema(description = "Título del recordatorio", example = "Sincronización matutina")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
        String titulo,

        @Schema(description = "Fecha y hora de expiración del recordatorio en formato ISO", example = "2026-12-31T23:59:59")
        LocalDateTime fechaExpiracion) {

        public void applyTo(Recordatorio target) {
                target.setUserId(this.userId);
                target.setTitulo(this.titulo);
                target.setFechaExpiracion(this.fechaExpiracion);
        }
}

