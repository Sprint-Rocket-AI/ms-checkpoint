package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EliminarActividad")
class EliminarActividadTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private EliminarActividad eliminarActividad;

    @Nested
    @DisplayName("Eliminación exitosa")
    class EliminacionExitosa {

        @Test
        @DisplayName("Debe eliminar la actividad correctamente cuando el id existe")
        void shouldDeleteActivityWhenIdExists() {
            // Given
            String id = "act-001";
            Actividad existente = buildActividad(id);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(existente));
            doNothing().when(actividadRepository).deleteById(id);

            // When / Then — no debe lanzar excepción
            assertDoesNotThrow(() -> eliminarActividad.execute(id));
        }

        @Test
        @DisplayName("Debe invocar deleteById() exactamente una vez con el id correcto")
        void shouldCallDeleteByIdExactlyOnceWhenActivityExists() {
            // Given
            String id = "act-002";
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));

            // When
            eliminarActividad.execute(id);

            // Then
            verify(actividadRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Debe invocar findById() antes de deleteById() para validar existencia")
        void shouldCallFindByIdBeforeDeleteByIdWhenDeleting() {
            // Given
            String id = "act-003";
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));

            // When
            eliminarActividad.execute(id);

            // Then
            var inOrder = inOrder(actividadRepository);
            inOrder.verify(actividadRepository).findById(id);
            inOrder.verify(actividadRepository).deleteById(id);
        }

        @Test
        @DisplayName("Debe no invocar ningún otro método del repositorio además de findById() y deleteById()")
        void shouldOnlyCallFindByIdAndDeleteByIdWhenDeletingActivity() {
            // Given
            String id = "act-004";
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));

            // When
            eliminarActividad.execute(id);

            // Then
            verify(actividadRepository).findById(id);
            verify(actividadRepository).deleteById(id);
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
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> eliminarActividad.execute(id)
            );

            assertTrue(ex.getMessage().contains(id));
        }

        @Test
        @DisplayName("Debe no invocar deleteById() cuando el id no existe")
        void shouldNotCallDeleteByIdWhenActivityIsNotFound() {
            // Given
            String id = "ghost-id";
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When
            assertThrows(ActividadNotFoundException.class, () -> eliminarActividad.execute(id));

            // Then
            verify(actividadRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción con entidad 'Actividad' e identificador correcto")
        void shouldThrowExceptionWithCorrectEntityAndIdentifierWhenNotFound() {
            // Given
            String id = "no-existe";
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> eliminarActividad.execute(id)
            );

            assertEquals("Actividad", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Actividad buildActividad(String id) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId("dev-001");
        a.setTitulo("Actividad de prueba");
        a.setEstado(EstadoActividad.PENDIENTE);
        a.setFechaCreacion(LocalDateTime.now());
        return a;
    }
}
