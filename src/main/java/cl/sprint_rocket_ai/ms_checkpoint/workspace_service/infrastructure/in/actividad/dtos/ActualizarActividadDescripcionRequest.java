package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualizar la descripción de una actividad")
public record ActualizarActividadDescripcionRequest(

        @Schema(description = "Nueva descripción de la actividad", example = "Actualizar el flujo OAuth2")
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 3, max = 1000, message = "La descripción debe tener entre 3 y 1000 caracteres")
        String descripcion

) {
    public void applyTo(Actividad target) {
        if (this.descripcion != null) target.setDescripcion(this.descripcion);
    }
}

