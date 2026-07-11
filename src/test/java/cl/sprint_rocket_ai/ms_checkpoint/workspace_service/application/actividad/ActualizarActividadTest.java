package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActualizarActividadRequest;
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
@DisplayName("ActualizarActividad")
class ActualizarActividadTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ActualizarActividad actualizarActividad;

    // =========================================================================
    // Happy path
    // =========================================================================

    @Nested
    @DisplayName("Actualización exitosa")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar ActividadResponse actualizado cuando la actividad existe y todos los campos cambian")
        void shouldReturnUpdatedResponseWhenActivityExistsAndAllFieldsChange() {
            // Given
            String id = "act-001";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Nuevo título", "Nueva descripción", EstadoActividad.EN_PROCESO
            );
            Actividad existente = buildActividad(id, "dev-001", "Título viejo",
                    "Desc vieja", EstadoActividad.PENDIENTE, LocalDateTime.now());

            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividad.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals(id, response.id());
            assertEquals("Nuevo título", response.titulo());
            assertEquals("Nueva descripción", response.descripcion());
            assertEquals(EstadoActividad.EN_PROCESO, response.estado());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez cuando la actualización es exitosa")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdateIsSuccessful() {
            // Given
            String id = "act-002";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Título actualizado", null, null
            );
            Actividad existente = buildActividad(id, "dev-002", "Título original",
                    "Desc", EstadoActividad.PENDIENTE, LocalDateTime.now());

            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividad.execute(id, request);

            // Then
            verify(actividadRepository, times(1)).findById(id);
            verify(actividadRepository, times(1)).save(any(Actividad.class));
            verifyNoMoreInteractions(actividadRepository);
        }

        @Test
        @DisplayName("Debe persistir la actividad con los nuevos valores cuando el request aplica cambios parciales")
        void shouldPersistActivityWithNewValuesWhenRequestAppliesPartialChanges() {
            // Given
            String id = "act-003";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Título modificado", null, EstadoActividad.COMPLETADA
            );
            Actividad existente = buildActividad(id, "dev-003", "Título original",
                    "Descripción original", EstadoActividad.EN_PROCESO, LocalDateTime.now());

            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividad.execute(id, request);

            // Then
            Actividad guardada = captor.getValue();
            assertEquals("Título modificado", guardada.getTitulo(),
                    "El título debe haberse actualizado");
            assertEquals("Descripción original", guardada.getDescripcion(),
                    "La descripción no debe cambiar cuando viene null en el request");
            assertEquals(EstadoActividad.COMPLETADA, guardada.getEstado(),
                    "El estado debe haberse actualizado a COMPLETADA");
        }

        @Test
        @DisplayName("Debe preservar los campos originales cuando el request tiene todos los campos null")
        void shouldPreserveOriginalFieldsWhenRequestHasAllNullFields() {
            // Given
            String id = "act-004";
            ActualizarActividadRequest requestVacio = new ActualizarActividadRequest(
                    null, null, null
            );
            Actividad existente = buildActividad(id, "dev-004", "Título intocable",
                    "Desc intocable", EstadoActividad.PENDIENTE, LocalDateTime.now());

            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarActividad.execute(id, requestVacio);

            // Then
            Actividad guardada = captor.getValue();
            assertEquals("Título intocable", guardada.getTitulo());
            assertEquals("Desc intocable", guardada.getDescripcion());
            assertEquals(EstadoActividad.PENDIENTE, guardada.getEstado());
        }
    }

    // =========================================================================
    // Lógica de cambio de estado
    // =========================================================================

    @Nested
    @DisplayName("Lógica de cambio de estado")
    class CambioDeEstado {

        @Test
        @DisplayName("Debe aplicar el nuevo estado cuando el estado cambia de PENDIENTE a EN_PROCESO")
        void shouldApplyNewStatusWhenStatusChangesFromPendienteToEnProceso() {
            // Given
            String id = "act-005";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    null, null, EstadoActividad.EN_PROCESO
            );
            Actividad existente = buildActividad(id, "dev-005", "Tarea en transición",
                    "Desc", EstadoActividad.PENDIENTE, LocalDateTime.now());

            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividad.execute(id, request);

            // Then
            assertEquals(EstadoActividad.EN_PROCESO, response.estado(),
                    "El estado debe haber cambiado a EN_PROCESO");
            assertEquals(EstadoActividad.EN_PROCESO, captor.getValue().getEstado(),
                    "La actividad persistida debe tener el nuevo estado");
        }

        @Test
        @DisplayName("Debe aplicar el nuevo estado cuando el estado cambia de EN_PROCESO a COMPLETADA")
        void shouldApplyNewStatusWhenStatusChangesFromEnProcesoToCompletada() {
            // Given
            String id = "act-006";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    null, null, EstadoActividad.COMPLETADA
            );
            Actividad existente = buildActividad(id, "dev-006", "Tarea completada",
                    "Desc", EstadoActividad.EN_PROCESO, LocalDateTime.now());

            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividad.execute(id, request);

            // Then
            assertEquals(EstadoActividad.COMPLETADA, response.estado());
        }

        @Test
        @DisplayName("Debe mantener el mismo estado cuando el request envía el mismo estado que ya tiene la actividad")
        void shouldKeepSameStatusWhenRequestSendsIdenticalStatus() {
            // Given
            String id = "act-007";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Nuevo título", null, EstadoActividad.PENDIENTE  // mismo estado
            );
            Actividad existente = buildActividad(id, "dev-007", "Título viejo",
                    "Desc", EstadoActividad.PENDIENTE, LocalDateTime.now());

            ArgumentCaptor<Actividad> captor = ArgumentCaptor.forClass(Actividad.class);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividad.execute(id, request);

            // Then
            assertEquals(EstadoActividad.PENDIENTE, response.estado(),
                    "El estado debe seguir siendo PENDIENTE");
            assertEquals("Nuevo título", response.titulo(),
                    "El título sí debe haberse actualizado");
        }

        @Test
        @DisplayName("Debe manejar correctamente el estado anterior null sin lanzar excepción")
        void shouldHandleNullPreviousStatusWithoutThrowingException() {
            // Given — actividad existente sin estado previo (dato inconsistente en BD)
            String id = "act-008";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    null, null, EstadoActividad.PENDIENTE
            );
            Actividad existente = buildActividad(id, "dev-008", "Tarea huérfana",
                    "Desc", null, LocalDateTime.now());  // estado null en BD

            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When / Then — no debe lanzar NullPointerException
            assertDoesNotThrow(() -> actualizarActividad.execute(id, request),
                    "No debe fallar cuando el estado previo en BD es null");
        }

        @Test
        @DisplayName("Debe aplicar estado CANCELADA correctamente cuando el request lo indica")
        void shouldApplyCanceladaStatusWhenRequestIndicatesIt() {
            // Given
            String id = "act-009";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    null, null, EstadoActividad.CANCELADA
            );
            Actividad existente = buildActividad(id, "dev-009", "Tarea cancelada",
                    "Desc", EstadoActividad.EN_PROCESO, LocalDateTime.now());

            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ActividadResponse response = actualizarActividad.execute(id, request);

            // Then
            assertEquals(EstadoActividad.CANCELADA, response.estado());
        }
    }

    // =========================================================================
    // Error — actividad no encontrada
    // =========================================================================

    @Nested
    @DisplayName("Actividad no encontrada")
    class ActividadNoEncontrada {

        @Test
        @DisplayName("Debe lanzar ActividadNotFoundException cuando el id no existe en el repositorio")
        void shouldThrowActividadNotFoundExceptionWhenIdDoesNotExist() {
            // Given
            String idInexistente = "id-que-no-existe";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Título", null, null
            );
            when(actividadRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> actualizarActividad.execute(idInexistente, request)
            );

            assertTrue(ex.getMessage().contains(idInexistente),
                    "El mensaje de la excepción debe contener el id buscado");
        }

        @Test
        @DisplayName("Debe no invocar save() cuando la actividad no es encontrada")
        void shouldNotCallSaveWhenActivityIsNotFound() {
            // Given
            String idInexistente = "ghost-id";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    "Título", null, null
            );
            when(actividadRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // When
            assertThrows(ActividadNotFoundException.class,
                    () -> actualizarActividad.execute(idInexistente, request));

            // Then
            verify(actividadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ActividadNotFoundException con la entidad 'Actividad' en el mensaje")
        void shouldThrowExceptionWithActividadEntityNameWhenIdDoesNotExist() {
            // Given
            String id = "no-existe";
            ActualizarActividadRequest request = new ActualizarActividadRequest(
                    null, null, null
            );
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> actualizarActividad.execute(id, request)
            );

            assertEquals("Actividad", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Actividad buildActividad(String id, String userId, String titulo,
                                     String descripcion, EstadoActividad estado,
                                     LocalDateTime fechaCreacion) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo(titulo);
        a.setDescripcion(descripcion);
        a.setEstado(estado);
        a.setFechaCreacion(fechaCreacion);
        return a;
    }
}
