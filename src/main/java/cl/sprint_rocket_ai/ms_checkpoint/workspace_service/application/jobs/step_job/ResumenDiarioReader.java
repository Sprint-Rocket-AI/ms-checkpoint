package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.UsuarioActividadesDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.UsuarioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ResumenDiarioReader {

    private static final Logger log = LoggerFactory.getLogger(ResumenDiarioReader.class);

    private final ActividadMongoRepository actividadRepository;
    private final UsuarioMongoRepository usuarioRepository;

    public ResumenDiarioReader(ActividadMongoRepository actividadRepository,
                               UsuarioMongoRepository usuarioRepository) {
        this.actividadRepository = actividadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioActividadesDto> read(LocalDate fecha) {
        log.info("Reader: Buscando actividades para la fecha '{}'", fecha);
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        List<Actividad> todasActividades = actividadRepository.findByFechaCreacionBetween(desde, hasta);

        if (todasActividades.isEmpty()) {
            log.info("Reader: No se encontraron actividades para la fecha '{}'", fecha);
            return List.of();
        }

        Map<String, List<Actividad>> porUsuario = todasActividades.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        List<UsuarioActividadesDto> resultados = new ArrayList<>();

        porUsuario.forEach((userId, actividades) -> {
            String correo = usuarioRepository.findByUserId(userId)
                    .map(Usuario::getCorreo)
                    .orElse("sin_correo@empresa.com");

            resultados.add(new UsuarioActividadesDto(userId, correo, actividades));
        });

        log.info("Reader: Retornando datos para {} usuarios", resultados.size());
        return resultados;
    }
}
