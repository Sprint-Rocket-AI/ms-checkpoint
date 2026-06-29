package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssue;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraSearchResponse;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.domain.mapper.JiraMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class JiraRestClientOut {

    private final RestClient restClient;

    public JiraRestClientOut(@Qualifier("jiraRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<JiraIssue> searchIssues(String jql) {

        try {
            JiraSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/jql")
                            .queryParam("jql", jql)
                            .queryParam("fields", "summary,status,assignee,priority")
                            .queryParam("maxResults", "10")
                            .build())
                    .retrieve()
                    .body(JiraSearchResponse.class);

            if (response == null || response.issues() == null) {
                return List.of();
            }

            return JiraMapper.toDomainList(response.issues());

        } catch (Exception ex) {
            throw new RuntimeException("Error searching Jira issues with JQL: " + jql, ex);
        }
    }

    public JiraIssue getIssue(String issueKey) {

        JiraIssueFields issue = restClient.get()
                .uri("/issue/" + issueKey)
                .retrieve()
                .body(JiraIssueFields.class);

        if (issue == null) {
            throw new RuntimeException("Issue not found: " + issueKey);
        }

        return JiraMapper.toDomain(issue);
    }

}

