package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramGraphRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.DiagramMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActualizarDiagramGraph")
class ActualizarDiagramGraphTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private ActualizarDiagramGraph actualizarDiagramGraph;

    private Diagram buildDiagram(String id) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName("Diagrama " + id);
        d.setUserId("user-1");
        return d;
    }

    @Nested
    @DisplayName("Cuando el diagrama existe")
    class CuandoDiagramaExiste {

        @Test
        @DisplayName("Debe actualizar el grafo y retornar DiagramResponse")
        void shouldUpdateGraphAndReturnDiagramResponse() {
            // Given
            Diagram existing = buildDiagram("diag-1");
            ActualizarDiagramGraphRequest request = new ActualizarDiagramGraphRequest(List.of(), List.of());
            when(diagramRepository.findById("diag-1")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagramGraph.execute("diag-1", request);

            // Then
            assertNotNull(response);
            assertEquals("diag-1", response.id());
        }

        @Test
        @DisplayName("Debe asignar fechaActualizacion al diagrama antes de guardar")
        void shouldSetFechaActualizacionBeforeSaving() {
            // Given
            Diagram existing = buildDiagram("diag-2");
            ActualizarDiagramGraphRequest request = new ActualizarDiagramGraphRequest(null, null);
            when(diagramRepository.findById("diag-2")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Diagram> captor = ArgumentCaptor.forClass(Diagram.class);
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            actualizarDiagramGraph.execute("diag-2", request);

            // Then
            verify(diagramRepository).save(captor.capture());
            LocalDateTime fecha = captor.getValue().getFechaActualizacion();
            assertNotNull(fecha);
            assertFalse(fecha.isBefore(antes));
            assertFalse(fecha.isAfter(LocalDateTime.now().plusSeconds(1)));
        }

        @Test
        @DisplayName("Debe llamar findById y luego save en orden correcto")
        void shouldCallFindByIdThenSaveInOrder() {
            // Given
            Diagram existing = buildDiagram("diag-3");
            ActualizarDiagramGraphRequest request = new ActualizarDiagramGraphRequest(List.of(), List.of());
            when(diagramRepository.findById("diag-3")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarDiagramGraph.execute("diag-3", request);

            // Then
            var inOrder = inOrder(diagramRepository);
            inOrder.verify(diagramRepository).findById("diag-3");
            inOrder.verify(diagramRepository).save(any(Diagram.class));
            verifyNoMoreInteractions(diagramRepository);
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
                    () -> actualizarDiagramGraph.execute("no-existe", new ActualizarDiagramGraphRequest(null, null)));

            assertEquals("Diagram", ex.getEntidad());
            assertEquals("no-existe", ex.getIdentificador());
        }

        @Test
        @DisplayName("No debe llamar a save cuando el diagrama no existe")
        void shouldNotCallSaveWhenDiagramNotFound() {
            // Given
            when(diagramRepository.findById("ghost")).thenReturn(Optional.empty());

            // When / Then
            assertThrows(DiagramNotFoundException.class,
                    () -> actualizarDiagramGraph.execute("ghost", new ActualizarDiagramGraphRequest(null, null)));

            verify(diagramRepository, never()).save(any());
        }
    }
}
