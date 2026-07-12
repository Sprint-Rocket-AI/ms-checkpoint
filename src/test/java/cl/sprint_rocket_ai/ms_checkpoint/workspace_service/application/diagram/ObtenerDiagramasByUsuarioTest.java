package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerDiagramasByUsuario")
class ObtenerDiagramasByUsuarioTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private ObtenerDiagramasByUsuario obtenerDiagramasByUsuario;

    private Diagram buildDiagram(String id, String name, String userId) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName(name);
        d.setUserId(userId);
        return d;
    }

    @Nested
    @DisplayName("Cuando el usuario tiene diagramas")
    class CuandoUsuarioTieneDiagramas {

        @Test
        @DisplayName("Debe retornar lista de DiagramResponse con todos los diagramas del usuario")
        void shouldReturnListWithAllUserDiagrams() {
            // Given
            List<Diagram> diagramas = List.of(
                    buildDiagram("diag-1", "Diagrama A", "user-1"),
                    buildDiagram("diag-2", "Diagrama B", "user-1")
            );
            when(diagramRepository.findByUserId("user-1")).thenReturn(diagramas);

            // When
            List<DiagramResponse> responses = obtenerDiagramasByUsuario.execute("user-1");

            // Then
            assertEquals(2, responses.size());
            assertEquals("diag-1", responses.get(0).id());
            assertEquals("diag-2", responses.get(1).id());
        }

        @Test
        @DisplayName("Debe llamar a findByUserId con el userId correcto")
        void shouldCallFindByUserIdWithCorrectUserId() {
            // Given
            when(diagramRepository.findByUserId("user-2")).thenReturn(List.of(buildDiagram("d", "D", "user-2")));

            // When
            obtenerDiagramasByUsuario.execute("user-2");

            // Then
            verify(diagramRepository, times(1)).findByUserId("user-2");
            verifyNoMoreInteractions(diagramRepository);
        }
    }

    @Nested
    @DisplayName("Cuando el usuario no tiene diagramas")
    class CuandoUsuarioNoTieneDiagramas {

        @Test
        @DisplayName("Debe retornar lista vacía cuando el usuario no tiene diagramas")
        void shouldReturnEmptyListWhenUserHasNoDiagrams() {
            // Given
            when(diagramRepository.findByUserId("user-sin-diagramas")).thenReturn(List.of());

            // When
            List<DiagramResponse> responses = obtenerDiagramasByUsuario.execute("user-sin-diagramas");

            // Then
            assertNotNull(responses);
            assertTrue(responses.isEmpty());
        }

        @Test
        @DisplayName("Debe llamar a findByUserId incluso cuando no hay resultados")
        void shouldCallFindByUserIdEvenWhenNoResults() {
            // Given
            when(diagramRepository.findByUserId("ghost-user")).thenReturn(List.of());

            // When
            obtenerDiagramasByUsuario.execute("ghost-user");

            // Then
            verify(diagramRepository, times(1)).findByUserId("ghost-user");
            verifyNoMoreInteractions(diagramRepository);
        }
    }
}
