package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools.CrearListaFormateadaParaBD;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ToolsController")
class ToolsControllerTest {

    private ToolsController controller;

    @BeforeEach
    void setUp() {
        controller = new ToolsController();
    }

    @Nested
    @DisplayName("POST /format-in — formatear lista para cláusula IN")
    class FormatearIn {

        @Test
        @DisplayName("Debe retornar 200 OK con el statement generado por CrearListaFormateadaParaBD")
        void shouldReturn200WithGeneratedStatement() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095,0-134104,0-134119",
                    true
            );

            // When
            ResponseEntity<FormatToListResponse> response = controller.formatearIn(request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().statement());
        }

        @Test
        @DisplayName("Debe incluir el nombre de columna en el statement retornado")
        void shouldIncludeColumnNameInStatement() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "u-1,u-2,u-3",
                    true
            );

            // When
            ResponseEntity<FormatToListResponse> response = controller.formatearIn(request);

            // Then
            assertTrue(response.getBody().statement().contains("USER_ID"));
        }

        @Test
        @DisplayName("Debe formatear valores numéricos sin comillas cuando el tipo es INT")
        void shouldFormatNumericValuesWithoutQuotes() {
            // Given — los valores se separan por salto de línea (lines())
            FormatToListRequest request = new FormatToListRequest(
                    "CANTIDAD",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "1\n2\n3",
                    true
            );

            // When
            ResponseEntity<FormatToListResponse> response = controller.formatearIn(request);

            // Then
            String statement = response.getBody().statement();
            assertTrue(statement.contains("1") && statement.contains("2") && statement.contains("3"));
            assertFalse(statement.contains("'1'"), "Los valores numéricos no deben ir entre comillas");
        }

        @Test
        @DisplayName("Debe formatear valores de texto con comillas cuando el tipo es STRING")
        void shouldFormatStringValuesWithQuotes() {
            // Given — los valores se separan por salto de línea (lines())
            FormatToListRequest request = new FormatToListRequest(
                    "NOMBRE",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "alpha\nbeta",
                    true
            );

            // When
            ResponseEntity<FormatToListResponse> response = controller.formatearIn(request);

            // Then
            String statement = response.getBody().statement();
            assertTrue(statement.contains("'alpha'") && statement.contains("'beta'"),
                    "Los valores STRING deben ir entre comillas simples");
        }

        @Test
        @DisplayName("Debe delegar a CrearListaFormateadaParaBD y retornar su resultado sin modificarlo")
        void shouldDelegateAndReturnResultUnmodified() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "COL",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "v1",
                    true
            );
            FormatToListResponse expectedFromUtility = CrearListaFormateadaParaBD.construir(request);

            // When
            ResponseEntity<FormatToListResponse> response = controller.formatearIn(request);

            // Then
            assertEquals(expectedFromUtility.statement(), response.getBody().statement());
        }
    }
}
