package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.SugerenciaActividad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MailResponseBuilder")
class MailResponseBuilderTest {

    private MailResponseBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new MailResponseBuilder();
    }

    @Nested
    @DisplayName("buildDailySummary")
    class BuildDailySummary {

        @Test
        @DisplayName("Debe incluir el userId en el saludo del cuerpo HTML")
        void shouldIncludeUserIdInGreeting() {
            // When
            String result = builder.buildDailySummary("sebastian", "Resumen del día", List.of());

            // Then
            assertTrue(result.contains("sebastian"));
        }

        @Test
        @DisplayName("Debe incluir el texto del resumen en el cuerpo HTML")
        void shouldIncludeResumenText() {
            // When
            String result = builder.buildDailySummary("user-1", "Gran avance hoy", List.of());

            // Then
            assertTrue(result.contains("Gran avance hoy"));
        }

        @Test
        @DisplayName("Debe incluir mensaje de sin sugerencias cuando la lista está vacía")
        void shouldIncludeNoSuggestionsMessageWhenEmpty() {
            // When
            String result = builder.buildDailySummary("user-1", "Resumen", List.of());

            // Then
            assertTrue(result.contains("No se generaron sugerencias automáticas"));
        }

        @Test
        @DisplayName("Debe incluir mensaje de sin sugerencias cuando la lista es nula")
        void shouldIncludeNoSuggestionsMessageWhenNull() {
            // When
            String result = builder.buildDailySummary("user-1", "Resumen", null);

            // Then
            assertTrue(result.contains("No se generaron sugerencias automáticas"));
        }

        @Test
        @DisplayName("Debe incluir el título de cada sugerencia en el HTML")
        void shouldIncludeEachSugerenciaTitulo() {
            // Given
            List<SugerenciaActividad> sugerencias = List.of(
                    new SugerenciaActividad("Revisar PR", "Descripción", "ALTA", "Bloquea al equipo"),
                    new SugerenciaActividad("Actualizar docs", "Descripción", "BAJA", "Mejora calidad")
            );

            // When
            String result = builder.buildDailySummary("user-1", "Resumen", sugerencias);

            // Then
            assertTrue(result.contains("Revisar PR"));
            assertTrue(result.contains("Actualizar docs"));
        }

        @Test
        @DisplayName("Debe aplicar color rojo para prioridad ALTA")
        void shouldApplyRedColorForAltaPriority() {
            // Given
            List<SugerenciaActividad> sugerencias = List.of(
                    new SugerenciaActividad("Tarea crítica", "Desc", "ALTA", "Urgente"));

            // When
            String result = builder.buildDailySummary("user-1", "Resumen", sugerencias);

            // Then
            assertTrue(result.contains("#dc3545")); // rojo
            assertTrue(result.contains("ALTA"));
        }

        @Test
        @DisplayName("Debe aplicar color amarillo para prioridad MEDIA")
        void shouldApplyYellowColorForMediaPriority() {
            // Given
            List<SugerenciaActividad> sugerencias = List.of(
                    new SugerenciaActividad("Tarea media", "Desc", "MEDIA", "Importante"));

            // When
            String result = builder.buildDailySummary("user-1", "Resumen", sugerencias);

            // Then
            assertTrue(result.contains("#ffc107")); // amarillo
        }

        @Test
        @DisplayName("Debe retornar HTML no vacío con estructura básica")
        void shouldReturnNonEmptyHtmlWithBasicStructure() {
            // When
            String result = builder.buildDailySummary("user-1", "Resumen", List.of());

            // Then
            assertNotNull(result);
            assertTrue(result.contains("<div"));
            assertTrue(result.contains("SpringRocket IA"));
        }
    }
}
