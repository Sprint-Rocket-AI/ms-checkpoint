package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class EliminarActividad {

    private static final Logger log = LoggerFactory.getLogger(EliminarActividad.class);

    private final ActividadMongoRepository actividadRepository;

    public EliminarActividad(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public void execute(String id) {
        log.info("Iniciando eliminación de actividad con id: {}", id);

        actividadRepository.findById(id)
                .orElseThrow(() -> new ActividadNotFoundException(id));

        actividadRepository.deleteById(id);

        log.info("Actividad eliminada exitosamente con id: {}", id);
    }
}
