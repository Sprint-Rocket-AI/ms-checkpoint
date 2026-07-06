package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoDiagrama;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Solicitud para crear un nuevo diagrama")
public record CrearDiagramRequest(

        @Schema(description = "Nombre del diagrama", example = "Flujo de autenticación")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
        String name,

        @Schema(description = "Descripción del diagrama", example = "Diagrama del flujo OAuth2")
        String description,


        @Schema(description = "Tipo de diagrama", example = "FLUJO")
        @NotNull(message = "El tipo de diagrama es obligatorio")
        TipoDiagrama tipo,

        @Schema(description = "ID del usuario desarrollador", example = "user-123")
        @NotBlank(message = "El ID del usuario es obligatorio")
        String userId

) {
    public void applyTo(Diagram target) {
        target.setName(this.name);
        target.setDescription(this.description);
        target.setTipo(this.tipo);
        target.setUserId(this.userId);
    }
}
