package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Actividad simplificada para solicitudes de pop-up a IA-ENGINE")
public record ActividadParaPopUp(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        String titulo,

        @Schema(description = "Prioridad de la actividad", example = "ALTA")
        String prioridad
) {
    public static ActividadParaPopUp from(Actividad actividad) {
        return new ActividadParaPopUp(
                actividad.getTitulo(),
                actividad.getPrioridad() != null ? actividad.getPrioridad().name() : null
        );
    }
}
