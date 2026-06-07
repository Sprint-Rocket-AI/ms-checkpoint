package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordatorioMongoRepository extends MongoRepository<Recordatorio, String> {

    List<Recordatorio> findByUserId(String userId);

    List<Recordatorio> findByActivoTrue();

    /** Recordatorios activos cuyo proximoEnvio ya ocurrió (vencidos). */
    List<Recordatorio> findByActivoTrueAndProximoEnvioLessThanEqual(LocalDateTime ahora);
}

