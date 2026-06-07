package cl.sprint_rocket_ai.ms_checkpoint.commons.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Configuración del {@link RestClient} para comunicación con IA-ENGINE.
 * La base-url se externaliza en {@code ia-engine.base-url}.
 */
@Configuration
public class IAEngineRestClientConfig {

    @Bean("iaEngineRestClient")
    public RestClient iaEngineRestClient(@Value("${ia-engine.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
