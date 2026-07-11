package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
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
@DisplayName("EliminarRecordatorio")
class EliminarRecordatorioTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private EliminarRecordatorio eliminarRecordatorio;

    @Nested
    @DisplayName("Eliminación exitosa")
    class EliminacionExitosa {

        @Test
        @DisplayName("Debe eliminar el recordatorio sin lanzar excepción cuando el id existe")
        void shouldDeleteRecordatorioWhenIdExists() {
            // Given
            String id = "rec-001";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));

            // When / Then
            assertDoesNotThrow(() -> eliminarRecordatorio.execute(id));
        }

        @Test
        @DisplayName("Debe invocar deleteById() exactamente una vez con el id correcto")
        void shouldCallDeleteByIdExactlyOnceWhenRecordatorioExists() {
            // Given
            String id = "rec-002";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));

            // When
            eliminarRecordatorio.execute(id);

            // Then
            verify(recordatorioRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Debe invocar findById() antes de deleteById() para validar existencia")
        void shouldCallFindByIdBeforeDeleteByIdWhenDeleting() {
            // Given
            String id = "rec-003";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));

            // When
            eliminarRecordatorio.execute(id);

            // Then
            var inOrder = inOrder(recordatorioRepository);
            inOrder.verify(recordatorioRepository).findById(id);
            inOrder.verify(recordatorioRepository).deleteById(id);
        }

        @Test
        @DisplayName("Debe no invocar ningún otro método del repositorio además de findById() y deleteById()")
        void shouldOnlyCallFindByIdAndDeleteByIdWhenDeleting() {
            // Given
            String id = "rec-004";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));

            // When
            eliminarRecordatorio.execute(id);

            // Then
            verify(recordatorioRepository).findById(id);
            verify(recordatorioRepository).deleteById(id);
            verifyNoMoreInteractions(recordatorioRepository);
        }
    }

    @Nested
    @DisplayName("Recordatorio no encontrado")
    class RecordatorioNoEncontrado {

        @Test
        @DisplayName("Debe lanzar RecordatorioNotFoundException cuando el id no existe")
        void shouldThrowRecordatorioNotFoundExceptionWhenIdDoesNotExist() {
            // Given
            String id = "id-inexistente";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            RecordatorioNotFoundException ex = assertThrows(
                    RecordatorioNotFoundException.class,
                    () -> eliminarRecordatorio.execute(id)
            );
            assertTrue(ex.getMessage().contains(id));
            assertEquals("Recordatorio", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe no invocar deleteById() cuando el recordatorio no existe")
        void shouldNotCallDeleteByIdWhenRecordatorioIsNotFound() {
            // Given
            String id = "ghost-id";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.empty());

            // When
            assertThrows(RecordatorioNotFoundException.class,
                    () -> eliminarRecordatorio.execute(id));

            // Then
            verify(recordatorioRepository, never()).deleteById(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId("dev-001");
        r.setTitulo("Recordatorio de prueba");
        r.setActivo(true);
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}
