package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class ObtenerRecordatorioById {

    private static final Logger log = LoggerFactory.getLogger(ObtenerRecordatorioById.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public ObtenerRecordatorioById(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public RecordatorioResponse execute(String id) {
        log.info("Obteniendo recordatorio con id: {}", id);

        return recordatorioPersistencePortOut.findById(id)
                .map(RecordatorioResponse::from)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));
    }
}
