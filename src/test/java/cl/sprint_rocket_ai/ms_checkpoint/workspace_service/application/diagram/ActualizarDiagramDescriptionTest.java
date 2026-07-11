package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramDescriptionRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActualizarDiagramDescription")
class ActualizarDiagramDescriptionTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private ActualizarDiagramDescription actualizarDiagramDescription;

    private Diagram buildDiagram(String id, String description) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName("Diagrama " + id);
        d.setDescription(description);
        d.setUserId("user-1");
        return d;
    }

    @Nested
    @DisplayName("Cuando el diagrama existe")
    class CuandoDiagramaExiste {

        @Test
        @DisplayName("Debe actualizar la descripción y retornar DiagramResponse")
        void shouldUpdateDescriptionAndReturnDiagramResponse() {
            // Given
            Diagram existing = buildDiagram("diag-1", "Descripción vieja");
            ActualizarDiagramDescriptionRequest request = new ActualizarDiagramDescriptionRequest("Descripción nueva");
            when(diagramRepository.findById("diag-1")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagramDescription.execute("diag-1", request);

            // Then
            assertNotNull(response);
            assertEquals("Descripción nueva", response.description());
        }

        @Test
        @DisplayName("Debe asignar fechaActualizacion antes de guardar")
        void shouldSetFechaActualizacionBeforeSaving() {
            // Given
            Diagram existing = buildDiagram("diag-2", "Desc");
            ActualizarDiagramDescriptionRequest request = new ActualizarDiagramDescriptionRequest("Nueva Desc");
            when(diagramRepository.findById("diag-2")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Diagram> captor = ArgumentCaptor.forClass(Diagram.class);
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            actualizarDiagramDescription.execute("diag-2", request);

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
            Diagram existing = buildDiagram("diag-3", "Desc");
            ActualizarDiagramDescriptionRequest request = new ActualizarDiagramDescriptionRequest("Actualizada");
            when(diagramRepository.findById("diag-3")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarDiagramDescription.execute("diag-3", request);

            // Then
            var inOrder = inOrder(diagramRepository);
            inOrder.verify(diagramRepository).findById("diag-3");
            inOrder.verify(diagramRepository).save(any(Diagram.class));
            verifyNoMoreInteractions(diagramRepository);
        }

        @Test
        @DisplayName("Debe actualizar la descripción a null cuando el request tiene description nula")
        void shouldSetDescriptionToNullWhenRequestDescriptionIsNull() {
            // Given
            Diagram existing = buildDiagram("diag-4", "Desc original");
            ActualizarDiagramDescriptionRequest request = new ActualizarDiagramDescriptionRequest(null);
            when(diagramRepository.findById("diag-4")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagramDescription.execute("diag-4", request);

            // Then
            assertNull(response.description());
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
                    () -> actualizarDiagramDescription.execute("no-existe", new ActualizarDiagramDescriptionRequest("Desc")));

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
                    () -> actualizarDiagramDescription.execute("ghost", new ActualizarDiagramDescriptionRequest("X")));

            verify(diagramRepository, never()).save(any());
        }
    }
}
