package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.in;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ResumenUsuarioDto;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.UsuarioActividadesDto;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.step_job.ResumenDiarioProcessor;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.step_job.ResumenDiarioReader;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.step_job.ResumenDiarioWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler que dispara la generación del resumen diario ejecutivo.
 *
 * <p>Se ejecuta cada 5 minutos (configurable via properties).
 * Utiliza un patrón de procesamiento por pasos (Reader-Processor-Writer).
 */
@Component
public final class GenerarResumenDiarioScheduler {

    private static final Logger log = LoggerFactory.getLogger(GenerarResumenDiarioScheduler.class);

    private final ResumenDiarioReader reader;
    private final ResumenDiarioProcessor processor;
    private final ResumenDiarioWriter writer;

    public GenerarResumenDiarioScheduler(ResumenDiarioReader reader,
                                         ResumenDiarioProcessor processor,
                                         ResumenDiarioWriter writer) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    @Scheduled(cron = "${schedulers.resumen-diario.cron:0 */5 * * * *}")
    public void execute() {
        log.info("=== Ejecutando job: Generar resumen diario ejecutivo [Cada 5 min] ===");
        try {
            LocalDate ayer = LocalDate.now().minusDays(1);
            
            // 1. Reader
            List<UsuarioActividadesDto> datos = reader.read(ayer);
            
            if (datos.isEmpty()) {
                log.info("=== Job de resumen diario completado: Sin datos para procesar ===");
                return;
            }

            // 2. Processor
            List<ResumenUsuarioDto> procesados = processor.process(datos, ayer);

            // 3. Writer
            writer.write(procesados);

            log.info("=== Job de resumen diario completado exitosamente ===");
        } catch (Exception e) {
            log.error("=== Error en job de resumen diario: {} ===", e.getMessage(), e);
        }
    }
}
