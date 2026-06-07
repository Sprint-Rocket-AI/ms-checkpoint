package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import java.time.LocalDate;
import java.util.List;

/**
 * Payload enviado a {@code POST /api/checkpoint/resumen-diario} en ms-ai-engine.
 */
public record ResumenDiarioRequestDto(
        String userId,
        LocalDate fecha,
        List<ActividadResumenDto> actividades
) {
}
