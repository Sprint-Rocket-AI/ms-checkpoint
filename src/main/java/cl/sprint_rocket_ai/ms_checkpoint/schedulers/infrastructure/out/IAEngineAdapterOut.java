package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ActividadParaPopUp;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.GenerarPopUpRequest;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.PopUpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public final class IAEngineAdapterOut implements IAEnginePortOut {

    private static final Logger log = LoggerFactory.getLogger(IAEngineAdapterOut.class);

    private final RestTemplate restTemplate;
    private final String iaEngineBaseUrl;

    public IAEngineAdapterOut(RestTemplate restTemplate,
                              @Value("${ia-engine.base-url}") String iaEngineBaseUrl) {
        this.restTemplate = restTemplate;
        this.iaEngineBaseUrl = iaEngineBaseUrl;
    }

    @Override
    public String generateSummary(List<Actividad> actividades) {
        log.info("Solicitando resumen a IA-ENGINE con {} actividades", actividades.size());
        try {
            String url = iaEngineBaseUrl + "/api/ia/generar-resumen";
            Map<String, Object> request = Map.of(
                    "actividades", actividades.stream().map(ActividadParaPopUp::from).toList(),
                    "tipo", "RESUMEN_DIARIO"
            );

            String response = restTemplate.postForObject(url, request, String.class);
            log.info("Resumen generado exitosamente por IA-ENGINE");
            return response;
        } catch (RestClientException e) {
            log.error("Error al comunicarse con IA-ENGINE para generar resumen: {}", e.getMessage());
            return generarResumenFallback(actividades);
        }
    }

    @Override
    public String generatePopUp(List<Actividad> actividades) {
        log.info("Solicitando pop-up a IA-ENGINE con {} actividades", actividades.size());
        try {
            String url = iaEngineBaseUrl + "/api/ia/generar-popup";
            List<ActividadParaPopUp> actividadesPopUp = actividades.stream()
                    .map(ActividadParaPopUp::from)
                    .toList();

            GenerarPopUpRequest request = new GenerarPopUpRequest(
                    actividades.getFirst().getUserId(),
                    actividadesPopUp
            );

            PopUpResponse response = restTemplate.postForObject(url, request, PopUpResponse.class);
            log.info("Pop-up generado exitosamente por IA-ENGINE");
            return response != null ? response.contenido() : generarPopUpFallback(actividades);
        } catch (RestClientException e) {
            log.error("Error al comunicarse con IA-ENGINE para generar pop-up: {}", e.getMessage());
            return generarPopUpFallback(actividades);
        }
    }

    private String generarResumenFallback(List<Actividad> actividades) {
        log.warn("Generando resumen básico sin IA-ENGINE (fallback)");
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Resumen de actividades completadas:\n\n");
        actividades.forEach(a -> sb.append("✅ ").append(a.getTitulo()).append("\n"));
        sb.append("\nTotal: ").append(actividades.size()).append(" actividades completadas.");
        return sb.toString();
    }

    private String generarPopUpFallback(List<Actividad> actividades) {
        log.warn("Generando pop-up básico sin IA-ENGINE (fallback)");
        StringBuilder sb = new StringBuilder();
        sb.append("⚡ Actividades pendientes prioritarias:\n\n");
        actividades.forEach(a -> sb.append("🔴 ").append(a.getTitulo())
                .append(" [").append(a.getPrioridad()).append("]\n"));
        return sb.toString();
    }
}
