package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.infrastructure.in.dtos.CrearActividadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CrearActividad {

    private static final Logger log = LoggerFactory.getLogger(CrearActividad.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public CrearActividad(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    public ActividadResponse execute(CrearActividadRequest request) {
        log.info("Iniciando creación de actividad para usuario: {}", request.userId());

        Actividad actividad = new Actividad();
        request.applyTo(actividad);
        actividad.setEstado(EstadoActividad.PENDIENTE);
        actividad.setFechaCreacion(LocalDateTime.now());

        Actividad saved = actividadPersistencePortOut.save(actividad);

        log.info("Actividad creada exitosamente con id: {}", saved.getId());
        return ActividadResponse.from(saved);
    }
}
