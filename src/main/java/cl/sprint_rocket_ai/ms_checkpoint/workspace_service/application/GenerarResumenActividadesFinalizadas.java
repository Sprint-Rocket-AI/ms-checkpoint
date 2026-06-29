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
public final class GenerarResumenActividadesFinalizadas {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenActividadesFinalizadas.class);

    private final ActividadMongoRepository actividadRepository;
    private final IAEngineRestClient iAEngineRestClient;
    private final NotificationJMS notificacionJMS;

    public GenerarResumenActividadesFinalizadas(ActividadMongoRepository actividadRepository,
                                                IAEngineRestClient iAEngineRestClient,
                                                NotificationJMS notificacionJMS) {
        this.actividadRepository = actividadRepository;
        this.iAEngineRestClient = iAEngineRestClient;
        this.notificacionJMS = notificacionJMS;
    }

    public void execute() {
        log.info("Iniciando generación de resumen de actividades finalizadas del día anterior");

        LocalDateTime ayer = LocalDateTime.now().minusDays(1);
        List<Actividad> completadas = actividadRepository
                .findByEstadoAndFechaCreacionBetween(EstadoActividad.COMPLETADA, ayer, ayer);

        if (completadas.isEmpty()) {
            log.info("No se encontraron actividades completadas para la fecha: {}", ayer);
            return;
        }

        log.info("Se encontraron {} actividades completadas para la fecha: {}", completadas.size(), ayer);

        // Agrupar por usuario para enviar resumen individual
        Map<String, List<Actividad>> porUsuario = completadas.stream()
                .collect(Collectors.groupingBy(Actividad::getUserId));

        porUsuario.forEach((userId, actividades) -> {
            try {
                String resumen = iAEngineRestClient.generateSummary(actividades);
                notificacionJMS.notifyDeveloper(userId, resumen);
                log.info("Resumen enviado exitosamente al desarrollador: {}", userId);
            } catch (Exception e) {
                log.error("Error al generar/enviar resumen para el desarrollador: {} - {}", userId, e.getMessage());
            }
        });

        log.info("Proceso de generación de resúmenes finalizado");
    }
}
