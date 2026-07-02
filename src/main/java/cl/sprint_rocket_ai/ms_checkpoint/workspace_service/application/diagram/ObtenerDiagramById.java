package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ObtenerDiagramById {

    private static final Logger log = LoggerFactory.getLogger(ObtenerDiagramById.class);

    private final DiagramMongoRepository diagramRepository;

    public ObtenerDiagramById(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public DiagramResponse execute(String id) {
        log.info("Obteniendo diagrama con id: {}", id);

        return diagramRepository.findById(id)
                .map(DiagramResponse::from)
                .orElseThrow(() -> new DiagramNotFoundException(id));
    }
}
