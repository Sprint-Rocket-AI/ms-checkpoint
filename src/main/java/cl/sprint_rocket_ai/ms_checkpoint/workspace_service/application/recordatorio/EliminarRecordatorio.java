package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class EliminarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(EliminarRecordatorio.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public EliminarRecordatorio(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public void execute(String id) {
        log.info("Iniciando eliminación de recordatorio con id: {}", id);

        recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        recordatorioRepository.deleteById(id);

        log.info("Recordatorio eliminado exitosamente con id: {}", id);
    }
}
