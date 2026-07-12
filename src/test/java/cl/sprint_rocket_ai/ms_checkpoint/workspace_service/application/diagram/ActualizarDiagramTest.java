package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.ActualizarDiagramRequest;
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
@DisplayName("ActualizarDiagram")
class ActualizarDiagramTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private ActualizarDiagram actualizarDiagram;

    private Diagram buildDiagram(String id, String name) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName(name);
        d.setUserId("user-1");
        return d;
    }

    @Nested
    @DisplayName("Cuando el diagrama existe")
    class CuandoDiagramaExiste {

        @Test
        @DisplayName("Debe actualizar el nombre y retornar DiagramResponse")
        void shouldUpdateNameAndReturnDiagramResponse() {
            // Given
            Diagram existing = buildDiagram("diag-1", "Viejo Nombre");
            ActualizarDiagramRequest request = new ActualizarDiagramRequest("Nuevo Nombre");
            when(diagramRepository.findById("diag-1")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagram.execute("diag-1", request);

            // Then
            assertNotNull(response);
            assertEquals("Nuevo Nombre", response.name());
        }

        @Test
        @DisplayName("Debe asignar fechaActualizacion al diagrama antes de guardar")
        void shouldSetFechaActualizacionBeforeSaving() {
            // Given
            Diagram existing = buildDiagram("diag-2", "Nombre");
            ActualizarDiagramRequest request = new ActualizarDiagramRequest("Actualizado");
            when(diagramRepository.findById("diag-2")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Diagram> captor = ArgumentCaptor.forClass(Diagram.class);
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            actualizarDiagram.execute("diag-2", request);

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
            Diagram existing = buildDiagram("diag-3", "Nombre");
            ActualizarDiagramRequest request = new ActualizarDiagramRequest("Nuevo");
            when(diagramRepository.findById("diag-3")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarDiagram.execute("diag-3", request);

            // Then
            var inOrder = inOrder(diagramRepository);
            inOrder.verify(diagramRepository).findById("diag-3");
            inOrder.verify(diagramRepository).save(any(Diagram.class));
            verifyNoMoreInteractions(diagramRepository);
        }

        @Test
        @DisplayName("Debe conservar el id del diagrama en la respuesta")
        void shouldRetainDiagramIdInResponse() {
            // Given
            Diagram existing = buildDiagram("diag-4", "Nombre");
            ActualizarDiagramRequest request = new ActualizarDiagramRequest("NuevoNombre");
            when(diagramRepository.findById("diag-4")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagram.execute("diag-4", request);

            // Then
            assertEquals("diag-4", response.id());
        }

        @Test
        @DisplayName("No debe modificar el nombre cuando el request tiene nombre nulo")
        void shouldNotModifyNameWhenRequestNameIsNull() {
            // Given
            Diagram existing = buildDiagram("diag-5", "Original");
            ActualizarDiagramRequest request = new ActualizarDiagramRequest(null);
            when(diagramRepository.findById("diag-5")).thenReturn(Optional.of(existing));
            when(diagramRepository.save(any(Diagram.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            DiagramResponse response = actualizarDiagram.execute("diag-5", request);

            // Then
            assertEquals("Original", response.name());
        }
    }

    @Nested
    @DisplayName("Cuando el diagrama no existe")
    class CuandoDiagramaNoExiste {

        @Test
        @DisplayName("Debe lanzar DiagramNotFoundException con el id correcto")
        void shouldThrowDiagramNotFoundExceptionWithCorrectId() {
            // Given
            when(diagramRepository.findById("no-existe")).thenReturn(Optional.empty());

            // When / Then
            DiagramNotFoundException ex = assertThrows(DiagramNotFoundException.class,
                    () -> actualizarDiagram.execute("no-existe", new ActualizarDiagramRequest("Nombre")));

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
                    () -> actualizarDiagram.execute("ghost", new ActualizarDiagramRequest("X")));

            verify(diagramRepository, never()).save(any());
        }
    }
}
