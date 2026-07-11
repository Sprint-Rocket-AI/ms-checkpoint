package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.ActualizarRecordatorioTituloRequest;
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
@DisplayName("ActualizarRecordatorioTitulo")
class ActualizarRecordatorioTituloTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private ActualizarRecordatorioTitulo actualizarRecordatorioTitulo;

    @Nested
    @DisplayName("Actualización exitosa de título")
    class ActualizacionExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse con el nuevo título cuando el recordatorio existe")
        void shouldReturnResponseWithNewTitleWhenRecordatorioExists() {
            // Given
            String id = "rec-001";
            ActualizarRecordatorioTituloRequest request = new ActualizarRecordatorioTituloRequest("Standup diario");
            Recordatorio existente = buildRecordatorio(id, "Título viejo", true);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            RecordatorioResponse response = actualizarRecordatorioTitulo.execute(id, request);

            // Then
            assertNotNull(response);
            assertEquals("Standup diario", response.titulo());
            assertTrue(response.activo(), "El campo activo no debe verse afectado");
        }

        @Test
        @DisplayName("Debe persistir solo el nuevo título sin modificar activo ni fechaExpiracion")
        void shouldPersistOnlyNewTitleWithoutModifyingOtherFields() {
            // Given
            String id = "rec-002";
            LocalDateTime expiracion = LocalDateTime.of(2026, 12, 31, 23, 59);
            ActualizarRecordatorioTituloRequest request = new ActualizarRecordatorioTituloRequest("Título actualizado");
            Recordatorio existente = buildRecordatorio(id, "Título original", true);
            existente.setFechaExpiracion(expiracion);
            ArgumentCaptor<Recordatorio> captor = ArgumentCaptor.forClass(Recordatorio.class);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(existente));
            when(recordatorioRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarRecordatorioTitulo.execute(id, request);

            // Then
            Recordatorio guardado = captor.getValue();
            assertEquals("Título actualizado", guardado.getTitulo());
            assertTrue(guardado.isActivo());
            assertEquals(expiracion, guardado.getFechaExpiracion());
        }

        @Test
        @DisplayName("Debe invocar findById() y save() exactamente una vez")
        void shouldCallFindByIdAndSaveExactlyOnceWhenUpdatingTitle() {
            // Given
            String id = "rec-003";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));
            when(recordatorioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            actualizarRecordatorioTitulo.execute(id, new ActualizarRecordatorioTituloRequest("Nuevo"));

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
                    () -> actualizarRecordatorioTitulo.execute(id, new ActualizarRecordatorioTituloRequest("T"))
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
                    () -> actualizarRecordatorioTitulo.execute(id, new ActualizarRecordatorioTituloRequest("T")));

            // Then
            verify(recordatorioRepository, never()).save(any());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id) {
        return buildRecordatorio(id, "Título default", true);
    }

    private Recordatorio buildRecordatorio(String id, String titulo, boolean activo) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId("dev-001");
        r.setTitulo(titulo);
        r.setActivo(activo);
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}
