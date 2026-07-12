package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.domain.mapper;

import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssue;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields.Assignee;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields.Description;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields.Fields;
import cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.infraestructure.out.dto.JiraIssueFields.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JiraMapper")
class JiraMapperTest {

    // ─── Mapper estático puro — no requiere @Mock/@InjectMocks ───────────────

    // =========================================================================
    // toDomain()
    // =========================================================================

    @Nested
    @DisplayName("toDomain — mapeo de un único issue")
    class ToDomain {

        @Test
        @DisplayName("Debe mapear todos los campos correctamente cuando el issue tiene todos los campos completos")
        void shouldMapAllFieldsWhenIssueIsComplete() {
            // Given
            JiraIssueFields issueFields = buildIssueFields(
                    "PROJ-42",
                    "Implementar autenticación OAuth2",
                    "Configurar el flujo OAuth2 con Google",
                    "En Progreso",
                    "Sebastián Carreño"
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertNotNull(result);
            assertEquals("PROJ-42", result.key());
            assertEquals("Implementar autenticación OAuth2", result.summary());
            assertEquals("Configurar el flujo OAuth2 con Google", result.description());
            assertEquals("En Progreso", result.status());
            assertEquals("Sebastián Carreño", result.assignee());
        }

        @Test
        @DisplayName("Debe mapear description como null cuando el campo description del issue es null")
        void shouldMapDescriptionAsNullWhenDescriptionIsNull() {
            // Given
            JiraIssueFields issueFields = buildIssueFields(
                    "PROJ-10",
                    "Revisar logs de producción",
                    null,          // description null
                    "Pendiente",
                    "María López"
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertNotNull(result);
            assertNull(result.description(),
                    "La descripción debe ser null cuando el issue no tiene description");
            assertEquals("PROJ-10", result.key());
            assertEquals("Revisar logs de producción", result.summary());
            assertEquals("Pendiente", result.status());
            assertEquals("María López", result.assignee());
        }

        @Test
        @DisplayName("Debe mapear assignee como null cuando el campo assignee del issue es null")
        void shouldMapAssigneeAsNullWhenAssigneeIsNull() {
            // Given
            JiraIssueFields issueFields = buildIssueFields(
                    "PROJ-99",
                    "Tarea sin asignar",
                    "Descripción de la tarea",
                    "Abierto",
                    null           // assignee null
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertNotNull(result);
            assertNull(result.assignee(),
                    "El assignee debe ser null cuando el issue no tiene assignee");
            assertEquals("PROJ-99", result.key());
            assertEquals("Tarea sin asignar", result.summary());
            assertEquals("Descripción de la tarea", result.description());
            assertEquals("Abierto", result.status());
        }

        @Test
        @DisplayName("Debe mapear description y assignee como null cuando ambos campos son null")
        void shouldMapDescriptionAndAssigneeAsNullWhenBothAreNull() {
            // Given
            JiraIssueFields issueFields = buildIssueFields(
                    "PROJ-1",
                    "Issue mínima",
                    null,   // description null
                    "Backlog",
                    null    // assignee null
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertNotNull(result);
            assertEquals("PROJ-1", result.key());
            assertEquals("Issue mínima", result.summary());
            assertNull(result.description());
            assertEquals("Backlog", result.status());
            assertNull(result.assignee());
        }

        @Test
        @DisplayName("Debe mapear correctamente el key y status con valores reales de Jira")
        void shouldMapKeyAndStatusWhenUsingRealJiraValues() {
            // Given — status con nombre exacto como lo devuelve la API de Jira
            JiraIssueFields issueFields = buildIssueFields(
                    "SPRINT-2024",
                    "Fix bug crítico en producción",
                    "El servicio de pagos retorna 500",
                    "In Progress",
                    "Dev Team"
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertEquals("SPRINT-2024", result.key());
            assertEquals("In Progress", result.status());
        }

        @Test
        @DisplayName("Debe mapear description vacía como string vacío cuando el content es vacío")
        void shouldMapEmptyDescriptionWhenDescriptionContentIsEmpty() {
            // Given — description presente pero con content vacío
            JiraIssueFields issueFields = new JiraIssueFields(
                    "PROJ-5",
                    new Fields(
                            "Tarea con descripción vacía",
                            new Description(""),   // content existe pero es string vacío
                            new Status("Cerrado"),
                            new Assignee("Analista")
                    )
            );

            // When
            JiraIssue result = JiraMapper.toDomain(issueFields);

            // Then
            assertNotNull(result);
            assertEquals("", result.description(),
                    "Debe preservar el string vacío sin convertirlo a null");
        }
    }

    // =========================================================================
    // toDomainList()
    // =========================================================================

    @Nested
    @DisplayName("toDomainList — mapeo de lista de issues")
    class ToDomainList {

        @Test
        @DisplayName("Debe mapear correctamente una lista con múltiples issues completas")
        void shouldMapAllIssuesWhenListContainsMultipleCompleteIssues() {
            // Given
            List<JiraIssueFields> issuesList = List.of(
                    buildIssueFields("PROJ-1", "Primera tarea", "Desc 1", "Abierto", "Dev A"),
                    buildIssueFields("PROJ-2", "Segunda tarea", "Desc 2", "En Progreso", "Dev B"),
                    buildIssueFields("PROJ-3", "Tercera tarea", "Desc 3", "Cerrado", "Dev C")
            );

            // When
            List<JiraIssue> result = JiraMapper.toDomainList(issuesList);

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());

            assertEquals("PROJ-1", result.get(0).key());
            assertEquals("Primera tarea", result.get(0).summary());
            assertEquals("Dev A", result.get(0).assignee());

            assertEquals("PROJ-2", result.get(1).key());
            assertEquals("Segunda tarea", result.get(1).summary());
            assertEquals("Dev B", result.get(1).assignee());

            assertEquals("PROJ-3", result.get(2).key());
            assertEquals("Tercera tarea", result.get(2).summary());
            assertEquals("Dev C", result.get(2).assignee());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando la lista de entrada es vacía")
        void shouldReturnEmptyListWhenInputListIsEmpty() {
            // Given
            List<JiraIssueFields> issuesList = List.of();

            // When
            List<JiraIssue> result = JiraMapper.toDomainList(issuesList);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty(), "El resultado debe ser una lista vacía");
        }

        @Test
        @DisplayName("Debe mapear lista con un único issue correctamente")
        void shouldMapSingleElementListWhenListContainsOneIssue() {
            // Given
            List<JiraIssueFields> issuesList = List.of(
                    buildIssueFields("PROJ-7", "Único issue", "Solo descripción", "Done", "Solo Dev")
            );

            // When
            List<JiraIssue> result = JiraMapper.toDomainList(issuesList);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("PROJ-7", result.get(0).key());
            assertEquals("Único issue", result.get(0).summary());
        }

        @Test
        @DisplayName("Debe mapear correctamente issues con campos nulos dentro de una lista mixta")
        void shouldHandleNullFieldsWhenListContainsMixedIssues() {
            // Given — mezcla de issues con y sin description/assignee
            List<JiraIssueFields> issuesList = List.of(
                    buildIssueFields("PROJ-10", "Con todo",  "Con desc", "Abierto", "Dev A"),
                    buildIssueFields("PROJ-11", "Sin desc",   null,        "Abierto", "Dev B"),
                    buildIssueFields("PROJ-12", "Sin assign", "Con desc", "Abierto", null)
            );

            // When
            List<JiraIssue> result = JiraMapper.toDomainList(issuesList);

            // Then
            assertEquals(3, result.size());

            assertNotNull(result.get(0).description());
            assertNotNull(result.get(0).assignee());

            assertNull(result.get(1).description(),
                    "El segundo issue debe tener description null");
            assertNotNull(result.get(1).assignee());

            assertNotNull(result.get(2).description());
            assertNull(result.get(2).assignee(),
                    "El tercer issue debe tener assignee null");
        }

        @Test
        @DisplayName("Debe preservar el orden de los issues tal como vienen en la lista de entrada")
        void shouldPreserveOrderWhenMappingList() {
            // Given
            List<JiraIssueFields> issuesList = List.of(
                    buildIssueFields("PROJ-100", "Primero",  "D1", "Abierto",    "A"),
                    buildIssueFields("PROJ-200", "Segundo",  "D2", "En Progreso","B"),
                    buildIssueFields("PROJ-300", "Tercero",  "D3", "Cerrado",    "C"),
                    buildIssueFields("PROJ-400", "Cuarto",   "D4", "Backlog",    "D")
            );

            // When
            List<JiraIssue> result = JiraMapper.toDomainList(issuesList);

            // Then
            assertEquals("PROJ-100", result.get(0).key());
            assertEquals("PROJ-200", result.get(1).key());
            assertEquals("PROJ-300", result.get(2).key());
            assertEquals("PROJ-400", result.get(3).key());
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    /**
     * Construye un JiraIssueFields con los campos dados.
     * Pasa null en description o assignee para simular ausencia del campo.
     */
    private JiraIssueFields buildIssueFields(
            String key,
            String summary,
            String descriptionContent,
            String statusName,
            String assigneeDisplayName
    ) {
        Description description = descriptionContent != null
                ? new Description(descriptionContent)
                : null;

        Assignee assignee = assigneeDisplayName != null
                ? new Assignee(assigneeDisplayName)
                : null;

        return new JiraIssueFields(
                key,
                new Fields(summary, description, new Status(statusName), assignee)
        );
    }
}
