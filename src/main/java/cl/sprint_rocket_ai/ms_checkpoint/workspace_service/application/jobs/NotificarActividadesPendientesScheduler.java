package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.NotificarActividadesPendientes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public final class NotificarActividadesPendientesScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificarActividadesPendientesScheduler.class);

    private final NotificarActividadesPendientes notificarActividadesPendientes;

    public NotificarActividadesPendientesScheduler(NotificarActividadesPendientes notificarActividadesPendientes) {
        this.notificarActividadesPendientes = notificarActividadesPendientes;
    }

    @Scheduled(cron = "${schedulers.notificar-pendientes.cron:0 0 * * * *}")
    public void execute() {
        log.info("=== Ejecutando job: Notificar actividades pendientes ===");
        try {
            notificarActividadesPendientes.execute();
            log.info("=== Job completado exitosamente ===");
        } catch (Exception e) {
            log.error("=== Error en job de notificación de pendientes: {} ===", e.getMessage(), e);
        }
    }
}
