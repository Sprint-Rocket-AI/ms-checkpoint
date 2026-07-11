package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.actividad.*;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.enums.EstadoActividad;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.actividad.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActividadController")
class ActividadControllerTest {

    @Mock private CrearActividad crearActividad;
    @Mock private ObtenerActividadById obtenerActividadById;
    @Mock private ListarActividadesByDesarrollador listarActividadesByDesarrollador;
    @Mock private ActualizarActividad actualizarActividad;
    @Mock private EliminarActividad eliminarActividad;
    @Mock private ListarActividadesByFecha listarActividadesByFecha;
    @Mock private ActualizarActividadTitulo actualizarActividadTitulo;
    @Mock private ActualizarActividadDescripcion actualizarActividadDescripcion;

    private ActividadController controller;

    @BeforeEach
    void setUp() {
        controller = new ActividadController(
                crearActividad, obtenerActividadById, listarActividadesByDesarrollador,
                actualizarActividad, eliminarActividad, listarActividadesByFecha,
                actualizarActividadTitulo, actualizarActividadDescripcion
        );
    }

    private ActividadResponse stubResponse(String id) {
        return new ActividadResponse(id, "user-1", "Título", "Desc",
                EstadoActividad.PENDIENTE, LocalDateTime.now());
    }

    @Nested
    @DisplayName("POST / — crear actividad")
    class Create {

        @Test
        @DisplayName("Debe retornar 201 CREATED con el body del use case")
        void shouldReturn201WithBody() {
            // Given
            CrearActividadRequest request = new CrearActividadRequest("user-1", "Título", "Desc");
            ActividadResponse expected = stubResponse("act-1");
            when(crearActividad.execute(request)).thenReturn(expected);

            // When
            ResponseEntity<ActividadResponse> response = controller.create(request);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expected, response.getBody());
            verify(crearActividad).execute(request);
        }
    }

    @Nested
    @DisplayName("GET /{id} — obtener por id")
    class FindById {

        @Test
        @DisplayName("Debe retornar 200 OK con el body del use case")
        void shouldReturn200WithBody() {
            // Given
            ActividadResponse expected = stubResponse("act-2");
            when(obtenerActividadById.execute("act-2")).thenReturn(expected);

            // When
            ResponseEntity<ActividadResponse> response = controller.findById("act-2");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("GET /desarrollador/{userId} — listar por desarrollador")
    class FindByDesarrollador {

        @Test
        @DisplayName("Debe retornar 200 OK con la lista del use case")
        void shouldReturn200WithList() {
            // Given
            List<ActividadResponse> expected = List.of(stubResponse("a1"), stubResponse("a2"));
            when(listarActividadesByDesarrollador.execute("user-1")).thenReturn(expected);

            // When
            ResponseEntity<List<ActividadResponse>> response = controller.findByDesarrollador("user-1");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("PUT /{id} — actualizar actividad")
    class Update {

        @Test
        @DisplayName("Debe retornar 200 OK con el body actualizado del use case")
        void shouldReturn200WithUpdatedBody() {
            // Given
            ActualizarActividadRequest request = new ActualizarActividadRequest("Nuevo", "Desc", EstadoActividad.EN_PROCESO);
            ActividadResponse expected = stubResponse("act-3");
            when(actualizarActividad.execute("act-3", request)).thenReturn(expected);

            // When
            ResponseEntity<ActividadResponse> response = controller.update("act-3", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/title — actualizar título")
    class UpdateTitulo {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con el id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarActividadTituloRequest request = new ActualizarActividadTituloRequest("Nuevo Título");
            ActividadResponse expected = stubResponse("act-4");
            when(actualizarActividadTitulo.execute("act-4", request)).thenReturn(expected);

            // When
            ResponseEntity<ActividadResponse> response = controller.updateTitulo("act-4", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/description — actualizar descripción")
    class UpdateDescripcion {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con el id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarActividadDescripcionRequest request = new ActualizarActividadDescripcionRequest("Nueva desc");
            ActividadResponse expected = stubResponse("act-5");
            when(actualizarActividadDescripcion.execute("act-5", request)).thenReturn(expected);

            // When
            ResponseEntity<ActividadResponse> response = controller.updateDescripcion("act-5", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("DELETE /{id} — eliminar actividad")
    class Delete {

        @Test
        @DisplayName("Debe retornar 204 NO CONTENT y llamar al use case con el id correcto")
        void shouldReturn204AndCallUseCase() {
            // When
            ResponseEntity<Void> response = controller.delete("act-6");

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(eliminarActividad).execute("act-6");
        }
    }

    @Nested
    @DisplayName("GET /fecha/{fecha}?userId= — listar por fecha")
    class FindByFecha {

        @Test
        @DisplayName("Debe retornar 200 OK con la lista del use case pasando userId y fecha")
        void shouldReturn200WithListForDateAndUser() {
            // Given
            LocalDate fecha = LocalDate.of(2026, 7, 10);
            List<ActividadResponse> expected = List.of(stubResponse("a1"));
            when(listarActividadesByFecha.execute("user-1", fecha)).thenReturn(expected);

            // When
            ResponseEntity<List<ActividadResponse>> response = controller.findByFecha(fecha, "user-1");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            verify(listarActividadesByFecha).execute("user-1", fecha);
        }
    }
}
