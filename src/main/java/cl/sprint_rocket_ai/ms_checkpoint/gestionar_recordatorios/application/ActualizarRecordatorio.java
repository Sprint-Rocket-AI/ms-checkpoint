package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.ActualizarRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class ActualizarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(ActualizarRecordatorio.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public ActualizarRecordatorio(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public RecordatorioResponse execute(String id, ActualizarRecordatorioRequest request) {
        log.info("Iniciando actualización de recordatorio con id: {}", id);

        return recordatorioPersistencePortOut.findById(id)
                .map(existing -> {
                    request.applyTo(existing);
                    existing.setFechaActualizacion(LocalDateTime.now());
                    return recordatorioPersistencePortOut.save(existing);
                })
                .map(RecordatorioResponse::from)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));
    }
}
