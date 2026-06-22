package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.mapper;



import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraIssue;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto.JiraIssueFields;

import java.util.List;

public class JiraMapper {

    public static JiraIssue toDomain(JiraIssueFields issue) {

        String description = null;

        if (issue.fields().description() != null) {
            description = issue.fields().description().content();
        }

        String assignee = issue.fields().assignee() != null
                ? issue.fields().assignee().displayName()
                : null;

        return new JiraIssue(
                issue.key(),
                issue.fields().summary(),
                description,
                issue.fields().status().name(),
                assignee
        );
    }

    public static List<JiraIssue> toDomainList(List<JiraIssueFields> issues) {
        return issues.stream()
                .map(JiraMapper::toDomain)
                .toList();
    }
}