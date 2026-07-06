package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualizar el título de un recordatorio")
public record ActualizarRecordatorioTituloRequest(

        @Schema(description = "Nuevo título del recordatorio", example = "Sincronización vespertina")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
        String titulo

) {
    public void applyTo(Recordatorio target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
    }
}

