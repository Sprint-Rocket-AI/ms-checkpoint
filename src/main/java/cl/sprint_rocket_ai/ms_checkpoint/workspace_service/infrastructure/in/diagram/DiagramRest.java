package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.CrearDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Diagramas", description = "Operaciones CRUD para gestionar diagramas del workspace")
public interface DiagramRest {

    @Operation(summary = "Crear diagrama", description = "Registra un nuevo diagrama")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Diagrama creado exitosamente",
                    content = @Content(schema = @Schema(implementation = DiagramResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<DiagramResponse> create(
            @RequestBody @Valid CrearDiagramRequest request);

    @Operation(summary = "Obtener diagrama por ID", description = "Obtiene los detalles de un diagrama por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagrama encontrado",
                    content = @Content(schema = @Schema(implementation = DiagramResponse.class))),
            @ApiResponse(responseCode = "404", description = "Diagrama no encontrado",
                    content = @Content)
    })
    ResponseEntity<DiagramResponse> findById(
            @Parameter(description = "ID del diagrama", required = true) @PathVariable String id);

    @Operation(summary = "Actualizar nombre del diagrama", description = "Actualiza únicamente el nombre de un diagrama existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagrama actualizado",
                    content = @Content(schema = @Schema(implementation = DiagramResponse.class))),
            @ApiResponse(responseCode = "404", description = "Diagrama no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    ResponseEntity<DiagramResponse> updateName(
            @Parameter(description = "ID del diagrama", required = true) @PathVariable String id,
            @RequestBody @Valid ActualizarDiagramRequest request);

    @Operation(summary = "Eliminar diagrama", description = "Elimina un diagrama por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Diagrama eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Diagrama no encontrado",
                    content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID del diagrama", required = true) @PathVariable String id);
}
