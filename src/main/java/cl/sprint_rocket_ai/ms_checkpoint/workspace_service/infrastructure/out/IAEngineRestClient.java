package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.ResumenDiarioResult;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ActividadParaPopUp;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ActividadResumenDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.GenerarPopUpRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.PopUpResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenDiarioRequestDto;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out.dto.ResumenDiarioResponseDto;
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
public final class IAEngineRestClient {

    private static final Logger log = LoggerFactory.getLogger(IAEngineRestClient.class);
    private static final String SUMMARY_PATH = "/api/ia/generar-resumen";
    private static final String POPUP_PATH = "/api/ia/generar-popup";
    private static final String DAILY_SUMMARY_PATH = "/api/checkpoint/resumen-diario";

    private final RestClient restClient;

    public IAEngineRestClient(@Qualifier("iaEngineRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

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

    private String generarPopUpFallback(List<Actividad> actividades) {
        log.warn("Generando pop-up básico sin IA-ENGINE (fallback)");
        StringBuilder sb = new StringBuilder();
        sb.append("⚡ Actividades pendientes prioritarias:\n\n");
        actividades.forEach(a -> sb.append("🔴 ").append(a.getTitulo()));
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
