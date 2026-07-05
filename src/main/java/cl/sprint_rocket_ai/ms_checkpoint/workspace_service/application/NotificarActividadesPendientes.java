package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.IAEngineRestClient;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.NotificationJMS;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class NotificarActividadesPendientes {

    private static final Logger log = LoggerFactory.getLogger(NotificarActividadesPendientes.class);
    private static final int TOP_ACTIVIDADES = 3;

    private final ActividadMongoRepository actividadMongoRepository;
    private final IAEngineRestClient iAEngineRestClient;
    private final NotificationJMS notificationJMS;

    public NotificarActividadesPendientes(ActividadMongoRepository actividadMongoRepository,
                                          IAEngineRestClient iAEngineRestClient,
                                          NotificationJMS notificationJMS) {
        this.actividadMongoRepository = actividadMongoRepository;
        this.iAEngineRestClient = iAEngineRestClient;
        this.notificationJMS = notificationJMS;
    }

    public void execute() {
        log.info("Iniciando notificación de actividades pendientes más importantes");

        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(1);
        LocalDateTime fechaFin = LocalDateTime.now();
        List<Actividad> pendientes = actividadMongoRepository
                .findByEstadoAndFechaCreacionBetween(EstadoActividad.PENDIENTE, fechaInicio, fechaFin);

        if (pendientes.isEmpty()) {
            log.info("No se encontraron actividades pendientes");
            return;
        }

        Map<String, List<Actividad>> porUsuario = pendientes.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        porUsuario.forEach((userId, actividades) -> {
            try {
                List<Actividad> actividadesPorUsuario = actividadMongoRepository
                        .findByUserIdAndEstado(userId, EstadoActividad.PENDIENTE);
                List<Actividad> topActividades = actividadesPorUsuario.stream().limit(TOP_ACTIVIDADES).toList();
                if (!topActividades.isEmpty()) {
                    String popUp = iAEngineRestClient.generatePopUp(topActividades);
                    notificationJMS.notifyDeveloper(userId, popUp);
                    log.info("Pop-up enviado exitosamente al desarrollador: {} con {} actividades", userId, topActividades.size());
                }
            } catch (Exception e) {
                log.error("Error al generar/enviar pop-up para el desarrollador: {} - {}", userId, e.getMessage());
            }
        });

        log.info("Proceso de notificación de actividades pendientes finalizado");
    }
}
