package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.DiagramNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nodo de un diagrama")
public record DiagramNodeDTO(

        @Schema(description = "Identificador del nodo", example = "node-1")
        String id,

        @Schema(description = "Tipo de nodo", example = "default")
        String type,

        @Schema(description = "Etiqueta visible del nodo", example = "Inicio")
        String label,

        @Schema(description = "Posición horizontal en el canvas", example = "100.0")
        Double positionX,

        @Schema(description = "Posición vertical en el canvas", example = "200.0")
        Double positionY

) {
    public static DiagramNodeDTO from(DiagramNode model) {
        return new DiagramNodeDTO(
                model.getId(),
                model.getType(),
                model.getLabel(),
                model.getPositionX(),
                model.getPositionY()
        );
    }

    public DiagramNode toModel() {
        DiagramNode node = new DiagramNode();
        node.setId(this.id);
        node.setType(this.type);
        node.setLabel(this.label);
        node.setPositionX(this.positionX);
        node.setPositionY(this.positionY);
        return node;
    }
}
