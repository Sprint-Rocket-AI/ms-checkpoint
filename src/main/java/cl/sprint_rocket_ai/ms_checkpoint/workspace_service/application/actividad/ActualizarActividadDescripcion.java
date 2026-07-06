package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActualizarActividadDescripcionRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ActualizarActividadDescripcion {

    private static final Logger log = LoggerFactory.getLogger(ActualizarActividadDescripcion.class);

    private final ActividadMongoRepository actividadRepository;

    public ActualizarActividadDescripcion(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public ActividadResponse execute(String id, ActualizarActividadDescripcionRequest request) {
        log.info("Actualizando descripción de actividad id={}", id);

        return actividadRepository.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    return actividadRepository.save(existing);
                })
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}

