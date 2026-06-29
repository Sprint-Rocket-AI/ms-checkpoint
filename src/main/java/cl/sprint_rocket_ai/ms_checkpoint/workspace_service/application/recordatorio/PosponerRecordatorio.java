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
public final class PosponerRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(PosponerRecordatorio.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public PosponerRecordatorio(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(String id, int minutos) {
        log.info("Posponiendo recordatorio | id='{}' minutos={}", id, minutos);

        Recordatorio recordatorio = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        LocalDateTime nuevoProximoEnvio = LocalDateTime.now().plusMinutes(minutos);
        recordatorio.setFechaCreacion(LocalDateTime.now());

        Recordatorio saved = recordatorioRepository.save(recordatorio);
        log.info("Recordatorio pospuesto exitosamente | id='{}' nuevoProximoEnvio='{}'",
                id, nuevoProximoEnvio);
        return RecordatorioResponse.from(saved);
    }
}
