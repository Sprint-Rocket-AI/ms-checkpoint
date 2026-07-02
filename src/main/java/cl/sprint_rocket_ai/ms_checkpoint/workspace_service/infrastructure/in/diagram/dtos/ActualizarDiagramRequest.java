package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualizar el nombre de un diagrama")
public record ActualizarDiagramRequest(

        @Schema(description = "Nuevo nombre del diagrama", example = "Flujo de autenticación v2")
        @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
        String name

) {
    public void applyTo(Diagram target) {
        if (this.name != null) target.setName(this.name);
    }
}
