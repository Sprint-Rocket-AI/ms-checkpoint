package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ListarActividadesByDesarrollador {

    private static final Logger log = LoggerFactory.getLogger(ListarActividadesByDesarrollador.class);

    private final ActividadMongoRepository actividadRepository;

    public ListarActividadesByDesarrollador(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public List<ActividadResponse> execute(String userId) {
        log.info("Listando actividades del desarrollador: {}", userId);

        return actividadRepository.findByUserId(userId)
                .stream()
                .map(ActividadResponse::from)
                .toList();
    }
}
