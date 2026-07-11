package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.CrearActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.ListarActividadesByFecha;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.ActividadResponse;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.CrearActividadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActividadTools")
class ActividadToolsTest {

    @Mock private CrearActividad crearActividad;
    @Mock private ListarActividadesByDesarrollador listarActividadesByDesarrollador;
    @Mock private ListarActividadesByFecha listarActividadesByFecha;

    @InjectMocks
    private ActividadTools actividadTools;

    private ActividadResponse stubResponse(String id) {
        return new ActividadResponse(id, "user-1", "Título", "Desc", EstadoActividad.PENDIENTE, LocalDateTime.now());
    }

    @Test
    @DisplayName("crearActividad debe construir el request correcto y retornar mensaje de éxito")
    void shouldBuildCorrectRequestAndReturnSuccessMessage() {
        // Given
        when(crearActividad.execute(any(CrearActividadRequest.class))).thenReturn(stubResponse("a1"));
        ArgumentCaptor<CrearActividadRequest> captor = ArgumentCaptor.forClass(CrearActividadRequest.class);

        // When
        String result = actividadTools.crearActividad("user-1", "Mi tarea", "Descripción");

        // Then
        verify(crearActividad).execute(captor.capture());
        assertEquals("user-1", captor.getValue().userId());
        assertEquals("Mi tarea", captor.getValue().titulo());
        assertEquals("Descripción", captor.getValue().descripcion());
        assertEquals("Actividad creada con éxito", result);
    }

    @Test
    @DisplayName("listarActividadesByDesarrollador debe delegar al use case con el userId correcto")
    void shouldDelegateToUseCaseWithCorrectUserId() {
        // Given
        List<ActividadResponse> expected = List.of(stubResponse("a1"), stubResponse("a2"));
        when(listarActividadesByDesarrollador.execute("user-1")).thenReturn(expected);

        // When
        List<ActividadResponse> result = actividadTools.listarActividadesByDesarrollador("user-1");

        // Then
        assertEquals(2, result.size());
        verify(listarActividadesByDesarrollador).execute("user-1");
    }

    @Test
    @DisplayName("listarActividadesByFecha debe delegar al use case con userId y fecha correctos")
    void shouldDelegateToUseCaseWithUserIdAndDate() {
        // Given
        LocalDate fecha = LocalDate.of(2026, 7, 10);
        List<ActividadResponse> expected = List.of(stubResponse("a1"));
        when(listarActividadesByFecha.execute("user-1", fecha)).thenReturn(expected);

        // When
        List<ActividadResponse> result = actividadTools.listarActividadesByFecha("user-1", fecha);

        // Then
        assertEquals(1, result.size());
        verify(listarActividadesByFecha).execute("user-1", fecha);
    }
}
