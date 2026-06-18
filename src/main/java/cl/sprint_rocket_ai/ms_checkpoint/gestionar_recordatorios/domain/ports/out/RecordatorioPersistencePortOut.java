package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.models.Recordatorio;

import java.util.List;
import java.util.Optional;

public interface RecordatorioPersistencePortOut {

    Recordatorio save(Recordatorio recordatorio);

    Optional<Recordatorio> findById(String id);

    List<Recordatorio> findByUserId(String userId);

    List<Recordatorio> findByActivoTrue();

    void deleteById(String id);
}

