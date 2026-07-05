package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Tools", description = "Utilidades genéricas para el formateo y transformación de datos")
public interface ToolsRest {

    @Operation(summary = "Construir sentencia SQL IN",
            description = "Recibe una columna, un tipo (STRING o INT) y una lista de valores; " +
                    "retorna la sentencia IN formateada eliminando duplicados y preservando el orden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sentencia construida exitosamente",
                    content = @Content(schema = @Schema(implementation = FormatToListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Sentencia inválida",
                    content = @Content)
    })
    ResponseEntity<FormatToListResponse> formatearIn(@RequestBody @Valid FormatToListRequest request);
}

