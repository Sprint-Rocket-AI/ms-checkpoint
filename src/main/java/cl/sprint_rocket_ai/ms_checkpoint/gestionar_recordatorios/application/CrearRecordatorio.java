package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CrearRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(CrearRecordatorio.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public CrearRecordatorio(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public RecordatorioResponse execute(CrearRecordatorioRequest request) {
        log.info("Iniciando creación de recordatorio para usuario: {}", request.userId());

        Recordatorio recordatorio = new Recordatorio();
        request.applyTo(recordatorio);
        recordatorio.setActivo(true);

        Recordatorio saved = recordatorioPersistencePortOut.save(recordatorio);

        log.info("Recordatorio creado exitosamente con id: {}", saved.getId());
        return RecordatorioResponse.from(saved);
    }
}
