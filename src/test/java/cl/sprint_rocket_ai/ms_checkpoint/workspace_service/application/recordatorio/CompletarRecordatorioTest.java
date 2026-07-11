package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
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
@DisplayName("CompletarRecordatorio")
class CompletarRecordatorioTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private CompletarRecordatorio completarRecordatorio;

    @Nested
    @DisplayName("Completación exitosa")
    class CompletacionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse con activo=false cuando el recordatorio existe")
        void shouldReturnResponseWithActivoFalseWhenRecordatorioExists() {
            // Given
            String id = "rec-001";
            Recordatorio existente = buildRecordatorio(id, true);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = completarRecordatorio.execute(id);

            // Then
            assertNotNull(response);
            assertFalse(response.activo(), "El recordatorio debe quedar inactivo tras completarse");
        }

        @Test
        @DisplayName("Debe setear activo=false en el objeto persistido")
        void shouldSetActivoFalseInPersistedRecordatorio() {
            // Given
            String id = "rec-002";
            Recordatorio existente = buildRecordatorio(id, true);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            completarRecordatorio.execute(id);

            // Then
            assertFalse(captor.getValue().isActivo(),
                    "El campo activo debe ser false al momento de persistir");
        }

        @Test
        @DisplayName("Debe setear fechaCreacion no nula al momento de completar")
        void shouldSetNonNullFechaCreacionWhenCompletingRecordatorio() {
            // Given
            String id = "rec-003";
            LocalDateTime antesDeEjecutar = LocalDateTime.now().minusSeconds(1);
            Recordatorio existente = buildRecordatorio(id, true);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            completarRecordatorio.execute(id);

            // Then
            LocalDateTime fechaCreacion = captor.getValue().getFechaCreacion();
            assertNotNull(fechaCreacion);
            assertFalse(fechaCreacion.isBefore(antesDeEjecutar),
                    "La fechaCreacion actualizada debe ser igual o posterior al inicio de la ejecución");
        }

        @Test
        @DisplayName("Debe completar correctamente un recordatorio que ya estaba inactivo (idempotente)")
        void shouldCompleteRecordatorioWhenItIsAlreadyInactive() {
            // Given
            String id = "rec-004";
            Recordatorio yaInactivo = buildRecordatorio(id, false); // ya estaba inactivo
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(yaInactivo));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = completarRecordatorio.execute(id);

            // Then
            assertFalse(response.activo(), "Debe seguir inactivo tras completar");
            verify(recordatorioRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenCompleting() {
            // Given
            String id = "rec-005";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id, true)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            completarRecordatorio.execute(id);

            // Then
            verify(recordatorioRepository, times(1)).findById(id);
            verify(recordatorioRepository, times(1)).save(any());
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
                    () -> completarRecordatorio.execute(id)
            );
            assertEquals("Recordatorio", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }

        @Test
        @DisplayName("Debe no invocar save() cuando el recordatorio no existe")
        void shouldNotCallSaveWhenRecordatorioIsNotFound() {
            // Given
            String id = "ghost-id";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.empty());

            // When
            assertThrows(RecordatorioNotFoundException.class,
                    () -> completarRecordatorio.execute(id));

            // Then
            verify(recordatorioRepository, never()).save(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id, boolean activo) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId("dev-001");
        r.setTitulo("Recordatorio de prueba");
        r.setActivo(activo);
        r.setFechaCreacion(LocalDateTime.now().minusHours(1));
        return r;
    }
}
