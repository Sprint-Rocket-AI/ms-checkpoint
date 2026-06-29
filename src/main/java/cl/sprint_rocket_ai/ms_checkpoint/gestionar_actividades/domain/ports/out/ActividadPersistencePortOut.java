package cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;

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

    /** Todas las actividades de un usuario en un día específico (cualquier estado). */
    List<Actividad> findByUserIdAndFecha(String userId, LocalDate fecha);

    /** Todas las actividades de todos los usuarios en un día específico (para el scheduler). */
    List<Actividad> findByFecha(LocalDate fecha);

    void deleteById(String id);
}

