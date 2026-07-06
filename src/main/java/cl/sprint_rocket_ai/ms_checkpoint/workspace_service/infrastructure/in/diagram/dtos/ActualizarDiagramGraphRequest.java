package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "Solicitud para actualizar nodos y aristas de un diagrama")
public record ActualizarDiagramGraphRequest(

        @Schema(description = "Lista de nodos del diagrama")
        @Valid
        List<DiagramNodeDTO> nodes,

        @Schema(description = "Lista de aristas del diagrama")
        @Valid
        List<DiagramEdgeDTO> edges

) {
    public void applyTo(Diagram target) {
        if (this.nodes != null) {
            target.setNodes(this.nodes.stream().map(DiagramNodeDTO::toModel).toList());
        }
        if (this.edges != null) {
            target.setEdges(this.edges.stream().map(DiagramEdgeDTO::toModel).toList());
        }
    }
}

