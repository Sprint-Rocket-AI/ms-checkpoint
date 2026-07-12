package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("ListarActividadesByDesarrollador")
class ListarActividadesByDesarrolladorTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ListarActividadesByDesarrollador listarActividadesByDesarrollador;

    private Actividad buildActividad(String id, String userId) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo("Actividad " + id);
        a.setDescripcion("Desc");
        a.setEstado(EstadoActividad.PENDIENTE);
        a.setFechaCreacion(LocalDateTime.now());
        return a;
    }

    @Test
    @DisplayName("Debe retornar lista de ActividadResponse para el userId dado")
    void shouldReturnActividadResponseListForUserId() {
        // Given
        List<Actividad> actividades = List.of(buildActividad("a1", "user-1"), buildActividad("a2", "user-1"));
        when(actividadRepository.findByUserId("user-1")).thenReturn(actividades);

        // When
        List<ActividadResponse> result = listarActividadesByDesarrollador.execute("user-1");

        // Then
        assertEquals(2, result.size());
        assertEquals("a1", result.get(0).id());
        assertEquals("user-1", result.get(0).userId());
        verify(actividadRepository).findByUserId("user-1");
        verifyNoMoreInteractions(actividadRepository);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el usuario no tiene actividades")
    void shouldReturnEmptyListWhenUserHasNoActivities() {
        // Given
        when(actividadRepository.findByUserId("user-sin-actividades")).thenReturn(List.of());

        // When
        List<ActividadResponse> result = listarActividadesByDesarrollador.execute("user-sin-actividades");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
