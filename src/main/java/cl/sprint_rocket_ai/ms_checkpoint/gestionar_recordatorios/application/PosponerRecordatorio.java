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
 * Caso de uso: posponer un recordatorio N minutos.
 *
 * <p>Desplaza {@code proximoEnvio} N minutos hacia adelante desde el momento actual,
 * de forma que el scheduler lo detecte nuevamente cuando vuelva a vencer.
 */
@Service
public final class PosponerRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(PosponerRecordatorio.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public PosponerRecordatorio(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    /**
     * Pospone el recordatorio {@code minutos} minutos a partir de ahora.
     *
     * @param id      identificador del recordatorio
     * @param minutos minutos a posponer (p.ej. 10)
     * @return recordatorio actualizado
     */
    public RecordatorioResponse execute(String id, int minutos) {
        log.info("Posponiendo recordatorio | id='{}' minutos={}", id, minutos);

        Recordatorio recordatorio = recordatorioPersistencePortOut.findById(id)
                .orElseThrow(() -> new RecordatorioNotFoundException(id));

        LocalDateTime nuevoProximoEnvio = LocalDateTime.now().plusMinutes(minutos);
        recordatorio.setProximoEnvio(nuevoProximoEnvio);
        recordatorio.setFechaActualizacion(LocalDateTime.now());

        Recordatorio saved = recordatorioPersistencePortOut.save(recordatorio);
        log.info("Recordatorio pospuesto exitosamente | id='{}' nuevoProximoEnvio='{}'",
                id, nuevoProximoEnvio);
        return RecordatorioResponse.from(saved);
    }
}
