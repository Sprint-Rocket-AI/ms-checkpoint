package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramGraphRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarDiagramGraph {

    private static final Logger log = LoggerFactory.getLogger(ActualizarDiagramGraph.class);

    private final DiagramMongoRepository diagramRepository;

    public ActualizarDiagramGraph(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public DiagramResponse execute(String id, ActualizarDiagramGraphRequest request) {
        log.info("Actualizando grafo del diagrama id={}", id);

        return diagramRepository.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    existing.setFechaActualizacion(LocalDateTime.now());
                    return diagramRepository.save(existing);
                })
                .map(DiagramResponse::from)
                .orElseThrow(() -> new DiagramNotFoundException(id));
    }
}

