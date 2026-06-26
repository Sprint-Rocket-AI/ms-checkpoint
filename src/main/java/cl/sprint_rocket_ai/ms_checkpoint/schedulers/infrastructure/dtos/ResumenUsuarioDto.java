package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.SugerenciaActividad;
import java.util.List;

public record ResumenUsuarioDto(
        String userId,
        String correo,
        String resumen,
        List<SugerenciaActividad> sugerencias
) {}
