package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.registrar_tareas.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.SugerenciaActividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.IAEnginePortOut;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ActividadParaPopUp;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ActividadResumenDto;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.GenerarPopUpRequest;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.PopUpResponse;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ResumenDiarioRequestDto;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.dtos.ResumenDiarioResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public final class IAEngineAdapterOut implements IAEnginePortOut {

    private static final Logger log = LoggerFactory.getLogger(IAEngineAdapterOut.class);
    private static final String SUMMARY_PATH = "/api/ia/generar-resumen";
    private static final String POPUP_PATH = "/api/ia/generar-popup";
    private static final String DAILY_SUMMARY_PATH = "/api/checkpoint/resumen-diario";

    private final RestClient restClient;

    public IAEngineAdapterOut(@Qualifier("iaEngineRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String generateSummary(List<Actividad> actividades) {
        log.info("Solicitando resumen a IA-ENGINE con {} actividades", actividades.size());
        try {
            Map<String, Object> request = Map.of(
                    "actividades", actividades.stream().map(ActividadParaPopUp::from).toList(),
                    "tipo", "RESUMEN_DIARIO"
            );

            String response = restClient.post()
                    .uri(SUMMARY_PATH)
                    .body(request)
                    .retrieve()
                    .body(String.class);
            log.info("Resumen generado exitosamente por IA-ENGINE");
            return response;
        } catch (RestClientResponseException e) {
            log.error("Error HTTP {} al comunicarse con IA-ENGINE para generar resumen: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return generarResumenFallback(actividades);
        } catch (Exception e) {
            log.error("Error al comunicarse con IA-ENGINE para generar resumen: {}", e.getMessage());
            return generarResumenFallback(actividades);
        }
    }

    @Override
    public String generatePopUp(List<Actividad> actividades) {
        log.info("Solicitando pop-up a IA-ENGINE con {} actividades", actividades.size());
        try {
            List<ActividadParaPopUp> actividadesPopUp = actividades.stream()
                    .map(ActividadParaPopUp::from)
                    .toList();

            GenerarPopUpRequest request = new GenerarPopUpRequest(
                    actividades.getFirst().getUserId(),
                    actividadesPopUp
            );

            PopUpResponse response = restClient.post()
                    .uri(POPUP_PATH)
                    .body(request)
                    .retrieve()
                    .body(PopUpResponse.class);
            log.info("Pop-up generado exitosamente por IA-ENGINE");
            return response != null ? response.contenido() : generarPopUpFallback(actividades);
        } catch (RestClientResponseException e) {
            log.error("Error HTTP {} al comunicarse con IA-ENGINE para generar pop-up: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return generarPopUpFallback(actividades);
        } catch (Exception e) {
            log.error("Error al comunicarse con IA-ENGINE para generar pop-up: {}", e.getMessage());
            return generarPopUpFallback(actividades);
        }
    }

    @Override
    public ResumenDiarioResult generateDailySummary(List<Actividad> actividades, String userId, LocalDate fecha) {
        log.info("Solicitando resumen diario a IA-ENGINE | userId='{}' fecha='{}' actividades={}",
                userId, fecha, actividades.size());
        try {
            List<ActividadResumenDto> actividadesDto = actividades.stream()
                    .map(ActividadResumenDto::from)
                    .toList();

            ResumenDiarioRequestDto requestDto = new ResumenDiarioRequestDto(userId, fecha, actividadesDto);

            ResumenDiarioResponseDto response = restClient.post()
                    .uri(DAILY_SUMMARY_PATH)
                    .body(requestDto)
                    .retrieve()
                    .body(ResumenDiarioResponseDto.class);

            if (response == null) {
                log.warn("IA-ENGINE retornó respuesta nula para resumen diario | userId='{}'", userId);
                return generarResumenDiarioFallback(actividades);
            }

            List<SugerenciaActividad> sugerencias = response.sugerencias() != null
                    ? response.sugerencias().stream()
                              .map(dto -> dto.toDomain())
                              .toList()
                    : Collections.emptyList();

            log.info("Resumen diario generado exitosamente por IA-ENGINE | userId='{}' sugerencias={}",
                    userId, sugerencias.size());
            return new ResumenDiarioResult(response.resumen(), sugerencias);

        } catch (RestClientResponseException e) {
            log.error("Error HTTP {} al comunicarse con IA-ENGINE para resumen diario | userId='{}': {}",
                    e.getStatusCode(), userId, e.getResponseBodyAsString());
            return generarResumenDiarioFallback(actividades);
        } catch (Exception e) {
            log.error("Error al comunicarse con IA-ENGINE para resumen diario | userId='{}': {}",
                    userId, e.getMessage());
            return generarResumenDiarioFallback(actividades);
        }
    }

    // ─── Fallbacks ──────────────────────────────────────────────────────────────

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

    private ResumenDiarioResult generarResumenDiarioFallback(List<Actividad> actividades) {
        log.warn("Generando resumen diario básico sin IA-ENGINE (fallback)");
        StringBuilder sb = new StringBuilder();
        sb.append("Resumen básico (sin IA):\n\n");
        actividades.forEach(a -> sb.append("- [").append(
                a.getEstado() != null ? a.getEstado().name() : "SIN_ESTADO")
                .append("] ").append(a.getTitulo()).append("\n"));
        return new ResumenDiarioResult(sb.toString(), Collections.emptyList());
    }
}
