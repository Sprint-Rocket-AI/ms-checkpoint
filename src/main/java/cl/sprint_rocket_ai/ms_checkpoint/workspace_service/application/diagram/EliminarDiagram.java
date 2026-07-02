package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class EliminarDiagram {

    private static final Logger log = LoggerFactory.getLogger(EliminarDiagram.class);

    private final DiagramMongoRepository diagramRepository;

    public EliminarDiagram(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public void execute(String id) {
        log.info("Iniciando eliminación de diagrama con id: {}", id);

        diagramRepository.findById(id)
                .orElseThrow(() -> new DiagramNotFoundException(id));

        diagramRepository.deleteById(id);

        log.info("Diagrama eliminado exitosamente con id: {}", id);
    }
}
