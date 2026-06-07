package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso: listar todas las actividades de un desarrollador para un día específico.
 *
 * <p>Retorna actividades de cualquier estado ({@code PENDIENTE}, {@code COMPLETADA},
 * {@code BLOQUEADA}, etc.) para el {@code userId} y la {@code fecha} dados.
 * Si no hay actividades, retorna lista vacía.
 */
@Service
public final class ListarActividadesByFecha {

    private static final Logger log = LoggerFactory.getLogger(ListarActividadesByFecha.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public ListarActividadesByFecha(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    /**
     * Retorna todas las actividades del usuario para la fecha indicada.
     *
     * @param userId identificador del desarrollador
     * @param fecha  fecha del día a consultar
     * @return lista de actividades (puede estar vacía si no hay registros)
     */
    public List<ActividadResponse> execute(String userId, LocalDate fecha) {
        log.info("Listando actividades | userId='{}' fecha='{}'", userId, fecha);
        List<Actividad> actividades = actividadPersistencePortOut.findByUserIdAndFecha(userId, fecha);
        log.info("Actividades encontradas: {} | userId='{}' fecha='{}'", actividades.size(), userId, fecha);
        return actividades.stream()
                .map(ActividadResponse::from)
                .toList();
    }
}
