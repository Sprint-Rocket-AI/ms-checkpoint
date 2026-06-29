package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import java.util.List;

public record UsuarioActividadesDto(
        String userId,
        String correo,
        List<Actividad> actividades
) {}
