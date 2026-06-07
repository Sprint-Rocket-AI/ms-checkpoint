package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.application.GenerarResumenDiario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler que dispara la generación del resumen diario ejecutivo.
 *
 * <p>Se ejecuta de lunes a viernes a las 8:30 a.m. (configurable via properties).
 * Recopila todas las actividades del día anterior (pendientes + completadas) por usuario
 * y solicita a IA-ENGINE un resumen ejecutivo + sugerencias de continuidad.
 * El resultado se envía al desarrollador correspondiente vía correo (actualmente por log).
 *
 * <p>Propiedad de configuración: {@code schedulers.resumen-diario.cron}
 * <br>Default: {@code 0 30 8 * * MON-FRI} (8:30 a.m. lunes a viernes)
 */
@Component
public final class GenerarResumenDiarioScheduler {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenDiarioScheduler.class);

    private final GenerarResumenDiario generarResumenDiario;

    public GenerarResumenDiarioScheduler(GenerarResumenDiario generarResumenDiario) {
        this.generarResumenDiario = generarResumenDiario;
    }

    @Scheduled(cron = "${schedulers.resumen-diario.cron:0 30 8 * * MON-FRI}")
    public void execute() {
        log.info("=== Ejecutando job: Generar resumen diario ejecutivo [8:30 a.m.] ===");
        try {
            generarResumenDiario.execute();
            log.info("=== Job de resumen diario completado exitosamente ===");
        } catch (Exception e) {
            log.error("=== Error en job de resumen diario: {} ===", e.getMessage(), e);
        }
    }
}
