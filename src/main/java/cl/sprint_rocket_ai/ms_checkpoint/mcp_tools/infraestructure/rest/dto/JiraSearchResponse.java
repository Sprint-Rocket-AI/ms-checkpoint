package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.rest.dto;

import java.util.List;

public record JiraSearchResponse(
        List<JiraIssueFields> issues
) {}