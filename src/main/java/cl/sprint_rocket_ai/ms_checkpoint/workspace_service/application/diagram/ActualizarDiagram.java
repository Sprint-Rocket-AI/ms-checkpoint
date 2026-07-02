package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarDiagram {

    private static final Logger log = LoggerFactory.getLogger(ActualizarDiagram.class);

    private final DiagramMongoRepository diagramRepository;

    public ActualizarDiagram(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public DiagramResponse execute(String id, ActualizarDiagramRequest request) {
        log.info("Iniciando actualización de nombre para diagrama con id: {}", id);

        return diagramRepository.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    existing.setFechaActualizacion(LocalDateTime.now());
                    log.info("Diagrama [{}] actualizado con nuevo nombre: '{}'", id, existing.getName());
                    return diagramRepository.save(existing);
                })
                .map(DiagramResponse::from)
                .orElseThrow(() -> new DiagramNotFoundException(id));
    }
}
