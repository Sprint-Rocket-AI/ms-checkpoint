package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.service;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraIssue;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraIssueFields;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraSearchResponse;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.mapper.JiraMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class JiraService {

    private final RestClient jiraRestClient;

    public JiraService(RestClient jiraRestClient) {
        this.jiraRestClient = jiraRestClient;
    }

    public List<JiraIssue> searchIssues(String jql) {

        try {
            JiraSearchResponse response = jiraRestClient.get()
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

        JiraIssueFields issue = jiraRestClient.get()
                .uri("/issue/" + issueKey)
                .retrieve()
                .body(JiraIssueFields.class);

        if (issue == null) {
            throw new RuntimeException("Issue not found: " + issueKey);
        }

        return JiraMapper.toDomain(issue);
    }

}

