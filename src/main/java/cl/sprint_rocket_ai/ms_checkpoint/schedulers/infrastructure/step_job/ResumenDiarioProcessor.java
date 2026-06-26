package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.step_job;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ResumenUsuarioDto;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.UsuarioActividadesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResumenDiarioProcessor {

    private static final Logger log = LoggerFactory.getLogger(ResumenDiarioProcessor.class);

    private final IAEnginePortOut iaEnginePortOut;

    public ResumenDiarioProcessor(IAEnginePortOut iaEnginePortOut) {
        this.iaEnginePortOut = iaEnginePortOut;
    }

    public List<ResumenUsuarioDto> process(List<UsuarioActividadesDto> datos, LocalDate fecha) {
        log.info("Processor: Procesando datos para {} usuarios", datos.size());
        List<ResumenUsuarioDto> resultados = new ArrayList<>();

        for (UsuarioActividadesDto dato : datos) {
            try {
                ResumenDiarioResult resultadoIA = iaEnginePortOut.generateDailySummary(
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
