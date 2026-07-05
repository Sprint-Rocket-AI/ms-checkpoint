package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Actividad simplificada para solicitudes de pop-up a IA-ENGINE")
public record ActividadParaPopUp(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        String titulo
) {
    public static ActividadParaPopUp from(Actividad actividad) {
        return new ActividadParaPopUp(
                actividad.getTitulo()
        );
    }
}
