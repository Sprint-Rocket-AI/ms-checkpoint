package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public final class CompletarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(CompletarRecordatorio.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public CompletarRecordatorio(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(String id) {
        log.info("Completando recordatorio | id='{}'", id);

        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        recordatorio.setActivo(false);
        recordatorio.setFechaCreacion(LocalDateTime.now());

        Recordatorio saved = recordatorioRepository.save(recordatorio);
        log.info("Recordatorio completado exitosamente | id='{}'", id);
        return RecordatorioResponse.from(saved);
    }
}
