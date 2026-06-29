package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.JiraRestClientOut;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssue;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JiraTools {

    private final JiraRestClientOut jiraRestClientOut;

    public JiraTools(JiraRestClientOut jiraRestClientOut) {
        this.jiraRestClientOut = jiraRestClientOut;
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
        return jiraRestClientOut.searchIssues(jql);
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
        return jiraRestClientOut.getIssue(issueKey);
    }

    @McpTool(
            name = "getMyJiraIssues",
            description = "Obtiene los issues asignados al usuario autenticado en Jira"
    )
    public List<JiraIssue> getMyJiraIssues() {
        return jiraRestClientOut.searchIssues(
                "assignee = currentUser() AND statusCategory != Done"
        );
    }
}
