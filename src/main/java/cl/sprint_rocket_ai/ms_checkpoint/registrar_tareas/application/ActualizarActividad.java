package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActualizarActividadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarActividad {

    private static final Logger log = LoggerFactory.getLogger(ActualizarActividad.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public ActualizarActividad(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    public ActividadResponse execute(String id, ActualizarActividadRequest request) {
        log.info("Iniciando actualización de actividad con id: {}", id);

        return actividadPersistencePortOut.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    existing.setFechaActualizacion(LocalDateTime.now());
                    return actividadPersistencePortOut.save(existing);
                })
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}
