package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoDiagrama;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Respuesta con los datos de un diagrama")
public record DiagramResponse(

        @Schema(description = "Identificador único del diagrama", example = "665a1b2c3d4e5f6a7b8c9d0e")
        String id,

        @Schema(description = "Nombre del diagrama", example = "Flujo de autenticación")
        String name,

        @Schema(description = "Descripción del diagrama", example = "Diagrama del flujo OAuth2")
        String description,

        @Schema(description = "Lista de nodos del diagrama")
        List<DiagramNodeDTO> nodes,

        @Schema(description = "Lista de aristas del diagrama")
        List<DiagramEdgeDTO> edges,

        @Schema(description = "Fecha de creación del diagrama")
        LocalDateTime fechaCreacion,

        @Schema(description = "Fecha de última actualización")
        LocalDateTime fechaActualizacion,

        @Schema(description = "Tipo de diagrama")
        TipoDiagrama tipo

 ) {
    public static DiagramResponse from(Diagram model) {
        List<DiagramNodeDTO> nodes = model.getNodes() == null ? List.of() :
                model.getNodes().stream().map(DiagramNodeDTO::from).toList();

        List<DiagramEdgeDTO> edges = model.getEdges() == null ? List.of() :
                model.getEdges().stream().map(DiagramEdgeDTO::from).toList();

        return new DiagramResponse(
                model.getId(),
                model.getName(),
                model.getDescription(),
                nodes,
                edges,
                model.getFechaCreacion(),
                model.getFechaActualizacion(),
                model.getTipo()
        );
    }
}
