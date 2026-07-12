package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.models.Actividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.persistences.mongodb.ActividadMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarActividadesByFecha")
class ListarActividadesByFechaTest {

    @Mock
    private ActividadMongoRepository actividadRepository;

    @InjectMocks
    private ListarActividadesByFecha listarActividadesByFecha;

    private Actividad buildActividad(String id, String userId) {
        Actividad a = new Actividad();
        a.setId(id);
        a.setUserId(userId);
        a.setTitulo("Actividad " + id);
        a.setEstado(EstadoActividad.COMPLETADA);
        a.setFechaCreacion(LocalDateTime.now());
        return a;
    }

    @Test
    @DisplayName("Debe buscar actividades con rango inicio-fin del día indicado para el userId")
    void shouldSearchWithFullDayRangeForUserId() {
        // Given
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        LocalDateTime expectedDesde = fecha.atStartOfDay();
        LocalDateTime expectedHasta = fecha.atTime(LocalTime.MAX);
        List<Actividad> actividades = List.of(buildActividad("a1", "user-1"));
        when(actividadRepository.findByUserIdAndFechaCreacionBetween("user-1", expectedDesde, expectedHasta))
                .thenReturn(actividades);

        // When
        List<ActividadResponse> result = listarActividadesByFecha.execute("user-1", fecha);

        // Then
        assertEquals(1, result.size());
        verify(actividadRepository).findByUserIdAndFechaCreacionBetween("user-1", expectedDesde, expectedHasta);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay actividades en esa fecha")
    void shouldReturnEmptyListWhenNoActivitiesOnDate() {
        // Given
        when(actividadRepository.findByUserIdAndFechaCreacionBetween(any(), any(), any()))
                .thenReturn(List.of());

        // When
        List<ActividadResponse> result = listarActividadesByFecha.execute("user-1", LocalDate.now());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
