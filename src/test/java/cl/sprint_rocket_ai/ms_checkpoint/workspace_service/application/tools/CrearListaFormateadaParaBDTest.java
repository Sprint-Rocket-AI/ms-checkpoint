package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearListaFormateadaParaBD")
class CrearListaFormateadaParaBDTest {

    // ─── Clase pura sin dependencias inyectadas — no requiere @Mock/@InjectMocks ───

    // =========================================================================
    // Tipo STRING
    // =========================================================================

    @Nested
    @DisplayName("Dado tipo STRING")
    class TipoString {

        @Test
        @DisplayName("Debe generar cláusula IN con comillas simples cuando se proporcionan valores STRING")
        void shouldReturnInClauseWithSingleQuotesWhenStringValuesAreProvided() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095\n0-134104\n0-134119\n0-134157"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("POLICY_ID IN ('0-134095','0-134104','0-134119','0-134157')", response.statement());
        }

        @Test
        @DisplayName("Debe generar cláusula IN con un único valor STRING")
        void shouldReturnSingleValueInClauseWhenOnlyOneStringValueIsProvided() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("POLICY_ID IN ('0-134095')", response.statement());
        }

        @Test
        @DisplayName("Debe escapar comillas simples dentro de los valores STRING duplicándolas")
        void shouldEscapeSingleQuotesWhenStringValueContainsSingleQuote() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "NOMBRE",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "O'Brien\nO'Connor"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("NOMBRE IN ('O''Brien','O''Connor')", response.statement());
        }

        @Test
        @DisplayName("Debe eliminar valores STRING duplicados manteniendo el orden de inserción")
        void shouldRemoveDuplicatesWhenStringValuesContainRepeatedEntries() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095\n0-134104\n0-134095\n0-134104\n0-134119"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("POLICY_ID IN ('0-134095','0-134104','0-134119')", response.statement());
        }

        @Test
        @DisplayName("Debe ignorar líneas vacías y líneas con solo espacios en blanco")
        void shouldFilterBlankLinesWhenStringValuesContainEmptyLines() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095\n\n   \n0-134104\n\t"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("POLICY_ID IN ('0-134095','0-134104')", response.statement());
        }

        @Test
        @DisplayName("Debe recortar espacios en blanco al inicio y al final de cada valor STRING")
        void shouldTrimWhitespaceWhenStringValuesHaveLeadingOrTrailingSpaces() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "  0-134095  \n  0-134104  "
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("POLICY_ID IN ('0-134095','0-134104')", response.statement());
        }

        @Test
        @DisplayName("Debe deduplicar valores que son iguales después del trim")
        void shouldDeduplicateAfterTrimWhenValuesMatchOnlyAfterStripping() {
            // Given — "abc" y "  abc  " son el mismo valor tras trim
            FormatToListRequest request = new FormatToListRequest(
                    "COL",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "abc\n  abc  "
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("COL IN ('abc')", response.statement());
        }

        @Test
        @DisplayName("Debe usar el nombre de columna exactamente como se proporciona, incluyendo alias de tabla")
        void shouldUseExactColumnNameWhenColumnIncludesTableAlias() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "T.POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "VAL1"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertTrue(response.statement().startsWith("T.POLICY_ID IN ("));
        }

        @Test
        @DisplayName("Debe generar el formato exacto del ejemplo de negocio documentado")
        void shouldMatchExactBusinessExampleFormatWhenProvidingCanonicalPolicyIds() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "POLICY_ID",
                    CrearListaFormateadaParaBD.TipoDato.STRING,
                    "0-134095\n0-134104\n0-134119\n0-134157"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals(
                    "POLICY_ID IN ('0-134095','0-134104','0-134119','0-134157')",
                    response.statement()
            );
        }
    }

    // =========================================================================
    // Tipo INT
    // =========================================================================

    @Nested
    @DisplayName("Dado tipo INT")
    class TipoInt {

        @Test
        @DisplayName("Debe generar cláusula IN sin comillas cuando se proporcionan valores INT válidos")
        void shouldReturnInClauseWithoutQuotesWhenIntValuesAreProvided() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "1\n2\n3"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("USER_ID IN (1,2,3)", response.statement());
        }

        @Test
        @DisplayName("Debe generar cláusula IN con un único valor INT")
        void shouldReturnSingleValueInClauseWhenOnlyOneIntValueIsProvided() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "42"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("USER_ID IN (42)", response.statement());
        }

        @Test
        @DisplayName("Debe manejar valores dentro del rango Long")
        void shouldHandleLongRangeNumbersWhenIntTypeIsUsed() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "BIG_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "9999999999\n1234567890"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("BIG_ID IN (9999999999,1234567890)", response.statement());
        }

        @Test
        @DisplayName("Debe eliminar valores INT duplicados manteniendo el orden de inserción")
        void shouldRemoveDuplicatesWhenIntValuesContainRepeatedEntries() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "1\n2\n1\n3\n2"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("USER_ID IN (1,2,3)", response.statement());
        }

        @Test
        @DisplayName("Debe ignorar líneas vacías en valores de tipo INT")
        void shouldFilterBlankLinesWhenIntValuesContainEmptyLines() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "1\n\n   \n2"
            );

            // When
            FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

            // Then
            assertNotNull(response);
            assertEquals("USER_ID IN (1,2)", response.statement());
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando un valor alfanumérico se usa en tipo INT")
        void shouldThrowIllegalArgumentExceptionWhenIntValueIsNotNumeric() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "1\nabc\n3"
            );

            // When / Then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> CrearListaFormateadaParaBD.construir(request)
            );

            assertTrue(ex.getMessage().contains("abc"),
                    "El mensaje debe incluir el valor inválido");
            assertTrue(ex.getMessage().contains("INT"),
                    "El mensaje debe mencionar el tipo INT");
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el valor INT es decimal")
        void shouldThrowIllegalArgumentExceptionWhenIntValueIsDecimal() {
            // Given — decimales no son válidos para Long.valueOf
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "3.14"
            );

            // When / Then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> CrearListaFormateadaParaBD.construir(request)
            );

            assertTrue(ex.getMessage().contains("3.14"),
                    "El mensaje debe incluir el valor decimal inválido");
        }

        @Test
        @DisplayName("Debe encapsular NumberFormatException como causa de IllegalArgumentException")
        void shouldWrapNumberFormatExceptionWhenIntValueIsInvalid() {
            // Given
            FormatToListRequest request = new FormatToListRequest(
                    "USER_ID",
                    CrearListaFormateadaParaBD.TipoDato.INT,
                    "noesunumero"
            );

            // When / Then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> CrearListaFormateadaParaBD.construir(request)
            );

            assertNotNull(ex.getCause(), "La excepción debe envolver una causa");
            assertInstanceOf(NumberFormatException.class, ex.getCause(),
                    "La causa debe ser NumberFormatException");
        }
    }
}
