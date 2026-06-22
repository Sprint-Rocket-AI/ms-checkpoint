package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto;

public record JiraIssue(
        String key,
        String summary,
        String description,
        String status,
        String assignee
) {}
