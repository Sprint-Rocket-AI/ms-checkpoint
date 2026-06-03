package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.persistences.mongodb;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActividadMongoRepository extends MongoRepository<Actividad, String> {

    List<Actividad> findByUserId(String userId);

    List<Actividad> findByUserIdAndEstado(String userId, EstadoActividad estado);

    List<Actividad> findByEstadoAndFechaCreacionBetween(EstadoActividad estado, LocalDateTime desde, LocalDateTime hasta);

    List<Actividad> findByUserIdAndEstadoOrderByPrioridadAsc(String userId, EstadoActividad estado);
}
