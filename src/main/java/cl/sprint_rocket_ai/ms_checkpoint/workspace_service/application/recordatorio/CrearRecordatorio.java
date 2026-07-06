package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CrearRecordatorio {

    private static final Logger log = LoggerFactory.getLogger(CrearRecordatorio.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public CrearRecordatorio(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public RecordatorioResponse execute(CrearRecordatorioRequest request) {
        log.info("Iniciando creación de recordatorio para usuario: {}", request.userId());

        Recordatorio recordatorio = new Recordatorio();
        recordatorio.setActivo(true);
        recordatorio.setFechaCreacion(LocalDateTime.now());
        request.applyTo(recordatorio);

        Recordatorio saved = recordatorioRepository.save(recordatorio);

        log.info("Recordatorio creado exitosamente con id: {}", saved.getId());
        return RecordatorioResponse.from(saved);
    }
}
