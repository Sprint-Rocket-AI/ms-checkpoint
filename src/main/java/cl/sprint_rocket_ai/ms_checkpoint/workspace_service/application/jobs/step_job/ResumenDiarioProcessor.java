package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.jobs.step_job;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.IAEngineRestClient;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenUsuarioDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.UsuarioActividadesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResumenDiarioProcessor {

    private static final Logger log = LoggerFactory.getLogger(ResumenDiarioProcessor.class);

    private final IAEngineRestClient iaEngine;

    public ResumenDiarioProcessor(IAEngineRestClient iaEngine) {
        this.iaEngine = iaEngine;
    }

    public List<ResumenUsuarioDto> process(List<UsuarioActividadesDto> datos, LocalDate fecha) {
        log.info("Processor: Procesando datos para {} usuarios", datos.size());
        List<ResumenUsuarioDto> resultados = new ArrayList<>();

        for (UsuarioActividadesDto dato : datos) {
            try {
                ResumenDiarioResult resultadoIA = iaEngine.generateDailySummary(
                        dato.actividades(), dato.userId(), fecha);

                resultados.add(new ResumenUsuarioDto(
                        dato.userId(),
                        dato.correo(),
                        resultadoIA.resumen(),
                        resultadoIA.sugerencias()
                ));
            } catch (Exception e) {
                log.error("Processor: Error al procesar resumen diario | userId='{}': {}", dato.userId(), e.getMessage(), e);
            }
        }

        return resultados;
    }
}
