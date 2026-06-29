package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class EliminarActividad {

    private static final Logger log = LoggerFactory.getLogger(EliminarActividad.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public EliminarActividad(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    public void execute(String id) {
        log.info("Iniciando eliminación de actividad con id: {}", id);

        actividadPersistencePortOut.findById(id)
                .orElseThrow(() -> new ActividadNotFoundException(id));

        actividadPersistencePortOut.deleteById(id);

        log.info("Actividad eliminada exitosamente con id: {}", id);
    }
}
