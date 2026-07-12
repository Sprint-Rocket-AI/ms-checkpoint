package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Excepciones de dominio — EntityNotFoundException y subclases")
class EntityNotFoundExceptionTest {

    // ─── Clases de dominio puras — no requieren @Mock/@InjectMocks ───────────

    // =========================================================================
    // EntityNotFoundException — clase base
    // =========================================================================

    @Nested
    @DisplayName("EntityNotFoundException")
    class EntityNotFoundExceptionTests {

        @Test
        @DisplayName("Debe construir el mensaje con el formato esperado cuando se proporcionan entidad e identificador")
        void shouldBuildExpectedMessageWhenEntityAndIdentifierAreProvided() {
            // Given
            String entidad = "Usuario";
            String identificador = "usr-001";

            // When
            EntityNotFoundException ex = new EntityNotFoundException(entidad, identificador);

            // Then
            assertEquals(
                    "No se encontró Usuario con identificador: usr-001",
                    ex.getMessage()
            );
        }

        @Test
        @DisplayName("Debe retornar la entidad exacta pasada al constructor")
        void shouldReturnExactEntityWhenGetEntidadIsCalled() {
            // Given
            String entidad = "Producto";
            String identificador = "prod-999";

            // When
            EntityNotFoundException ex = new EntityNotFoundException(entidad, identificador);

            // Then
            assertEquals("Producto", ex.getEntidad());
        }

