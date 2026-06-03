package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Respuesta con los datos de una actividad")
public record ActividadResponse(

        @Schema(description = "Identificador único de la actividad", example = "665a1b2c3d4e5f6a7b8c9d0e")
        String id,

        @Schema(description = "Identificador del usuario/desarrollador", example = "dev-matias-001")
        String userId,

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Configurar el flujo OAuth2")
        String descripcion,

        @Schema(description = "Tipo de actividad", example = "TAREA")
        TipoActividad tipo,

        @Schema(description = "Prioridad de la actividad", example = "ALTA")
        Prioridad prioridad,

        @Schema(description = "Estado actual de la actividad", example = "PENDIENTE")
        EstadoActividad estado,

        @Schema(description = "Identificador del ticket en Jira", example = "SPRINT-1234")
        String ticketJira,

        @Schema(description = "Fecha de vencimiento", example = "2024-06-15")
        LocalDate fechaVencimiento,

        @Schema(description = "Etiquetas asociadas", example = "[\"backend\", \"autenticacion\"]")
        List<String> etiquetas,

        @Schema(description = "Notas adicionales", example = "Revisar documentación")
        String notas,

        @Schema(description = "Horas reales invertidas", example = "4.5")
        Double horasReales,

        @Schema(description = "Fecha de creación de la actividad")
        LocalDateTime fechaCreacion,

        @Schema(description = "Fecha de última actualización")
        LocalDateTime fechaActualizacion,

        @Schema(description = "Indica si la actividad está vencida", example = "true")
        boolean vencida
) {
    public static ActividadResponse from(Actividad modelo) {
        boolean estaVencida = modelo.getFechaVencimiento() != null
                && LocalDate.now().isAfter(modelo.getFechaVencimiento())
                && modelo.getEstado() != EstadoActividad.COMPLETADA
                && modelo.getEstado() != EstadoActividad.CANCELADA;

        return new ActividadResponse(
                modelo.getId(),
                modelo.getUserId(),
                modelo.getTitulo(),
                modelo.getDescripcion(),
                modelo.getTipo(),
                modelo.getPrioridad(),
                modelo.getEstado(),
                modelo.getTicketJira(),
                modelo.getFechaVencimiento(),
                modelo.getEtiquetas(),
                modelo.getNotas(),
                modelo.getHorasReales(),
                modelo.getFechaCreacion(),
                modelo.getFechaActualizacion(),
                estaVencida
        );
    }
}
