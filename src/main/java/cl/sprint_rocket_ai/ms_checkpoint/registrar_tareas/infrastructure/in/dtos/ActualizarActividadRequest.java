package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.Prioridad;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Solicitud para actualizar una actividad existente")
public record ActualizarActividadRequest(

        @Schema(description = "Título de la actividad", example = "Implementar autenticación OAuth2 v2")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Actualizar flujo OAuth2")
        String descripcion,

        @Schema(description = "Tipo de actividad", example = "TAREA")
        TipoActividad tipo,

        @Schema(description = "Prioridad de la actividad", example = "MEDIA")
        Prioridad prioridad,

        @Schema(description = "Estado de la actividad", example = "EN_PROCESO")
        EstadoActividad estado,

        @Schema(description = "Identificador del ticket en Jira", example = "SPRINT-1234")
        String ticketJira,

        @Schema(description = "Fecha de vencimiento de la actividad", example = "2024-06-20")
        LocalDate fechaVencimiento,

        @Schema(description = "Etiquetas asociadas a la actividad", example = "[\"backend\"]")
        List<String> etiquetas,

        @Schema(description = "Notas adicionales sobre la actividad", example = "Revisar cambios")
        String notas,

        @Schema(description = "Horas reales invertidas en la actividad", example = "4.5")
        Double horasReales
) {
    public void applyTo(Actividad target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.descripcion != null) target.setDescripcion(this.descripcion);
        if (this.tipo != null) target.setTipo(this.tipo);
        if (this.prioridad != null) target.setPrioridad(this.prioridad);
        if (this.estado != null) target.setEstado(this.estado);
        if (this.ticketJira != null) target.setTicketJira(this.ticketJira);
        if (this.fechaVencimiento != null) target.setFechaVencimiento(this.fechaVencimiento);
        if (this.etiquetas != null) target.setEtiquetas(this.etiquetas);
        if (this.notas != null) target.setNotas(this.notas);
        if (this.horasReales != null) target.setHorasReales(this.horasReales);
    }
}
