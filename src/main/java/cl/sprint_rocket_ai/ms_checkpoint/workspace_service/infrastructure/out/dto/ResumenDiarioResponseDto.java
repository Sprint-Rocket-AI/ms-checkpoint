package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import java.util.List;

/**
 * Respuesta recibida de {@code POST /api/checkpoint/resumen-diario} en ms-ai-engine.
 */
public record ResumenDiarioResponseDto(
        String resumen,
        List<SugerenciaActividadDto> sugerencias
) {
}
