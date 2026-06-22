package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.service.JiraService;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraIssue;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JiraTools {

    private final JiraService jiraService;

    public JiraTools(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @McpTool(
            name = "searchJiraIssues",
            description = "Busca issues en Jira usando JQL (Jira Query Language)"
    )
    public List<JiraIssue> searchJiraIssues(
            @McpToolParam(
                    description = "Consulta JQL para filtrar issues (ej: assignee = currentUser() AND status != Done)",
                    required = true
            )
            String jql
    ) {
        return jiraService.searchIssues(jql);
    }

    @McpTool(
            name = "getJiraIssue",
            description = "Obtiene el detalle completo de un issue de Jira por su key (ej: SPR-123)"
    )
    public JiraIssue getJiraIssue(
            @McpToolParam(
                    description = "Key del issue Jira (ej: SPR-123)",
                    required = true
            )
            String issueKey
    ) {
        return jiraService.getIssue(issueKey);
    }

    @McpTool(
            name = "getMyJiraIssues",
            description = "Obtiene los issues asignados al usuario autenticado en Jira"
    )
    public List<JiraIssue> getMyJiraIssues() {
        return jiraService.searchIssues(
                "assignee = currentUser() AND statusCategory != Done"
        );
    }
}
