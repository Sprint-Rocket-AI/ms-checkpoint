package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.DiagramNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.DiagramResponse;
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
@DisplayName("ObtenerDiagramById")
class ObtenerDiagramByIdTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private ObtenerDiagramById obtenerDiagramById;

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
        @DisplayName("Debe retornar DiagramResponse con los datos del diagrama encontrado")
        void shouldReturnDiagramResponseWithCorrectData() {
            // Given
            Diagram diagram = buildDiagram("diag-1", "Mi Diagrama");
            when(diagramRepository.findById("diag-1")).thenReturn(Optional.of(diagram));

            // When
            DiagramResponse response = obtenerDiagramById.execute("diag-1");

            // Then
            assertNotNull(response);
            assertEquals("diag-1", response.id());
            assertEquals("Mi Diagrama", response.name());
        }

        @Test
        @DisplayName("Debe llamar a findById con el id correcto y no más interacciones")
        void shouldCallFindByIdWithCorrectIdAndNoMoreInteractions() {
            // Given
            when(diagramRepository.findById("diag-2")).thenReturn(Optional.of(buildDiagram("diag-2", "Otro")));

            // When
            obtenerDiagramById.execute("diag-2");

            // Then
            verify(diagramRepository, times(1)).findById("diag-2");
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
                    () -> obtenerDiagramById.execute("no-existe"));

            assertEquals("Diagram", ex.getEntidad());
            assertEquals("no-existe", ex.getIdentificador());
        }
    }
}
