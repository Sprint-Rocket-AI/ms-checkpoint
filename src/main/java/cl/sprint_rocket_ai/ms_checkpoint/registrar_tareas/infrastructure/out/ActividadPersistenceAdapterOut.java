package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
public final class ActividadPersistenceAdapterOut implements ActividadPersistencePortOut {

    private static final Logger log = LoggerFactory.getLogger(ActividadPersistenceAdapterOut.class);

    private final ActividadMongoRepository actividadMongoRepository;

    public ActividadPersistenceAdapterOut(ActividadMongoRepository actividadMongoRepository) {
        this.actividadMongoRepository = actividadMongoRepository;
    }

    @Override
    public Actividad save(Actividad actividad) {
        log.info("Persistiendo actividad con título: {}", actividad.getTitulo());
        return actividadMongoRepository.save(actividad);
    }

    @Override
    public Optional<Actividad> findById(String id) {
        log.info("Buscando actividad con id: {}", id);
        return actividadMongoRepository.findById(id);
    }

    @Override
    public List<Actividad> findByUserId(String userId) {
        log.info("Buscando actividades del usuario: {}", userId);
        return actividadMongoRepository.findByUserId(userId);
    }

    @Override
    public List<Actividad> findByUserIdAndEstado(String userId, EstadoActividad estado) {
        log.info("Buscando actividades del usuario: {} con estado: {}", userId, estado);
        return actividadMongoRepository.findByUserIdAndEstado(userId, estado);
    }

    @Override
    public List<Actividad> findByEstadoAndFechaCreacionBetween(EstadoActividad estado, LocalDate desde, LocalDate hasta) {
        log.info("Buscando actividades con estado: {} entre {} y {}", estado, desde, hasta);
        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(LocalTime.MAX);
        return actividadMongoRepository.findByEstadoAndFechaCreacionBetween(estado, desdeDateTime, hastaDateTime);
    }

    @Override
    public List<Actividad> findByUserIdOrderByPrioridadAsc(String userId, EstadoActividad estado, int limit) {
        log.info("Buscando top {} actividades pendientes del usuario: {}", limit, userId);
        List<Actividad> actividades = actividadMongoRepository.findByUserIdAndEstadoOrderByPrioridadAsc(userId, estado);
        return actividades.stream().limit(limit).toList();
    }

    @Override
    public void deleteById(String id) {
        log.info("Eliminando actividad con id: {}", id);
        actividadMongoRepository.deleteById(id);
    }

    @Override
    public List<Actividad> findByUserIdAndFecha(String userId, LocalDate fecha) {
        log.info("Buscando actividades del usuario: {} para la fecha: {}", userId, fecha);
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        return actividadMongoRepository.findByUserIdAndFechaCreacionBetween(userId, desde, hasta);
    }

    @Override
    public List<Actividad> findByFecha(LocalDate fecha) {
        log.info("Buscando todas las actividades de la fecha: {}", fecha);
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        return actividadMongoRepository.findByFechaCreacionBetween(desde, hasta);
    }
}

