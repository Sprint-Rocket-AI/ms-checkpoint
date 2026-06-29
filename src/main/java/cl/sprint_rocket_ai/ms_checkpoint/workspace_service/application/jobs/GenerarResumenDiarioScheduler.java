package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenUsuarioDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.UsuarioActividadesDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioProcessor;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioReader;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job.ResumenDiarioWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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
