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
@DisplayName("PosponerRecordatorio")
class PosponerRecordatorioTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private PosponerRecordatorio posponerRecordatorio;

    @Nested
    @DisplayName("Posposición exitosa")
    class PosposicionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse correctamente cuando el recordatorio existe")
        void shouldReturnResponseWhenRecordatorioExists() {
            // Given
            String id = "rec-001";
            Recordatorio existente = buildRecordatorio(id, true);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = posponerRecordatorio.execute(id, 10);

            // Then
            assertNotNull(response);
            assertEquals(id, response.id());
        }

        @Test
        @DisplayName("Debe actualizar fechaCreacion al momento de posponer")
        void shouldUpdateFechaCreacionWhenPostponing() {
            // Given
            String id = "rec-002";
            LocalDateTime fechaOriginal = LocalDateTime.now().minusHours(2);
            Recordatorio existente = buildRecordatorio(id, true);
            existente.setFechaCreacion(fechaOriginal);
            LocalDateTime antesDeEjecutar = LocalDateTime.now().minusSeconds(1);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            posponerRecordatorio.execute(id, 30);

            // Then
            LocalDateTime nuevaFecha = captor.getValue().getFechaCreacion();
            assertNotNull(nuevaFecha);
            assertFalse(nuevaFecha.isBefore(antesDeEjecutar),
                    "La fechaCreacion debe actualizarse al momento de posponer");
        }

        @Test
        @DisplayName("Debe invocar save() exactamente una vez con distintos valores de minutos")
        void shouldCallSaveExactlyOnceRegardlessOfMinutesValue() {
            // Given
            String id = "rec-003";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id, true)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When — probar con 5, 10 y 60 minutos
            posponerRecordatorio.execute(id, 5);
            posponerRecordatorio.execute(id, 10);
            posponerRecordatorio.execute(id, 60);

            // Then
            verify(recordatorioRepository, times(3)).findById(id);
            verify(recordatorioRepository, times(3)).save(any());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez por ejecución")
        void shouldCallFindByIdAndSaveExactlyOncePerExecution() {
            // Given
            String id = "rec-004";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id, true)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            posponerRecordatorio.execute(id, 15);

            // Then
            verify(recordatorioRepository, times(1)).findById(id);
            verify(recordatorioRepository, times(1)).save(any());
            verifyNoMoreInteractions(recordatorioRepository);
        }

        @Test
        @DisplayName("Debe posponer correctamente con valor mínimo de 1 minuto")
        void shouldPostponeCorrectlyWithMinimumOneMinute() {
            // Given
            String id = "rec-005";
            Recordatorio existente = buildRecordatorio(id, true);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = posponerRecordatorio.execute(id, 1);

            // Then
            assertNotNull(response);
            assertNotNull(captor.getValue().getFechaCreacion());
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
                    () -> posponerRecordatorio.execute(id, 10)
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
                    () -> posponerRecordatorio.execute(id, 10));

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
