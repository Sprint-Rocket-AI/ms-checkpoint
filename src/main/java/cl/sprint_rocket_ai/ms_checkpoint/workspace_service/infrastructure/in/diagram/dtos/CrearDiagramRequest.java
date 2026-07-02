package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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

        @Schema(description = "Lista de nodos del diagrama")
        List<DiagramNodeDTO> nodes,

        @Schema(description = "Lista de aristas del diagrama")
        List<DiagramEdgeDTO> edges

) {
    public void applyTo(Diagram target) {
        target.setName(this.name);
        target.setDescription(this.description);
        target.setNodes(this.nodes == null ? List.of() :
                this.nodes.stream().map(DiagramNodeDTO::toModel).toList());
        target.setEdges(this.edges == null ? List.of() :
                this.edges.stream().map(DiagramEdgeDTO::toModel).toList());
    }
}
