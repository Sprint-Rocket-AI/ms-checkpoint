package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioFechaFinalizacionRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ActualizarRecordatorioFechaFinalizacion {

    private static final Logger log = LoggerFactory.getLogger(ActualizarRecordatorioFechaFinalizacion.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public ActualizarRecordatorioFechaFinalizacion(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(String id, ActualizarRecordatorioFechaFinalizacionRequest request) {
        log.info("Actualizando fecha de finalización de recordatorio id={}", id);

        return recordatorioRepository.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    return recordatorioRepository.save(existing);
                })
                .map(RecordatorioResponse::from)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));
    }
}

