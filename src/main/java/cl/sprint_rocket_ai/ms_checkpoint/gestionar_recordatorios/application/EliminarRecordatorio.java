package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class EliminarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(EliminarRecordatorio.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public EliminarRecordatorio(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public void execute(String id) {
        log.info("Iniciando eliminación de recordatorio con id: {}", id);

        recordatorioPersistencePortOut.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        recordatorioPersistencePortOut.deleteById(id);

        log.info("Recordatorio eliminado exitosamente con id: {}", id);
    }
}
