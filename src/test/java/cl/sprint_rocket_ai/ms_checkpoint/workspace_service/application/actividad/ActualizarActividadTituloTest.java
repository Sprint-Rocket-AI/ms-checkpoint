package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActualizarActividadTituloRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
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
@DisplayName("ActualizarActividadTitulo")
class ActualizarActividadTituloTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ActualizarActividadTitulo actualizarActividadTitulo;

    @Nested
    @DisplayName("Actualización exitosa de título")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar ActividadResponse con el nuevo título cuando la actividad existe")
        void shouldReturnResponseWithNewTitleWhenActivityExists() {
            // Given
            String id = "act-001";
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Nuevo título OAuth2");
            Actividad existente = buildActividad(id, "Título viejo", "Desc original");
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividadTitulo.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals("Nuevo título OAuth2", response.titulo());
            assertEquals("Desc original", response.descripcion(),
                    "La descripción no debe verse afectada por la actualización de título");
        }

        @Test
        @DisplayName("Debe persistir solo el nuevo título sin modificar los demás campos")
        void shouldPersistOnlyNewTitleWithoutModifyingOtherFields() {
            // Given
            String id = "act-002";
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Título actualizado");
            Actividad existente = buildActividad(id, "Título original", "Descripción preservada");
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividadTitulo.execute(id, request);

            // Then
            Actividad guardada = captor.getValue();
            assertEquals("Título actualizado", guardada.getTitulo());
            assertEquals("Descripción preservada", guardada.getDescripcion());
            assertEquals(EstadoActividad.PENDIENTE, guardada.getEstado());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdatingTitle() {
            // Given
            String id = "act-003";
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Otro título");
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));
            when(actividadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividadTitulo.execute(id, request);

            // Then
            verify(actividadRepository, times(1)).findById(id);
            verify(actividadRepository, times(1)).save(any(Actividad.class));
            verifyNoMoreInteractions(actividadRepository);
        }
    }

    @Nested
    @DisplayName("Actividad no encontrada")
    class ActividadNoEncontrada {

        @Test
        @DisplayName("Debe lanzar ActividadNotFoundException cuando el id no existe")
        void shouldThrowActividadNotFoundExceptionWhenIdDoesNotExist() {
            // Given
            String id = "id-inexistente";
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Título");
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> actualizarActividadTitulo.execute(id, request)
            );

            assertTrue(ex.getMessage().contains(id));
            assertEquals("Actividad", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe no invocar save() cuando la actividad no existe")
        void shouldNotCallSaveWhenActivityIsNotFound() {
            // Given
            String id = "ghost-id";
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Título");
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When
            assertThrows(ActividadNotFoundException.class,
                    () -> actualizarActividadTitulo.execute(id, request));

            // Then
            verify(actividadRepository, never()).save(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Actividad buildActividad(String id) {
        return buildActividad(id, "Título default", "Descripción default");
    }

    private Actividad buildActividad(String id, String titulo, String descripcion) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId("dev-001");
        a.setTitulo(titulo);
        a.setDescripcion(descripcion);
        a.setEstado(EstadoActividad.PENDIENTE);
        a.setFechaCreacion(LocalDateTime.now());
        return a;
    }
}
