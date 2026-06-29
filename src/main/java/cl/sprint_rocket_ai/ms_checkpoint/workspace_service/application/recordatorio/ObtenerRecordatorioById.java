package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ObtenerRecordatorioById {

    private static final Logger log = LoggerFactory.getLogger(ObtenerRecordatorioById.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public ObtenerRecordatorioById(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(String id) {
        log.info("Obteniendo recordatorio con id: {}", id);

        return recordatorioRepository.findById(id)
                .map(RecordatorioResponse::from)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));
    }
}
