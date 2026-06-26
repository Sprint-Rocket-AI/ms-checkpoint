package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Solicitud para crear un nuevo recordatorio")
public record CrearRecordatorioRequest(

                @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001") @NotBlank(message = "El userId es obligatorio") String userId,

                @Schema(description = "Título del recordatorio", example = "Sincronización matutina") @NotBlank(message = "El título es obligatorio") @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres") String titulo,

                @Schema(description = "Fecha de expiración del recordatorio", example = "2024-12-31") LocalDate fechaExpiracion,
                
                @Schema(description = "Hora de expiración del recordatorio", example = "08:30") LocalTime horaExpiracion) {
        public void applyTo(Recordatorio target) {
                target.setUserId(this.userId);
                target.setTitulo(this.titulo);
                if (this.fechaExpiracion != null && this.horaExpiracion != null) {
                        target.setFechaExpiracion(this.fechaExpiracion.atTime(this.horaExpiracion));
                } else if (this.fechaExpiracion != null) {
                        target.setFechaExpiracion(this.fechaExpiracion.atStartOfDay());
                } else {
                        target.setFechaExpiracion(null);
                }
        }
}
