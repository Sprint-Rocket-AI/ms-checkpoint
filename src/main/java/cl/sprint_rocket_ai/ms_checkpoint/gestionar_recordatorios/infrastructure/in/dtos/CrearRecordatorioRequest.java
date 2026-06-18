package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.DiaSemana;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Solicitud para crear un nuevo recordatorio")
public record CrearRecordatorioRequest(

                @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001") @NotBlank(message = "El userId es obligatorio") String userId,

                @Schema(description = "Título del recordatorio", example = "Sincronización matutina") @NotBlank(message = "El título es obligatorio") @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres") String titulo,

                @Schema(description = "Tipo de recordatorio", example = "DIARIO") @NotNull(message = "El tipo de recordatorio es obligatorio") TipoRecordatorio tipoRecordatorio,

                @Schema(description = "Fecha de expiración del recordatorio", example = "2024-12-31") LocalDateTime fechaExpiracion) {
        public void applyTo(Recordatorio target) {
                target.setUserId(this.userId);
                target.setTitulo(this.titulo);
                target.setTipoRecordatorio(this.tipoRecordatorio);
                target.setFechaExpiracion(this.fechaExpiracion);
        }
}
