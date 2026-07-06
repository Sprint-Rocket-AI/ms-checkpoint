package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiagramMongoRepository extends MongoRepository<Diagram, String> {
	List<Diagram> findByUserId(String userId);
}
