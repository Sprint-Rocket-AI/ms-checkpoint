package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.DiagramEdge;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Arista (conexión) entre nodos de un diagrama")
public record DiagramEdgeDTO(

        @Schema(description = "Identificador de la arista", example = "edge-1")
        String id,

        @Schema(description = "Nodo origen", example = "node-1")
        String source,

        @Schema(description = "Nodo destino", example = "node-2")
        String target,

        @Schema(description = "Etiqueta visible de la arista", example = "flujo principal")
        String label,

        @Schema(description = "Tipo de arista", example = "smoothstep")
        String type

) {
    public static DiagramEdgeDTO from(DiagramEdge model) {
        return new DiagramEdgeDTO(
                model.getId(),
                model.getSource(),
                model.getTarget(),
                model.getLabel(),
                model.getType()
        );
    }

    public DiagramEdge toModel() {
        DiagramEdge edge = new DiagramEdge();
        edge.setId(this.id);
        edge.setSource(this.source);
        edge.setTarget(this.target);
        edge.setLabel(this.label);
        edge.setType(this.type);
        return edge;
    }
}
