package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActividadMongoRepository extends MongoRepository<Actividad, String> {

    List<Actividad> findByUserId(String userId);

    List<Actividad> findByUserIdAndEstado(String userId, EstadoActividad estado);

    List<Actividad> findByEstadoAndFechaCreacionBetween(EstadoActividad estado, LocalDateTime desde, LocalDateTime hasta);

    /** Todas las actividades de un usuario en un rango de fechas (cualquier estado). */
    List<Actividad> findByUserIdAndFechaCreacionBetween(String userId, LocalDateTime desde, LocalDateTime hasta);

    /** Actividades de todos los usuarios en un rango de fechas (para el scheduler). */
    List<Actividad> findByFechaCreacionBetween(LocalDateTime desde, LocalDateTime hasta);
}
