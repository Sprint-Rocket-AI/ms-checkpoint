package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.CrearActividadRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CrearActividad {

    private static final Logger log = LoggerFactory.getLogger(CrearActividad.class);

    private final ActividadMongoRepository actividadRepository;

    public CrearActividad(ActividadMongoRepository actividadPersistencePortOut) {
        this.actividadRepository = actividadPersistencePortOut;
    }

    public ActividadResponse execute(CrearActividadRequest request) {
        log.info("Iniciando creación de actividad para usuario: {}", request.userId());

        Actividad actividad = new Actividad();
        request.applyTo(actividad);
        actividad.setEstado(EstadoActividad.PENDIENTE);
        actividad.setFechaCreacion(LocalDateTime.now());

        Actividad saved = actividadRepository.save(actividad);

        log.info("Actividad creada exitosamente con id: {}", saved.getId());
        return ActividadResponse.from(saved);
    }
}
