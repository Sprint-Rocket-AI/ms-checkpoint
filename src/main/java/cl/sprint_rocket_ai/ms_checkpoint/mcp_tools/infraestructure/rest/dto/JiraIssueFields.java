package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto;

public record JiraIssueFields(
        String key,
        Fields fields
) {
    public record Fields(
            String summary,
            Description description,
            Status status,
            Assignee assignee
    ) {}

    public record Description(String content) {}
    public record Status(String name) {}
    public record Assignee(String displayName) {}
}