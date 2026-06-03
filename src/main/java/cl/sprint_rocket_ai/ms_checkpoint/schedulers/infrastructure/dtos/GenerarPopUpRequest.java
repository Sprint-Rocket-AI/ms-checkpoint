package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Solicitud para generar un pop-up con actividades pendientes")
public record GenerarPopUpRequest(

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        String userId,

        @Schema(description = "Lista de actividades simplificadas para el pop-up")
        List<ActividadParaPopUp> actividades
) {
}