        @Test
        @DisplayName("Debe retornar el identificador exacto pasado al constructor")
        void shouldReturnExactIdentifierWhenGetIdentificadorIsCalled() {
            // Given
            String entidad = "Orden";
            String identificador = "ORD-2024-001";

            // When
            EntityNotFoundException ex = new EntityNotFoundException(entidad, identificador);

            // Then
            assertEquals("ORD-2024-001", ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe extender RuntimeException para permitir uso sin bloque catch obligatorio")
        void shouldExtendRuntimeExceptionWhenInstantiated() {
            // Given / When
            EntityNotFoundException ex = new EntityNotFoundException("Entidad", "id-1");

            // Then
            assertInstanceOf(RuntimeException.class, ex,
                    "EntityNotFoundException debe ser una RuntimeException (unchecked)");
        }

        @Test
        @DisplayName("Debe incluir el identificador en el mensaje cuando el id tiene formato UUID")
        void shouldIncludeUuidIdentifierInMessageWhenIdIsUuidFormat() {
            // Given
            String uuid = "550e8400-e29b-41d4-a716-446655440000";

            // When
            EntityNotFoundException ex = new EntityNotFoundException("Contrato", uuid);

            // Then
            assertTrue(ex.getMessage().contains(uuid),
                    "El mensaje debe contener el UUID completo");
            assertEquals(uuid, ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe manejar identificador numérico como string sin alterar el valor")
        void shouldPreserveNumericStringIdentifierWithoutModification() {
            // Given
            String identificadorNumerico = "12345";

            // When
            EntityNotFoundException ex = new EntityNotFoundException("Factura", identificadorNumerico);

            // Then
            assertEquals("12345", ex.getIdentificador());
            assertTrue(ex.getMessage().contains("12345"));
        }

        @Test
        @DisplayName("Debe poder ser lanzada y capturada como RuntimeException")
        void shouldBeThrowableAndCatchableAsRuntimeException() {
            // Given
            String entidad = "Recurso";
            String id = "res-42";

            // When / Then
            RuntimeException capturada = assertThrows(
                    RuntimeException.class,
                    () -> { throw new EntityNotFoundException(entidad, id); }
            );

            assertEquals("No se encontró Recurso con identificador: res-42", capturada.getMessage());
        }
    }

    // =========================================================================
    // ActividadNotFoundException
    // =========================================================================

    @Nested
    @DisplayName("ActividadNotFoundException")
    class ActividadNotFoundExceptionTests {

        @Test
        @DisplayName("Debe construir el mensaje con 'Actividad' como entidad cuando se pasa solo el identificador")
        void shouldBuildMessageWithActividadEntityWhenOnlyIdIsProvided() {
            // Given
            String id = "act-001";

            // When
            ActividadNotFoundException ex = new ActividadNotFoundException(id);

            // Then
            assertEquals("No se encontró Actividad con identificador: act-001", ex.getMessage());
        }

        @Test
        @DisplayName("Debe reportar 'Actividad' como entidad en getEntidad()")
        void shouldReturnActividadWhenGetEntidadIsCalled() {
            // Given / When
            ActividadNotFoundException ex = new ActividadNotFoundException("act-123");

            // Then
            assertEquals("Actividad", ex.getEntidad());
        }

        @Test
        @DisplayName("Debe retornar el identificador exacto pasado al constructor")
        void shouldReturnExactIdentifierWhenGetIdentificadorIsCalled() {
            // Given
            String id = "act-xyz-456";

            // When
            ActividadNotFoundException ex = new ActividadNotFoundException(id);

            // Then
            assertEquals("act-xyz-456", ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe ser instancia de EntityNotFoundException para ser capturada por el manejador global")
        void shouldBeInstanceOfEntityNotFoundExceptionForGlobalHandlerCompatibility() {
            // Given / When
            ActividadNotFoundException ex = new ActividadNotFoundException("act-001");

            // Then
            assertInstanceOf(EntityNotFoundException.class, ex,
                    "Debe ser EntityNotFoundException para ser manejada por GlobalExceptionHandler");
            assertInstanceOf(RuntimeException.class, ex);
        }

        @Test
        @DisplayName("Debe poder ser lanzada y capturada correctamente")
        void shouldBeThrowableAndCatchableWhenThrown() {
            // Given
            String id = "act-no-existe";

            // When / Then
            ActividadNotFoundException capturada = assertThrows(
                    ActividadNotFoundException.class,
                    () -> { throw new ActividadNotFoundException(id); }
            );

            assertTrue(capturada.getMessage().contains(id));
        }
    }

    // =========================================================================
    // RecordatorioNotFoundException
    // =========================================================================

    @Nested
    @DisplayName("RecordatorioNotFoundException")
    class RecordatorioNotFoundExceptionTests {

        @Test
        @DisplayName("Debe construir el mensaje con 'Recordatorio' como entidad cuando se pasa solo el identificador")
        void shouldBuildMessageWithRecordatorioEntityWhenOnlyIdIsProvided() {
            // Given
            String id = "rec-007";

            // When
            RecordatorioNotFoundException ex = new RecordatorioNotFoundException(id);

            // Then
            assertEquals("No se encontró Recordatorio con identificador: rec-007", ex.getMessage());
        }

        @Test
        @DisplayName("Debe reportar 'Recordatorio' como entidad en getEntidad()")
        void shouldReturnRecordatorioWhenGetEntidadIsCalled() {
            // Given / When
            RecordatorioNotFoundException ex = new RecordatorioNotFoundException("rec-001");

            // Then
            assertEquals("Recordatorio", ex.getEntidad());
        }

        @Test
        @DisplayName("Debe retornar el identificador exacto pasado al constructor")
        void shouldReturnExactIdentifierWhenGetIdentificadorIsCalled() {
            // Given
            String id = "rec-abc-789";

            // When
            RecordatorioNotFoundException ex = new RecordatorioNotFoundException(id);

            // Then
            assertEquals("rec-abc-789", ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe ser instancia de EntityNotFoundException para ser capturada por el manejador global")
        void shouldBeInstanceOfEntityNotFoundExceptionForGlobalHandlerCompatibility() {
            // Given / When
            RecordatorioNotFoundException ex = new RecordatorioNotFoundException("rec-001");

            // Then
            assertInstanceOf(EntityNotFoundException.class, ex);
            assertInstanceOf(RuntimeException.class, ex);
        }
    }

    // =========================================================================
    // DiagramNotFoundException
    // =========================================================================

    @Nested
    @DisplayName("DiagramNotFoundException")
    class DiagramNotFoundExceptionTests {

        @Test
        @DisplayName("Debe construir el mensaje con 'Diagram' como entidad cuando se pasa solo el identificador")
        void shouldBuildMessageWithDiagramEntityWhenOnlyIdIsProvided() {
            // Given
            String id = "diag-100";

            // When
            DiagramNotFoundException ex = new DiagramNotFoundException(id);

            // Then
            assertEquals("No se encontró Diagram con identificador: diag-100", ex.getMessage());
        }

        @Test
        @DisplayName("Debe reportar 'Diagram' como entidad en getEntidad()")
        void shouldReturnDiagramWhenGetEntidadIsCalled() {
            // Given / When
            DiagramNotFoundException ex = new DiagramNotFoundException("diag-001");

            // Then
            assertEquals("Diagram", ex.getEntidad());
        }

        @Test
        @DisplayName("Debe retornar el identificador exacto pasado al constructor")
        void shouldReturnExactIdentifierWhenGetIdentificadorIsCalled() {
            // Given
            String id = "diag-xyz-321";

            // When
            DiagramNotFoundException ex = new DiagramNotFoundException(id);

            // Then
            assertEquals("diag-xyz-321", ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe ser instancia de EntityNotFoundException para ser capturada por el manejador global")
        void shouldBeInstanceOfEntityNotFoundExceptionForGlobalHandlerCompatibility() {
            // Given / When
            DiagramNotFoundException ex = new DiagramNotFoundException("diag-001");

            // Then
            assertInstanceOf(EntityNotFoundException.class, ex);
            assertInstanceOf(RuntimeException.class, ex);
        }
    }

    // =========================================================================
    // Polimorfismo — captura como clase base
    // =========================================================================

    @Nested
    @DisplayName("Polimorfismo — captura como EntityNotFoundException")
    class Polimorfismo {

        @Test
        @DisplayName("Debe capturar ActividadNotFoundException como EntityNotFoundException")
        void shouldCatchActividadNotFoundExceptionAsEntityNotFoundException() {
            // Given / When / Then
            EntityNotFoundException capturada = assertThrows(
                    EntityNotFoundException.class,
                    () -> { throw new ActividadNotFoundException("act-999"); }
            );

            assertEquals("Actividad", capturada.getEntidad());
            assertEquals("act-999", capturada.getIdentificador());
        }

        @Test
        @DisplayName("Debe capturar RecordatorioNotFoundException como EntityNotFoundException")
        void shouldCatchRecordatorioNotFoundExceptionAsEntityNotFoundException() {
            // Given / When / Then
            EntityNotFoundException capturada = assertThrows(
                    EntityNotFoundException.class,
                    () -> { throw new RecordatorioNotFoundException("rec-999"); }
            );

            assertEquals("Recordatorio", capturada.getEntidad());
            assertEquals("rec-999", capturada.getIdentificador());
        }

        @Test
        @DisplayName("Debe capturar DiagramNotFoundException como EntityNotFoundException")
        void shouldCatchDiagramNotFoundExceptionAsEntityNotFoundException() {
            // Given / When / Then
            EntityNotFoundException capturada = assertThrows(
                    EntityNotFoundException.class,
                    () -> { throw new DiagramNotFoundException("diag-999"); }
            );

            assertEquals("Diagram", capturada.getEntidad());
            assertEquals("diag-999", capturada.getIdentificador());
        }
    }
}
