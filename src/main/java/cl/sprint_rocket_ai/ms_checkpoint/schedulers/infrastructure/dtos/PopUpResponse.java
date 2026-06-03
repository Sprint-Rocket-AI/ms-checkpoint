package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de IA-ENGINE con contenido del pop-up generado")
public record PopUpResponse(

        @Schema(description = "Título del pop-up generado", example = "Tareas pendientes prioritarias")
        String titulo,

        @Schema(description = "Contenido del pop-up en formato texto/HTML", example = "Tienes 3 tareas pendientes de alta prioridad...")
        String contenido,

        @Schema(description = "Nivel de urgencia del pop-up", example = "ALTA")
        String urgencia
) {
}
