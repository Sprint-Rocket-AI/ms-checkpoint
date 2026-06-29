package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos.ActualizarActividadRequest;
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
                    String estadoAnterior = existing.getEstado() != null ? existing.getEstado().name() : "N/A";
                    request.applyTo(existing);
                    String estadoNuevo = existing.getEstado() != null ? existing.getEstado().name() : "N/A";
                    
                    if (!estadoAnterior.equals(estadoNuevo)) {
                        log.info("✅ Actividad [{}] '{}' cambió su estado de [{}] a [{}]", 
                            id, existing.getTitulo(), estadoAnterior, estadoNuevo);
                    } else {
                        log.info("📝 Actividad [{}] '{}' actualizada (sin cambio de estado)", 
                            id, existing.getTitulo());
                    }

                    existing.setFechaActualizacion(LocalDateTime.now());
                    return actividadPersistencePortOut.save(existing);
                })
                .map(ActividadResponse::from)
                .orElseThrow(() -> new ActividadNotFoundException(id));
    }
}
