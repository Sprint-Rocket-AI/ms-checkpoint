package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram.*;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiagramController")
class DiagramControllerTest {

    @Mock private CrearDiagram crearDiagram;
    @Mock private ObtenerDiagramById obtenerDiagramById;
    @Mock private ActualizarDiagram actualizarDiagram;
    @Mock private EliminarDiagram eliminarDiagram;
    @Mock private ActualizarDiagramGraph actualizarDiagramGraph;
    @Mock private ActualizarDiagramDescription actualizarDiagramDescription;
    @Mock private ObtenerDiagramasByUsuario obtenerDiagramasByUsuario;

    private DiagramController controller;

    @BeforeEach
    void setUp() {
        controller = new DiagramController(
                crearDiagram, obtenerDiagramById, actualizarDiagram, eliminarDiagram,
                actualizarDiagramGraph, actualizarDiagramDescription, obtenerDiagramasByUsuario
        );
    }

    private DiagramResponse stubResponse(String id) {
        return new DiagramResponse(id, "Diagrama", "Desc", List.of(), List.of(), null, null, null, null);
    }

    @Nested
    @DisplayName("POST / — crear diagrama")
    class Create {

        @Test
        @DisplayName("Debe retornar 201 CREATED con el body del use case")
        void shouldReturn201WithBody() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("D", "Desc",
                    cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoDiagrama.FLUJO, "user-1");
            DiagramResponse expected = stubResponse("diag-1");
            when(crearDiagram.execute(request)).thenReturn(expected);

            // When
            ResponseEntity<DiagramResponse> response = controller.create(request);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expected, response.getBody());
            verify(crearDiagram).execute(request);
        }
    }

    @Nested
    @DisplayName("GET /{id} — obtener por id")
    class FindById {

        @Test
        @DisplayName("Debe retornar 200 OK con el body del use case")
        void shouldReturn200WithBody() {
            // Given
            DiagramResponse expected = stubResponse("diag-2");
            when(obtenerDiagramById.execute("diag-2")).thenReturn(expected);

            // When
            ResponseEntity<DiagramResponse> response = controller.findById("diag-2");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/name — actualizar nombre")
    class UpdateName {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarDiagramRequest request = new ActualizarDiagramRequest("Nuevo Nombre");
            DiagramResponse expected = stubResponse("diag-3");
            when(actualizarDiagram.execute("diag-3", request)).thenReturn(expected);

            // When
            ResponseEntity<DiagramResponse> response = controller.updateName("diag-3", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("DELETE /{id} — eliminar diagrama")
    class Delete {

        @Test
        @DisplayName("Debe retornar 204 NO CONTENT y llamar al use case con el id correcto")
        void shouldReturn204AndCallUseCase() {
            // When
            ResponseEntity<Void> response = controller.delete("diag-4");

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(eliminarDiagram).execute("diag-4");
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/graph — actualizar grafo")
    class UpdateGraph {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarDiagramGraphRequest request = new ActualizarDiagramGraphRequest(List.of(), List.of());
            DiagramResponse expected = stubResponse("diag-5");
            when(actualizarDiagramGraph.execute("diag-5", request)).thenReturn(expected);

            // When
            ResponseEntity<DiagramResponse> response = controller.updateGraph("diag-5", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/description — actualizar descripción")
    class UpdateDescription {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarDiagramDescriptionRequest request = new ActualizarDiagramDescriptionRequest("Nueva desc");
            DiagramResponse expected = stubResponse("diag-6");
            when(actualizarDiagramDescription.execute("diag-6", request)).thenReturn(expected);

            // When
            ResponseEntity<DiagramResponse> response = controller.updateDescription("diag-6", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("GET /desarrollador/{userId} — listar por desarrollador")
    class FindByDesarrollador {

        @Test
        @DisplayName("Debe retornar 200 OK con la lista del use case")
        void shouldReturn200WithList() {
            // Given
            List<DiagramResponse> expected = List.of(stubResponse("d1"), stubResponse("d2"));
            when(obtenerDiagramasByUsuario.execute("user-1")).thenReturn(expected);

            // When
            ResponseEntity<List<DiagramResponse>> response = controller.findByDesarrollador("user-1");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
            verify(obtenerDiagramasByUsuario).execute("user-1");
        }
    }
}
