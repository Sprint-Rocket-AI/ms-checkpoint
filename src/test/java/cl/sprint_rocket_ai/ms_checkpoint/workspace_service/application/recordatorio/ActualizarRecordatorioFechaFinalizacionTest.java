package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioFechaFinalizacionRequest;
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
@DisplayName("ActualizarRecordatorioFechaFinalizacion")
class ActualizarRecordatorioFechaFinalizacionTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private ActualizarRecordatorioFechaFinalizacion actualizarFecha;

    @Nested
    @DisplayName("Actualización exitosa de fecha de finalización")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse con la nueva fechaExpiracion cuando el recordatorio existe")
        void shouldReturnResponseWithNewFechaExpiracionWhenRecordatorioExists() {
            // Given
            String id = "rec-001";
            LocalDateTime nuevaFecha = LocalDateTime.of(2027, 3, 15, 9, 0);
            ActualizarRecordatorioFechaFinalizacionRequest request =
                    new ActualizarRecordatorioFechaFinalizacionRequest(nuevaFecha);
            Recordatorio existente = buildRecordatorio(id, "Sprint review",
                    LocalDateTime.of(2026, 1, 1, 0, 0));
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = actualizarFecha.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals(nuevaFecha, response.fechaExpiracion());
        }

        @Test
        @DisplayName("Debe persistir la nueva fechaExpiracion sin modificar título ni activo")
        void shouldPersistNewFechaExpiracionWithoutModifyingOtherFields() {
            // Given
            String id = "rec-002";
            LocalDateTime nuevaFecha = LocalDateTime.of(2027, 6, 30, 18, 0);
            ActualizarRecordatorioFechaFinalizacionRequest request =
                    new ActualizarRecordatorioFechaFinalizacionRequest(nuevaFecha);
            Recordatorio existente = buildRecordatorio(id, "Título intocable",
                    LocalDateTime.of(2025, 1, 1, 0, 0));
            existente.setActivo(true);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarFecha.execute(id, request);

            // Then
            Recordatorio guardado = captor.getValue();
            assertEquals(nuevaFecha, guardado.getFechaExpiracion());
            assertEquals("Título intocable", guardado.getTitulo());
            assertTrue(guardado.isActivo());
        }

        @Test
        @DisplayName("Debe preservar la fechaExpiracion original cuando el request trae null")
        void shouldPreserveOriginalFechaExpiracionWhenRequestIsNull() {
            // Given
            String id = "rec-003";
            LocalDateTime fechaOriginal = LocalDateTime.of(2026, 12, 31, 23, 59);
            ActualizarRecordatorioFechaFinalizacionRequest request =
                    new ActualizarRecordatorioFechaFinalizacionRequest(null);
            Recordatorio existente = buildRecordatorio(id, "Recordatorio", fechaOriginal);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarFecha.execute(id, request);

            // Then
            assertEquals(fechaOriginal, captor.getValue().getFechaExpiracion(),
                    "La fechaExpiracion no debe cambiar cuando el request trae null");
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdatingDate() {
            // Given
            String id = "rec-004";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarFecha.execute(id, new ActualizarRecordatorioFechaFinalizacionRequest(LocalDateTime.now()));

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
                    () -> actualizarFecha.execute(id,
                            new ActualizarRecordatorioFechaFinalizacionRequest(LocalDateTime.now()))
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
                    () -> actualizarFecha.execute(id,
                            new ActualizarRecordatorioFechaFinalizacionRequest(LocalDateTime.now())));

            // Then
            verify(recordatorioRepository, never()).save(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id) {
        return buildRecordatorio(id, "Título default", null);
    }

    private Recordatorio buildRecordatorio(String id, String titulo, LocalDateTime fechaExpiracion) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId("dev-001");
        r.setTitulo(titulo);
        r.setActivo(true);
        r.setFechaExpiracion(fechaExpiracion);
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}
