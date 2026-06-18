package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out.RecordatorioPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public final class RecordatorioPersistenceAdapterOut implements RecordatorioPersistencePortOut {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioPersistenceAdapterOut.class);

    private final RecordatorioMongoRepository recordatorioMongoRepository;

    public RecordatorioPersistenceAdapterOut(RecordatorioMongoRepository recordatorioMongoRepository) {
        this.recordatorioMongoRepository = recordatorioMongoRepository;
    }

    @Override
    public Recordatorio save(Recordatorio recordatorio) {
        log.info("Persistiendo recordatorio con título: {}", recordatorio.getTitulo());
        return recordatorioMongoRepository.save(recordatorio);
    }

    @Override
    public Optional<Recordatorio> findById(String id) {
        log.info("Buscando recordatorio con id: {}", id);
        return recordatorioMongoRepository.findById(id);
    }

    @Override
    public List<Recordatorio> findByUserId(String userId) {
        log.info("Buscando recordatorios del usuario: {}", userId);
        return recordatorioMongoRepository.findByUserId(userId);
    }

    @Override
    public List<Recordatorio> findByActivoTrue() {
        log.info("Buscando recordatorios activos");
        List<Recordatorio> activos = recordatorioMongoRepository.findByActivoTrue();
        log.info("Total de recordatorios activos detectados por el PersistenceAdapterOut: {}", activos.size());
        return activos;
    }

    @Override
    public void deleteById(String id) {
        log.info("Eliminando recordatorio con id: {}", id);
        recordatorioMongoRepository.deleteById(id);
    }
}

