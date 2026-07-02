package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools.CrearListaFormateadaParaBD;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record FormatToListRequest(
        @NotBlank(message = "La columna no puede ser nula o vacía")
        String columna,

        @NotNull(message = "El tipo de dato no puede ser nulo")
        CrearListaFormateadaParaBD.TipoDato tipo,

        @NotEmpty(message = "La lista de valores no puede ser nula o vacía")
        Collection<?> valores
) {

}



