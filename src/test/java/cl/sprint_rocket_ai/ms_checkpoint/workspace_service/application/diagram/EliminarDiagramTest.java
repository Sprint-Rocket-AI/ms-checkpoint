package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EliminarDiagram")
class EliminarDiagramTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private EliminarDiagram eliminarDiagram;

    private Diagram buildDiagram(String id) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName("Diagrama " + id);
        return d;
    }

    @Nested
    @DisplayName("Cuando el diagrama existe")
    class CuandoDiagramaExiste {

        @Test
        @DisplayName("Debe eliminar el diagrama sin lanzar excepción")
        void shouldDeleteDiagramWithoutException() {
            // Given
            when(diagramRepository.findById("diag-1")).thenReturn(Optional.of(buildDiagram("diag-1")));
            doNothing().when(diagramRepository).deleteById("diag-1");

            // When / Then
            assertDoesNotThrow(() -> eliminarDiagram.execute("diag-1"));
        }

        @Test
        @DisplayName("Debe llamar findById y luego deleteById en orden correcto")
        void shouldCallFindByIdThenDeleteByIdInOrder() {
            // Given
            when(diagramRepository.findById("diag-2")).thenReturn(Optional.of(buildDiagram("diag-2")));
            doNothing().when(diagramRepository).deleteById("diag-2");

            // When
            eliminarDiagram.execute("diag-2");

            // Then
            var inOrder = inOrder(diagramRepository);
            inOrder.verify(diagramRepository).findById("diag-2");
            inOrder.verify(diagramRepository).deleteById("diag-2");
            verifyNoMoreInteractions(diagramRepository);
        }

        @Test
        @DisplayName("Debe llamar deleteById con el id correcto")
        void shouldCallDeleteByIdWithCorrectId() {
            // Given
            when(diagramRepository.findById("diag-3")).thenReturn(Optional.of(buildDiagram("diag-3")));
            doNothing().when(diagramRepository).deleteById("diag-3");

            // When
            eliminarDiagram.execute("diag-3");

            // Then
            verify(diagramRepository).deleteById("diag-3");
        }
    }

    @Nested
    @DisplayName("Cuando el diagrama no existe")
    class CuandoDiagramaNoExiste {

        @Test
        @DisplayName("Debe lanzar DiagramNotFoundException con entidad y id correctos")
        void shouldThrowDiagramNotFoundExceptionWithCorrectFields() {
            // Given
            when(diagramRepository.findById("no-existe")).thenReturn(Optional.empty());

            // When / Then
            DiagramNotFoundException ex = assertThrows(DiagramNotFoundException.class,
                    () -> eliminarDiagram.execute("no-existe"));

            assertEquals("Diagram", ex.getEntidad());
            assertEquals("no-existe", ex.getIdentificador());
        }

        @Test
        @DisplayName("No debe llamar a deleteById cuando el diagrama no existe")
        void shouldNotCallDeleteByIdWhenDiagramNotFound() {
            // Given
            when(diagramRepository.findById("ghost")).thenReturn(Optional.empty());

            // When / Then
            assertThrows(DiagramNotFoundException.class, () -> eliminarDiagram.execute("ghost"));

            verify(diagramRepository, never()).deleteById(any());
        }
    }
}
