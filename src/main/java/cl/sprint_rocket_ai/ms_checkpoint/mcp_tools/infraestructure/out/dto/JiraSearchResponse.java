package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto;

import java.util.List;

public record JiraSearchResponse(
        List<JiraIssueFields> issues
) {}