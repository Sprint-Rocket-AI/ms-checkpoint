package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.step_job;

import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.models.Usuario;
import cl.sprint_rocket_ai.ms_checkpoint.obtener_usuarios.domain.ports.out.UsuarioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.UsuarioActividadesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ResumenDiarioReader {

    private static final Logger log = LoggerFactory.getLogger(ResumenDiarioReader.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;
    private final UsuarioPersistencePortOut usuarioPersistencePortOut;

    public ResumenDiarioReader(ActividadPersistencePortOut actividadPersistencePortOut,
                               UsuarioPersistencePortOut usuarioPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
        this.usuarioPersistencePortOut = usuarioPersistencePortOut;
    }

    public List<UsuarioActividadesDto> read(LocalDate fecha) {
        log.info("Reader: Buscando actividades para la fecha '{}'", fecha);
        List<Actividad> todasActividades = actividadPersistencePortOut.findByFecha(fecha);

        if (todasActividades.isEmpty()) {
            log.info("Reader: No se encontraron actividades para la fecha '{}'", fecha);
            return List.of();
        }

        Map<String, List<Actividad>> porUsuario = todasActividades.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        List<UsuarioActividadesDto> resultados = new ArrayList<>();

        porUsuario.forEach((userId, actividades) -> {
            String correo = usuarioPersistencePortOut.findByUserId(userId)
                    .map(Usuario::getCorreo)
                    .orElse("sin_correo@empresa.com");

            resultados.add(new UsuarioActividadesDto(userId, correo, actividades));
        });

        log.info("Reader: Retornando datos para {} usuarios", resultados.size());
        return resultados;
    }
}
