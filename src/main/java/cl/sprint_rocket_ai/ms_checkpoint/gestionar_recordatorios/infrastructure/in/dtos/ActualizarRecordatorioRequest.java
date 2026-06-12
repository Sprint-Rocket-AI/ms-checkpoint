package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.DiaSemana;
import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.TipoRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Solicitud para actualizar un recordatorio existente")
public record ActualizarRecordatorioRequest(

        @Schema(description = "Título del recordatorio", example = "Sincronización vespertina")
        String titulo,

        @Schema(description = "Tipo de recordatorio", example = "SEMANAL")
        TipoRecordatorio tipoRecordatorio,

        @Schema(description = "Hora de activación en formato HH:mm", example = "14:00")
        String horaActivacion,

        @Schema(description = "Días de la semana en que se activa")
        List<DiaSemana> diasSemana,

        @Schema(description = "Indica si el recordatorio está activo", example = "false")
        Boolean activo,

        @Schema(description = "Fecha de expiración del recordatorio", example = "2025-06-30")
        LocalDate fechaExpiracion,

        @Schema(description = "Próximo envío programado", example = "2024-05-30T14:30:00")
        LocalDateTime proximoEnvio
) {
    public void applyTo(Recordatorio target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.tipoRecordatorio != null) target.setTipoRecordatorio(this.tipoRecordatorio);
        if (this.horaActivacion != null) {
            target.setHoraActivacion(this.horaActivacion);
            target.setProximoEnvio(null); // Reset para que vuelva a evaluarse hoy si fue pospuesto
        }
        if (this.diasSemana != null) target.setDiasSemana(this.diasSemana);
        if (this.activo != null) target.setActivo(this.activo);
        if (this.fechaExpiracion != null) target.setFechaExpiracion(this.fechaExpiracion);
        if (this.proximoEnvio != null) target.setProximoEnvio(this.proximoEnvio);
    }
}
