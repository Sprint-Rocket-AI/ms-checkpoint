package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ObtenerActividadById {

    private static final Logger log = LoggerFactory.getLogger(ObtenerActividadById.class);

    private final ActividadMongoRepository actividadRepository;

    public ObtenerActividadById(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public ActividadResponse execute(String id) {
        log.info("Obteniendo actividad con id: {}", id);

        return actividadRepository.findById(id)
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}
