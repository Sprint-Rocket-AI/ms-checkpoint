package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import java.util.List;

public record ResumenUsuarioDto(
        String userId,
        String correo,
        String resumen,
        List<SugerenciaActividad> sugerencias
) {}
