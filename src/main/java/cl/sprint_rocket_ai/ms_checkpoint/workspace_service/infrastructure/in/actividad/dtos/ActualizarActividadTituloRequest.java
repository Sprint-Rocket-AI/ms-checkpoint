package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualizar el título de una actividad")
public record ActualizarActividadTituloRequest(

        @Schema(description = "Nuevo título de la actividad", example = "Implementar autenticación OAuth2 v2")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
        String titulo

) {
    public void applyTo(Actividad target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
    }
}

