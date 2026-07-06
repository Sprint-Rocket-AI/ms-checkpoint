package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ObtenerDiagramasByUsuario {

    private static final Logger log = LoggerFactory.getLogger(ObtenerDiagramasByUsuario.class);

    private final DiagramMongoRepository diagramRepository;

    public ObtenerDiagramasByUsuario(DiagramMongoRepository diagramRepository) {
        this.diagramRepository = diagramRepository;
    }

    public List<DiagramResponse> execute(String userId) {
        log.info("Buscando diagramas para usuario={}", userId);
        return diagramRepository.findByUserId(userId)
                .stream()
                .map(DiagramResponse::from)
                .toList();
    }
}

