package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import java.util.List;

public record UsuarioActividadesDto(
        String userId,
        String correo,
        List<Actividad> actividades
) {}
