package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.GenerarResumenActividadesFinalizadas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public final class GenerarResumenActividadesFinalizadasScheduler {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenActividadesFinalizadasScheduler.class);

    private final GenerarResumenActividadesFinalizadas generarResumenActividadesFinalizadas;

    public GenerarResumenActividadesFinalizadasScheduler(GenerarResumenActividadesFinalizadas generarResumenActividadesFinalizadas) {
        this.generarResumenActividadesFinalizadas = generarResumenActividadesFinalizadas;
    }

    @Scheduled(cron = "${schedulers.resumen-actividades.cron:0 30 8 * * MON-FRI}")
    public void execute() {
        log.info("=== Ejecutando job: Generar resumen de actividades finalizadas ===");
        try {
            generarResumenActividadesFinalizadas.execute();
            log.info("=== Job completado exitosamente ===");
        } catch (Exception e) {
            log.error("=== Error en job de resumen de actividades: {} ===", e.getMessage(), e);
        }
    }
}
