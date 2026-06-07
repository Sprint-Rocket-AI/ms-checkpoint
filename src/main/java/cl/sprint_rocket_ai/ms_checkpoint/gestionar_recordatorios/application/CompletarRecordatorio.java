package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso: marcar un recordatorio como completado.
 *
 * <p>Desactiva el recordatorio ({@code activo = false}) sin eliminarlo,
 * preservando el historial. No volverá a dispararse.
 */
@Service
public final class CompletarRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(CompletarRecordatorio.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public CompletarRecordatorio(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public RecordatorioResponse execute(String id) {
        log.info("Completando recordatorio | id='{}'", id);

        Recordatorio recordatorio = recordatorioPersistencePortOut.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        recordatorio.setActivo(false);
        recordatorio.setFechaActualizacion(LocalDateTime.now());

        Recordatorio saved = recordatorioPersistencePortOut.save(recordatorio);
        log.info("Recordatorio completado exitosamente | id='{}'", id);
        return RecordatorioResponse.from(saved);
    }
}
