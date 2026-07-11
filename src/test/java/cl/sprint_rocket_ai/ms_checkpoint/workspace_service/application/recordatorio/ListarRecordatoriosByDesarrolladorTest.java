package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Recordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.RecordatorioMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarRecordatoriosByDesarrollador")
class ListarRecordatoriosByDesarrolladorTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private ListarRecordatoriosByDesarrollador listarRecordatorios;

    @Nested
    @DisplayName("Listado exitoso")
    class ListadoExitoso {

        @Test
        @DisplayName("Debe retornar lista de RecordatorioResponse cuando el desarrollador tiene recordatorios")
        void shouldReturnRecordatorioListWhenDeveloperHasRecordatorios() {
            // Given
            String userId = "dev-001";
            List<Recordatorio> recordatorios = List.of(
                    buildRecordatorio("rec-1", userId, "Standup diario", true),
                    buildRecordatorio("rec-2", userId, "Code review", true),
                    buildRecordatorio("rec-3", userId, "Retrospectiva", false)
            );
            when(recordatorioRepository.findByUserId(userId)).thenReturn(recordatorios);

            // When
            List<RecordatorioResponse> result = listarRecordatorios.execute(userId);

            // Then
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals("rec-1", result.get(0).id());
            assertEquals("Standup diario", result.get(0).titulo());
            assertTrue(result.get(0).activo());
            assertEquals("rec-3", result.get(2).id());
            assertFalse(result.get(2).activo());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando el desarrollador no tiene recordatorios")
        void shouldReturnEmptyListWhenDeveloperHasNoRecordatorios() {
            // Given
            String userId = "dev-sin-recordatorios";
            when(recordatorioRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            List<RecordatorioResponse> result = listarRecordatorios.execute(userId);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe invocar findByUserId() exactamente una vez con el userId proporcionado")
        void shouldCallFindByUserIdExactlyOnceWithCorrectUserId() {
            // Given
            String userId = "dev-002";
            when(recordatorioRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            listarRecordatorios.execute(userId);

            // Then
            verify(recordatorioRepository, times(1)).findByUserId(userId);
            verifyNoMoreInteractions(recordatorioRepository);
        }

        @Test
        @DisplayName("Debe preservar el orden de los recordatorios tal como los devuelve el repositorio")
        void shouldPreserveOrderWhenMappingRecordatorios() {
            // Given
            String userId = "dev-003";
            List<Recordatorio> recordatorios = List.of(
                    buildRecordatorio("rec-A", userId, "Primero", true),
                    buildRecordatorio("rec-B", userId, "Segundo", true),
                    buildRecordatorio("rec-C", userId, "Tercero", false)
            );
            when(recordatorioRepository.findByUserId(userId)).thenReturn(recordatorios);

            // When
            List<RecordatorioResponse> result = listarRecordatorios.execute(userId);

            // Then
            assertEquals("rec-A", result.get(0).id());
            assertEquals("rec-B", result.get(1).id());
            assertEquals("rec-C", result.get(2).id());
        }

        @Test
        @DisplayName("Debe mapear correctamente activo=false en los recordatorios completados")
        void shouldMapActivoFalseCorrectlyForCompletedRecordatorios() {
            // Given
            String userId = "dev-004";
            List<Recordatorio> recordatorios = List.of(
                    buildRecordatorio("rec-1", userId, "Completado", false)
            );
            when(recordatorioRepository.findByUserId(userId)).thenReturn(recordatorios);

            // When
            List<RecordatorioResponse> result = listarRecordatorios.execute(userId);

            // Then
            assertFalse(result.get(0).activo());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id, String userId, String titulo, boolean activo) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId(userId);
        r.setTitulo(titulo);
        r.setActivo(activo);
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}
