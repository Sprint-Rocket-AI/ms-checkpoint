
package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools.CrearListaFormateadaParaBD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormatToListRequest(
        @NotBlank(message = "La columna no puede ser nula o vacía")
        String columna,

        @NotNull(message = "El tipo de dato no puede ser nulo")
        CrearListaFormateadaParaBD.TipoDato tipo,

        @NotBlank(message = "Los valores no pueden ser nulos o vacíos")
        String valores
) {

}



