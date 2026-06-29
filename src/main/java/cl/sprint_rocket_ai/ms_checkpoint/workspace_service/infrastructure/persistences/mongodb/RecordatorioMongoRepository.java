package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecordatorioMongoRepository extends MongoRepository<Recordatorio, String> {

    List<Recordatorio> findByUserId(String userId);

    List<Recordatorio> findByActivoTrue();

}

