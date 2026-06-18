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

        @Schema(description = "Indica si el recordatorio está activo", example = "false")
        Boolean activo,

        @Schema(description = "Fecha de expiración del recordatorio", example = "2025-06-30")
        LocalDateTime fechaExpiracion
) {
    public void applyTo(Recordatorio target) {
        if (this.titulo != null) target.setTitulo(this.titulo);
        if (this.tipoRecordatorio != null) target.setTipoRecordatorio(this.tipoRecordatorio);
        if (this.activo != null) target.setActivo(this.activo);
        if (this.fechaExpiracion != null) target.setFechaExpiracion(this.fechaExpiracion);
    }
}
