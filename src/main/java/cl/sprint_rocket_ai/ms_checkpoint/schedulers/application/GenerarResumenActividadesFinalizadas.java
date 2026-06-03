package cl.sprint_rocket_ai.ms_checkpoint.schedulers.application;

import cl.sprint_rocket_ai.ms_checkpoint.commons.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.ports.out.ActividadPersistencePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.NotificationPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public final class GenerarResumenActividadesFinalizadas {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenActividadesFinalizadas.class);

    private final ActividadPersistencePortOut actividadPersistencePortOut;
    private final IAEnginePortOut iaEnginePortOut;
    private final NotificationPortOut notificationPortOut;

    public GenerarResumenActividadesFinalizadas(ActividadPersistencePortOut actividadPersistencePortOut,
                                                IAEnginePortOut iaEnginePortOut,
                                                NotificationPortOut notificationPortOut) {
        this.actividadPersistencePortOut = actividadPersistencePortOut;
        this.iaEnginePortOut = iaEnginePortOut;
        this.notificationPortOut = notificationPortOut;
    }

    public void execute() {
        log.info("Iniciando generación de resumen de actividades finalizadas del día anterior");

        LocalDate ayer = LocalDate.now().minusDays(1);
        List<Actividad> completadas = actividadPersistencePortOut
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
                String resumen = iaEnginePortOut.generateSummary(actividades);
                notificationPortOut.notifyDeveloper(userId, resumen);
                log.info("Resumen enviado exitosamente al desarrollador: {}", userId);
            } catch (Exception e) {
                log.error("Error al generar/enviar resumen para el desarrollador: {} - {}", userId, e.getMessage());
            }
        });

        log.info("Proceso de generación de resúmenes finalizado");
    }
}
