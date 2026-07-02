package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.CrearDiagramRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CrearDiagram {

    private static final Logger log = LoggerFactory.getLogger(CrearDiagram.class);

    private final DiagramMongoRepository diagramRepository;

    public CrearDiagram(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public DiagramResponse execute(CrearDiagramRequest request) {
        log.info("Iniciando creación de diagrama con nombre: {}", request.name());

        Diagram diagram = new Diagram();
        request.applyTo(diagram);
        diagram.setFechaCreacion(LocalDateTime.now());

        Diagram saved = diagramRepository.save(diagram);

        log.info("Diagrama creado exitosamente con id: {}", saved.getId());
        return DiagramResponse.from(saved);
    }
}
