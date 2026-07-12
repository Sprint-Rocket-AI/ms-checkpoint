package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con la cláusula SQL IN construida")
public record FormatToListResponse(
        @Schema(description = "Cláusula SQL resultante",
                example = "CUENTA_ID IN ('0-134095','0-134104','0-134119','0-134157')")
        String statement
) {
}

