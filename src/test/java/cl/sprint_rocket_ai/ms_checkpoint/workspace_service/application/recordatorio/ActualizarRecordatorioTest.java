package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioRequest;
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
@DisplayName("ActualizarRecordatorio")
class ActualizarRecordatorioTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private ActualizarRecordatorio actualizarRecordatorio;

    @Nested
    @DisplayName("Actualización exitosa")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse actualizado cuando todos los campos cambian")
        void shouldReturnUpdatedResponseWhenAllFieldsChange() {
            // Given
            String id = "rec-001";
            LocalDateTime nuevaExpiracion = LocalDateTime.of(2027, 1, 1, 0, 0);
            ActualizarRecordatorioRequest request = new ActualizarRecordatorioRequest(
                    "Nuevo título", false, nuevaExpiracion
            );
            Recordatorio existente = buildRecordatorio(id, "dev-001", "Título viejo", true, null);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = actualizarRecordatorio.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals("Nuevo título", response.titulo());
            assertFalse(response.activo());
            assertEquals(nuevaExpiracion, response.fechaExpiracion());
        }

        @Test
        @DisplayName("Debe preservar campos originales cuando el request tiene todos los campos null")
        void shouldPreserveOriginalFieldsWhenRequestHasAllNullFields() {
            // Given
            String id = "rec-002";
            LocalDateTime expiracionOriginal = LocalDateTime.of(2026, 6, 30, 12, 0);
            ActualizarRecordatorioRequest requestVacio = new ActualizarRecordatorioRequest(null, null, null);
            Recordatorio existente = buildRecordatorio(id, "dev-002", "Título original", true, expiracionOriginal);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarRecordatorio.execute(id, requestVacio);

            // Then
            Recordatorio guardado = captor.getValue();
            assertEquals("Título original", guardado.getTitulo());
            assertTrue(guardado.isActivo());
            assertEquals(expiracionOriginal, guardado.getFechaExpiracion());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdateIsSuccessful() {
            // Given
            String id = "rec-003";
            ActualizarRecordatorioRequest request = new ActualizarRecordatorioRequest("Nuevo", null, null);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarRecordatorio.execute(id, request);

            // Then
            verify(recordatorioRepository, times(1)).findById(id);
            verify(recordatorioRepository, times(1)).save(any(Recordatorio.class));
            verifyNoMoreInteractions(recordatorioRepository);
        }

        @Test
        @DisplayName("Debe actualizar activo a false (deshabilitar) cuando el request lo indica")
        void shouldSetActivoFalseWhenRequestIndicatesDeactivation() {
            // Given
            String id = "rec-004";
            ActualizarRecordatorioRequest request = new ActualizarRecordatorioRequest(null, false, null);
            Recordatorio existente = buildRecordatorio(id, "dev-004", "Rec activo", true, null);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarRecordatorio.execute(id, request);

            // Then
            assertFalse(captor.getValue().isActivo());
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
                    () -> actualizarRecordatorio.execute(id, new ActualizarRecordatorioRequest(null, null, null))
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
                    () -> actualizarRecordatorio.execute(id, new ActualizarRecordatorioRequest(null, null, null)));

            // Then
            verify(recordatorioRepository, never()).save(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id) {
        return buildRecordatorio(id, "dev-001", "Título", true, null);
    }

    private Recordatorio buildRecordatorio(String id, String userId, String titulo,
                                           boolean activo, LocalDateTime fechaExpiracion) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId(userId);
        r.setTitulo(titulo);
        r.setActivo(activo);
        r.setFechaExpiracion(fechaExpiracion);
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}
