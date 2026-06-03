package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActividadPersistencePortOut {

    Actividad save(Actividad actividad);

    Optional<Actividad> findById(String id);

    List<Actividad> findByUserId(String userId);

    List<Actividad> findByUserIdAndEstado(String userId, EstadoActividad estado);

    List<Actividad> findByEstadoAndFechaCreacionBetween(EstadoActividad estado, LocalDate desde, LocalDate hasta);

    List<Actividad> findByUserIdOrderByPrioridadAsc(String userId, EstadoActividad estado, int limit);

    void deleteById(String id);
}
