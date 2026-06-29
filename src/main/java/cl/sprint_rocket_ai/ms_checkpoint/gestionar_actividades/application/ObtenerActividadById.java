package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos.ActividadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ObtenerActividadById {

    private static final Logger log = LoggerFactory.getLogger(ObtenerActividadById.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public ObtenerActividadById(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    public ActividadResponse execute(String id) {
        log.info("Obteniendo actividad con id: {}", id);

        return actividadPersistencePortOut.findById(id)
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}
