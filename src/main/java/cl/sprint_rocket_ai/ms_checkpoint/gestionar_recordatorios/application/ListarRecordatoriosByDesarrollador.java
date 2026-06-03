package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.dtos.RecordatorioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ListarRecordatoriosByDesarrollador {

    private static final Logger log = LoggerFactory.getLogger(ListarRecordatoriosByDesarrollador.class);

    private final RecordatorioPersistencePortOut recordatorioPersistencePortOut;

    public ListarRecordatoriosByDesarrollador(RecordatorioPersistencePortOut recordatorioPersistencePortOut) {
        this.recordatorioPersistencePortOut = recordatorioPersistencePortOut;
    }

    public List<RecordatorioResponse> execute(String userId) {
        log.info("Listando recordatorios del desarrollador: {}", userId);

        return recordatorioPersistencePortOut.findByUserId(userId)
                .stream()
                .map(RecordatorioResponse::from)
                .toList();
    }
}
