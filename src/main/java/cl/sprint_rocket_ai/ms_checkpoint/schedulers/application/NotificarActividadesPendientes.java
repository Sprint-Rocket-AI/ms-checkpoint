package cl.sprint_rocket_ai.ms_checkpoint.schedulers.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_actividades.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.NotificationPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class NotificarActividadesPendientes {

    private static final Logger log = LoggerFactory.getLogger(NotificarActividadesPendientes.class);
    private static final int TOP_ACTIVIDADES = 3;

    private final ActividadPersistencePortOut actividadPersistencePortOut;
    private final IAEnginePortOut iaEnginePortOut;
    private final NotificationPortOut notificationPortOut;

    public NotificarActividadesPendientes(ActividadPersistencePortOut actividadPersistencePortOut,
                                          IAEnginePortOut iaEnginePortOut,
                                          NotificationPortOut notificationPortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
        this.iaEnginePortOut = iaEnginePortOut;
        this.notificationPortOut = notificationPortOut;
    }

    public void execute() {
        log.info("Iniciando notificación de actividades pendientes más importantes");

        // Obtener todas las actividades pendientes, agrupadas por usuario
        List<Actividad> pendientes = actividadPersistencePortOut
                .findByEstadoAndFechaCreacionBetween(EstadoActividad.PENDIENTE, 
                        java.time.LocalDate.now().minusMonths(6), java.time.LocalDate.now());

        if (pendientes.isEmpty()) {
            log.info("No se encontraron actividades pendientes");
            return;
        }

        Map<String, List<Actividad>> porUsuario = pendientes.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        porUsuario.forEach((userId, actividades) -> {
            try {
                List<Actividad> top = actividadPersistencePortOut
                        .findByUserIdOrderByPrioridadAsc(userId, EstadoActividad.PENDIENTE, TOP_ACTIVIDADES);

                if (!top.isEmpty()) {
                    String popUp = iaEnginePortOut.generatePopUp(top);
                    notificationPortOut.notifyDeveloper(userId, popUp);
                    log.info("Pop-up enviado exitosamente al desarrollador: {} con {} actividades", userId, top.size());
                }
            } catch (Exception e) {
                log.error("Error al generar/enviar pop-up para el desarrollador: {} - {}", userId, e.getMessage());
            }
        });

        log.info("Proceso de notificación de actividades pendientes finalizado");
    }
}
