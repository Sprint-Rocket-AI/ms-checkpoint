package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActualizarActividadRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarActividad {

    private static final Logger log = LoggerFactory.getLogger(ActualizarActividad.class);

    private final ActividadMongoRepository actividadRepository;

    public ActualizarActividad(ActividadMongoRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public ActividadResponse execute(String id, ActualizarActividadRequest request) {
        log.info("Iniciando actualización de actividad con id: {}", id);

        return actividadRepository.findById(id)
                .map(existing -> {
                    String estadoAnterior = existing.getEstado() != null ? existing.getEstado().name() : "N/A";
                    request.applyTo(existing);
                    String estadoNuevo = existing.getEstado() != null ? existing.getEstado().name() : "N/A";
                    
                    if (!estadoAnterior.equals(estadoNuevo)) {
                        log.info("Actividad [{}] '{}' cambió su estado de [{}] a [{}]",
                            id, existing.getTitulo(), estadoAnterior, estadoNuevo);
                    } else {
                        log.info("Actividad [{}] '{}' actualizada (sin cambio de estado)",
                            id, existing.getTitulo());
                    }
                    return actividadRepository.save(existing);
                })
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}
