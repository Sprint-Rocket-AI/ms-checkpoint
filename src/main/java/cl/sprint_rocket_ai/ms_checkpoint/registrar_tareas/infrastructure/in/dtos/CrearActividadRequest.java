package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Solicitud para crear una nueva actividad")
public record CrearActividadRequest(

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        @NotBlank(message = "El userId es obligatorio")
        String userId,

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Configurar el flujo OAuth2 con provider externo")
        String descripcion,

        @Schema(description = "Tipo de actividad", example = "TAREA")
        @NotNull(message = "El tipo de actividad es obligatorio")
        TipoActividad tipo,

        @Schema(description = "Prioridad de la actividad", example = "ALTA")
        @NotNull(message = "La prioridad es obligatoria")
        Prioridad prioridad,

        @Schema(description = "Identificador del ticket en Jira", example = "SPRINT-1234")
        String ticketJira,

        @Schema(description = "Fecha de vencimiento de la actividad", example = "2024-06-15")
        LocalDate fechaVencimiento,

        @Schema(description = "Etiquetas asociadas a la actividad", example = "[\"backend\", \"autenticacion\"]")
        List<String> etiquetas,

        @Schema(description = "Notas adicionales sobre la actividad", example = "Revisar documentación del provider")
        String notas
) {
    public void applyTo(Actividad target) {
        target.setUserId(this.userId);
        target.setTitulo(this.titulo);
        target.setDescripcion(this.descripcion);
        target.setTipo(this.tipo);
        target.setPrioridad(this.prioridad);
        target.setTicketJira(this.ticketJira);
        target.setFechaVencimiento(this.fechaVencimiento);
        target.setEtiquetas(this.etiquetas);
        target.setNotas(this.notas);
    }
}
