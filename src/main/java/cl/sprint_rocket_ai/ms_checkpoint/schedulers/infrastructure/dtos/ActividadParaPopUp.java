package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Actividad simplificada para solicitudes de pop-up a IA-ENGINE")
public record ActividadParaPopUp(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        String titulo,

        @Schema(description = "Prioridad de la actividad", example = "ALTA")
        String prioridad,

        @Schema(description = "Ticket Jira asociado", example = "SPRINT-1234")
        String ticketJira,

        @Schema(description = "Notas de la actividad", example = "Revisar documentación")
        String notas
) {
    public static ActividadParaPopUp from(Actividad actividad) {
        return new ActividadParaPopUp(
                actividad.getTitulo(),
                actividad.getPrioridad() != null ? actividad.getPrioridad().name() : null,
                actividad.getTicketJira(),
                actividad.getNotas()
        );
    }
}
