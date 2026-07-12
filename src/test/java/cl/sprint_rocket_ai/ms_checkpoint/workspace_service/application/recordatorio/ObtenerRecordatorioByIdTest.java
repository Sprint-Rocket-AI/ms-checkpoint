package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.RecordatorioNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerRecordatorioById")
class ObtenerRecordatorioByIdTest {

    @Mock
    private RecordatorioMongoRepository recordatorioRepository;

    @InjectMocks
    private ObtenerRecordatorioById obtenerRecordatorioById;

    @Nested
    @DisplayName("Búsqueda exitosa")
    class BusquedaExitosa {

        @Test
        @DisplayName("Debe retornar RecordatorioResponse con todos los campos mapeados cuando el id existe")
        void shouldReturnRecordatorioResponseWhenIdExists() {
            // Given
            String id = "rec-001";
            LocalDateTime expiracion = LocalDateTime.of(2026, 12, 31, 23, 59);
            LocalDateTime creacion = LocalDateTime.of(2024, 1, 15, 10, 0);
            Recordatorio recordatorio = buildRecordatorio(id, "dev-001",
                    "Sincronización matutina", true, expiracion, creacion);
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(recordatorio));

            // When
            RecordatorioResponse response = obtenerRecordatorioById.execute(id);

            // Then
            assertNotNull(response);
            assertEquals(id, response.id());
            assertEquals("dev-001", response.userId());
            assertEquals("Sincronización matutina", response.titulo());
            assertTrue(response.activo());
            assertEquals(expiracion, response.fechaExpiracion());
            assertEquals(creacion, response.fechaCreacion());
        }

        @Test
        @DisplayName("Debe invocar findById() exactamente una vez con el id proporcionado")
        void shouldCallFindByIdExactlyOnceWhenSearching() {
            // Given
            String id = "rec-002";
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(buildRecordatorio(id)));

            // When
            obtenerRecordatorioById.execute(id);

            // Then
            verify(recordatorioRepository, times(1)).findById(id);
            verifyNoMoreInteractions(recordatorioRepository);
        }

        @Test
        @DisplayName("Debe retornar correctamente un recordatorio inactivo")
        void shouldReturnInactiveRecordatorioWhenActivoIsFalse() {
            // Given
            String id = "rec-003";
            Recordatorio inactivo = buildRecordatorio(id, "dev-003", "Completado", false, null, LocalDateTime.now());
            when(recordatorioRepository.findById(id)).thenReturn(Optional.of(inactivo));

            // When
            RecordatorioResponse response = obtenerRecordatorioById.execute(id);

            // Then
            assertFalse(response.activo());
            assertNull(response.fechaExpiracion());
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
                    () -> obtenerRecordatorioById.execute(id)
            );
            assertTrue(ex.getMessage().contains(id));
            assertEquals("Recordatorio", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Recordatorio buildRecordatorio(String id) {
        return buildRecordatorio(id, "dev-001", "Título", true, null, LocalDateTime.now());
    }

    private Recordatorio buildRecordatorio(String id, String userId, String titulo,
                                           boolean activo, LocalDateTime fechaExpiracion,
                                           LocalDateTime fechaCreacion) {
        Recordatorio r = new Recordatorio();
        r.setId(id);
        r.setUserId(userId);
        r.setTitulo(titulo);
        r.setActivo(activo);
        r.setFechaExpiracion(fechaExpiracion);
        r.setFechaCreacion(fechaCreacion);
        return r;
    }
}
