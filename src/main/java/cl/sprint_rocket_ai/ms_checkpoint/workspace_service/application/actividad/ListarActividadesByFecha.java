package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public final class ListarActividadesByFecha {

    private static final Logger log = LoggerFactory.getLogger(ListarActividadesByFecha.class);

    private final ActividadMongoRepository actividadRepository;

    public ListarActividadesByFecha(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public List<ActividadResponse> execute(String userId, LocalDate fecha) {
        log.info("Listando actividades | userId='{}' fecha='{}'", userId, fecha);
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        List<Actividad> actividades = actividadRepository.findByUserIdAndFechaCreacionBetween(userId, desde, hasta);
        log.info("Actividades encontradas: {} | userId='{}' fecha='{}'", actividades.size(), userId, fecha);
        return actividades.stream()
                .map(ActividadResponse::from)
                .toList();
    }
}
