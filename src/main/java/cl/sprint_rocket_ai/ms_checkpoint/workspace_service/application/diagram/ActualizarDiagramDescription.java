package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramDescriptionRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarDiagramDescription {

    private static final Logger log = LoggerFactory.getLogger(ActualizarDiagramDescription.class);

    private final DiagramMongoRepository diagramRepository;

    public ActualizarDiagramDescription(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public DiagramResponse execute(String id, ActualizarDiagramDescriptionRequest request) {
        log.info("Actualizando descripción del diagrama id={}", id);

        return diagramRepository.findById(id)
                .map(existing -> {
                    existing.setDescription(request.description());
                    existing.setFechaActualizacion(LocalDateTime.now());
                    return diagramRepository.save(existing);
                })
                .map(DiagramResponse::from)
                .orElseThrow(() -> new DiagramNotFoundException(id));
    }
}

