package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.JiraRestClientOut;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JiraTools")
class JiraToolsTest {

    @Mock
    private JiraRestClientOut jiraRestClientOut;

    @InjectMocks
    private JiraTools jiraTools;

    private JiraIssue stubIssue(String key) {
        return new JiraIssue(key, "Resumen " + key, null, "In Progress", null);
    }

    @Test
    @DisplayName("searchJiraIssues debe delegar al client con el JQL recibido y retornar los issues")
    void shouldDelegateSearchWithJqlAndReturnIssues() {
        // Given
        String jql = "assignee = currentUser()";
        List<JiraIssue> expected = List.of(stubIssue("SPR-1"), stubIssue("SPR-2"));
        when(jiraRestClientOut.searchIssues(jql)).thenReturn(expected);

        // When
        List<JiraIssue> result = jiraTools.searchJiraIssues(jql);

        // Then
        assertEquals(2, result.size());
        verify(jiraRestClientOut).searchIssues(jql);
    }

    @Test
    @DisplayName("getJiraIssue debe delegar al client con el issueKey recibido y retornar el issue")
    void shouldDelegateGetIssueWithKeyAndReturnIssue() {
        // Given
        JiraIssue expected = stubIssue("SPR-42");
        when(jiraRestClientOut.getIssue("SPR-42")).thenReturn(expected);

        // When
        JiraIssue result = jiraTools.getJiraIssue("SPR-42");

        // Then
        assertNotNull(result);
        assertEquals("SPR-42", result.key());
        verify(jiraRestClientOut).getIssue("SPR-42");
    }

    @Test
    @DisplayName("getMyJiraIssues debe buscar con JQL de usuario actual y estado no Done")
    void shouldSearchWithCurrentUserAndNotDoneStatus() {
        // Given
        List<JiraIssue> expected = List.of(stubIssue("SPR-10"));
        when(jiraRestClientOut.searchIssues(anyString())).thenReturn(expected);

        // When
        List<JiraIssue> result = jiraTools.getMyJiraIssues();

        // Then
        assertEquals(1, result.size());
        verify(jiraRestClientOut).searchIssues(contains("currentUser()"));
    }
}
