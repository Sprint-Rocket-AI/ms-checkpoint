package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.exceptions.ActividadNotFoundException;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
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
@DisplayName("ObtenerActividadById")
class ObtenerActividadByIdTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ObtenerActividadById obtenerActividadById;

    @Nested
    @DisplayName("Búsqueda exitosa")
    class BusquedaExitosa {

        @Test
        @DisplayName("Debe retornar ActividadResponse con todos los campos mapeados cuando el id existe")
        void shouldReturnActividadResponseWhenIdExists() {
            // Given
            String id = "act-001";
            LocalDateTime fecha = LocalDateTime.of(2024, 6, 1, 10, 0);
            Actividad actividad = buildActividad(id, "dev-001", "Implementar OAuth2",
                    "Descripción detallada", EstadoActividad.PENDIENTE, fecha);
            when(actividadRepository.findById(id)).thenReturn(Optional.of(actividad));

            // When
            ActividadResponse response = obtenerActividadById.execute(id);

            // Then
            assertNotNull(response);
            assertEquals(id, response.id());
            assertEquals("dev-001", response.userId());
            assertEquals("Implementar OAuth2", response.titulo());
            assertEquals("Descripción detallada", response.descripcion());
            assertEquals(EstadoActividad.PENDIENTE, response.estado());
            assertEquals(fecha, response.fechaCreacion());
        }

        @Test
        @DisplayName("Debe invocar findById() exactamente una vez con el id proporcionado")
        void shouldCallFindByIdExactlyOnceWhenSearching() {
            // Given
            String id = "act-002";
            when(actividadRepository.findById(id)).thenReturn(Optional.of(buildActividad(id)));

            // When
            obtenerActividadById.execute(id);

            // Then
            verify(actividadRepository, times(1)).findById(id);
            verifyNoMoreInteractions(actividadRepository);
        }

        @Test
        @DisplayName("Debe retornar correctamente actividad con descripcion null")
        void shouldReturnResponseWhenActivityHasNullDescription() {
            // Given
            String id = "act-003";
            Actividad actividad = buildActividad(id, "dev-003", "Sin descripción",
                    null, EstadoActividad.EN_PROCESO, LocalDateTime.now());
            when(actividadRepository.findById(id)).thenReturn(Optional.of(actividad));

            // When
            ActividadResponse response = obtenerActividadById.execute(id);

            // Then
            assertNotNull(response);
            assertNull(response.descripcion());
            assertEquals(EstadoActividad.EN_PROCESO, response.estado());
        }

        @Test
        @DisplayName("Debe retornar la actividad con estado COMPLETADA correctamente")
        void shouldReturnCompletedActivityWhenStatusIsCompletada() {
            // Given
            String id = "act-004";
            Actividad actividad = buildActividad(id, "dev-004", "Tarea lista",
                    "Desc", EstadoActividad.COMPLETADA, LocalDateTime.now());
            when(actividadRepository.findById(id)).thenReturn(Optional.of(actividad));

            // When
            ActividadResponse response = obtenerActividadById.execute(id);

            // Then
            assertEquals(EstadoActividad.COMPLETADA, response.estado());
        }
    }

    @Nested
    @DisplayName("Actividad no encontrada")
    class ActividadNoEncontrada {

        @Test
        @DisplayName("Debe lanzar ActividadNotFoundException cuando el id no existe en el repositorio")
        void shouldThrowActividadNotFoundExceptionWhenIdDoesNotExist() {
            // Given
            String id = "id-inexistente";
            when(actividadRepository.findById(id)).thenReturn(Optional.empty());

            // When / Then
            ActividadNotFoundException ex = assertThrows(
                    ActividadNotFoundException.class,
                    () -> obtenerActividadById.execute(id)
            );

            assertTrue(ex.getMessage().contains(id));
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
                    () -> obtenerActividadById.execute(id)
            );

            assertEquals("Actividad", ex.getEntidad());
            assertEquals(id, ex.getIdentificador());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Actividad buildActividad(String id) {
        return buildActividad(id, "dev-001", "Título", "Descripción",
                EstadoActividad.PENDIENTE, LocalDateTime.now());
    }

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
