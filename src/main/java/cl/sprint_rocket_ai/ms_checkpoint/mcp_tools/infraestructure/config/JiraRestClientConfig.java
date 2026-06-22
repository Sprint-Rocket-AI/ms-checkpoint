package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class JiraRestClientConfig {

    @Bean
    public RestClient jiraRestClient(
            @Value("${jira.url}") String jiraUrl,
            @Value("${jira.email}") String email,
            @Value("${jira.api-token}") String token) {

        String auth = Base64.getEncoder()
                .encodeToString((email + ":" + token).getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl(jiraUrl + "/rest/api/3")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
