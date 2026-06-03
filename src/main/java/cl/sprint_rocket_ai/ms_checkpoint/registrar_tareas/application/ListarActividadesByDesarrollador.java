package cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.infrastructure.in.dtos.ActividadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ListarActividadesByDesarrollador {

    private static final Logger log = LoggerFactory.getLogger(ListarActividadesByDesarrollador.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;

    public ListarActividadesByDesarrollador(ActividadPersistencePortOut actividadPersistencePortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
    }

    public List<ActividadResponse> execute(String userId, EstadoActividad estado) {
        log.info("Listando actividades del desarrollador: {} con filtro estado: {}", userId, estado);

        if (estado != null) {
            return actividadPersistencePortOut.findByUserIdAndEstado(userId, estado)
                    .stream()
                    .map(ActividadResponse::from)
                    .toList();
        }

        return actividadPersistencePortOut.findByUserId(userId)
                .stream()
                .map(ActividadResponse::from)
                .toList();
    }
}
