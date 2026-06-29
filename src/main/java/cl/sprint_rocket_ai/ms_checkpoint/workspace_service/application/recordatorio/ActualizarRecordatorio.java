package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(ActualizarRecordatorio.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public ActualizarRecordatorio(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(String id, ActualizarRecordatorioRequest request) {
        log.info("Iniciando actualización de recordatorio con id: {}", id);

        return recordatorioRepository.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    existing.setFechaCreacion(LocalDateTime.now());
                    return recordatorioRepository.save(existing);
                })
                .map(RecordatorioResponse::from)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));
    }
}
