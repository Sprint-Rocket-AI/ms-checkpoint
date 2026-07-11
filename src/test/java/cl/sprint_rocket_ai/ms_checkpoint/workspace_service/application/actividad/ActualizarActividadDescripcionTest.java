package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActualizarActividadDescripcionRequest;
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
@DisplayName("ActualizarActividadDescripcion")
class ActualizarActividadDescripcionTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ActualizarActividadDescripcion actualizarActividadDescripcion;

    @Nested
    @DisplayName("Actualización exitosa de descripción")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar ActividadResponse con la nueva descripción cuando la actividad existe")
        void shouldReturnResponseWithNewDescriptionWhenActivityExists() {
            // Given
            String id = "act-001";
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Nueva descripción detallada");
            Actividad existente = buildActividad(id, "Título intocable", "Descripción vieja");
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividadDescripcion.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals("Nueva descripción detallada", response.descripcion());
            assertEquals("Título intocable", response.titulo(),
                    "El título no debe verse afectado por la actualización de descripción");
        }

        @Test
        @DisplayName("Debe persistir solo la nueva descripción sin modificar título ni estado")
        void shouldPersistOnlyNewDescriptionWithoutModifyingTitleOrStatus() {
            // Given
            String id = "act-002";
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Descripción actualizada");
            Actividad existente = buildActividad(id, "Título preservado", "Descripción original");
            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividadDescripcion.execute(id, request);

            // Then
            Actividad guardada = captor.getValue();
            assertEquals("Descripción actualizada", guardada.getDescripcion());
            assertEquals("Título preservado", guardada.getTitulo());
            assertEquals(EstadoActividad.PENDIENTE, guardada.getEstado());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdatingDescription() {
            // Given
            String id = "act-003";
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Descripción nueva");
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));
            when(actividadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividadDescripcion.execute(id, request);

            // Then
            verify(actividadRepository, times(1)).findById(id);
            verify(actividadRepository, times(1)).save(any(Actividad.class));
            verifyNoMoreInteractions(actividadRepository);
        }

        @Test
        @DisplayName("Debe actualizar la descripción cuando la actividad tiene estado EN_PROCESO")
        void shouldUpdateDescriptionWhenActivityStatusIsEnProceso() {
            // Given
            String id = "act-004";
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Desc en progreso");
            Actividad existente = buildActividad(id, "Título", "Desc vieja");
            existente.setEstado(EstadoActividad.EN_PROCESO);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividadDescripcion.execute(id, request);

            // Then
            assertEquals("Desc en progreso", response.descripcion());
            assertEquals(EstadoActividad.EN_PROCESO, response.estado());
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
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Desc");
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> actualizarActividadDescripcion.execute(id, request)
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
            ActualizarActividadDescripcionRequest request =
                    new ActualizarActividadDescripcionRequest("Desc");
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When
            assertThrows(ActividadNotFoundException.class,
                    () -> actualizarActividadDescripcion.execute(id, request));

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
