package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para actualizar la descripción de un diagrama")
public record ActualizarDiagramDescriptionRequest(

        @Schema(description = "Nueva descripción del diagrama", example = "Diagrama actualizado")
        String description

) {
}

