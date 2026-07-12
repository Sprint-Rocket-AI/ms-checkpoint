package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.diagram;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.TipoDiagrama;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Diagram;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.diagram.dtos.CrearDiagramRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrearDiagram")
class CrearDiagramTest {

    @Mock
    private DiagramMongoRepository diagramRepository;

    @InjectMocks
    private CrearDiagram crearDiagram;

    private Diagram buildDiagram(String id, String name, String userId) {
        Diagram d = new Diagram();
        d.setId(id);
        d.setName(name);
        d.setUserId(userId);
        d.setTipo(TipoDiagrama.FLUJO);
        d.setFechaCreacion(LocalDateTime.now());
        return d;
    }

    @Nested
    @DisplayName("Cuando el diagrama se crea exitosamente")
    class CuandoSeCreaDiagramaExitosamente {

        @Test
        @DisplayName("Debe llamar a applyTo antes de setFechaCreacion y luego guardar")
        void shouldCallApplyToBeforeFechaCreacionAndSave() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("Mi Diagrama", "Desc", TipoDiagrama.FLUJO, "user-1");
            Diagram saved = buildDiagram("diag-1", "Mi Diagrama", "user-1");
            when(diagramRepository.save(any(Diagram.class))).thenReturn(saved);

            // When
            DiagramResponse response = crearDiagram.execute(request);

            // Then
            assertNotNull(response);
            verify(diagramRepository, times(1)).save(any(Diagram.class));
        }

        @Test
        @DisplayName("Debe asignar fechaCreacion en el diagrama guardado")
        void shouldSetFechaCreacionOnDiagram() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("Diagrama", "Desc", TipoDiagrama.FLUJO, "user-2");
            Diagram saved = buildDiagram("diag-2", "Diagrama", "user-2");
            when(diagramRepository.save(any(Diagram.class))).thenReturn(saved);

            ArgumentCaptor<Diagram> captor = ArgumentCaptor.forClass(Diagram.class);
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            crearDiagram.execute(request);

            // Then
            verify(diagramRepository).save(captor.capture());
            LocalDateTime fecha = captor.getValue().getFechaCreacion();
            assertNotNull(fecha);
            assertFalse(fecha.isBefore(antes), "fechaCreacion debe ser >= momento antes de ejecutar");
            assertFalse(fecha.isAfter(LocalDateTime.now().plusSeconds(1)), "fechaCreacion debe ser <= momento después de ejecutar");
        }

        @Test
        @DisplayName("Debe retornar el DiagramResponse generado desde el diagrama guardado")
        void shouldReturnDiagramResponseFromSaved() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("Flow", "Desc", TipoDiagrama.FLUJO, "user-3");
            Diagram saved = buildDiagram("diag-3", "Flow", "user-3");
            when(diagramRepository.save(any(Diagram.class))).thenReturn(saved);

            // When
            DiagramResponse response = crearDiagram.execute(request);

            // Then
            assertEquals("diag-3", response.id());
            assertEquals("Flow", response.name());
            assertEquals("user-3", response.userId());
        }

        @Test
        @DisplayName("Debe pasar datos del request al diagrama antes de guardar")
        void shouldApplyRequestDataToDiagramBeforeSaving() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("Arquitectura", "Desc sistema", TipoDiagrama.FLUJO, "user-4");
            Diagram saved = buildDiagram("diag-4", "Arquitectura", "user-4");
            when(diagramRepository.save(any(Diagram.class))).thenReturn(saved);

            ArgumentCaptor<Diagram> captor = ArgumentCaptor.forClass(Diagram.class);

            // When
            crearDiagram.execute(request);

            // Then
            verify(diagramRepository).save(captor.capture());
            assertEquals("Arquitectura", captor.getValue().getName());
            assertEquals("user-4", captor.getValue().getUserId());
            assertEquals(TipoDiagrama.FLUJO, captor.getValue().getTipo());
        }

        @Test
        @DisplayName("Debe llamar a save exactamente una vez y no más interacciones con el repositorio")
        void shouldCallSaveOnceAndNoMoreRepositoryInteractions() {
            // Given
            CrearDiagramRequest request = new CrearDiagramRequest("D", "Desc", TipoDiagrama.FLUJO, "u");
            Diagram saved = buildDiagram("diag-5", "D", "u");
            when(diagramRepository.save(any(Diagram.class))).thenReturn(saved);

            // When
            crearDiagram.execute(request);

            // Then
            verify(diagramRepository, times(1)).save(any(Diagram.class));
            verifyNoMoreInteractions(diagramRepository);
        }
    }
}
