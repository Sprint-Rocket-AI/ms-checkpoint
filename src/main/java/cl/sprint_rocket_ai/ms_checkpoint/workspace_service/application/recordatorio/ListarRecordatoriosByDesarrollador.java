package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ListarRecordatoriosByDesarrollador {

    private static final Logger log = LoggerFactory.getLogger(ListarRecordatoriosByDesarrollador.class);

    private final RecordatorioMongoRepository recordatorioRepository;

    public ListarRecordatoriosByDesarrollador(RecordatorioMongoRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    public List<RecordatorioResponse> execute(String userId) {
        log.info("Listando recordatorios del desarrollador: {}", userId);

        return recordatorioRepository.findByUserId(userId)
                .stream()
                .map(RecordatorioResponse::from)
                .toList();
    }
}
