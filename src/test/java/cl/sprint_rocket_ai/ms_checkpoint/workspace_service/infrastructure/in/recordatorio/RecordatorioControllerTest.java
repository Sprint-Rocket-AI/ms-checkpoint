package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.*;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordatorioController")
class RecordatorioControllerTest {

    @Mock private CrearRecordatorio crearRecordatorio;
    @Mock private ObtenerRecordatorioById obtenerRecordatorioById;
    @Mock private ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador;
    @Mock private ActualizarRecordatorio actualizarRecordatorio;
    @Mock private EliminarRecordatorio eliminarRecordatorio;
    @Mock private CompletarRecordatorio completarRecordatorio;
    @Mock private PosponerRecordatorio posponerRecordatorio;
    @Mock private ActualizarRecordatorioFechaFinalizacion actualizarRecordatorioFechaFinalizacion;
    @Mock private ActualizarRecordatorioTitulo actualizarRecordatorioTitulo;

    private RecordatorioController controller;

    @BeforeEach
    void setUp() {
        controller = new RecordatorioController(
                crearRecordatorio, obtenerRecordatorioById, listarRecordatoriosByDesarrollador,
                actualizarRecordatorio, eliminarRecordatorio, completarRecordatorio,
                posponerRecordatorio, actualizarRecordatorioFechaFinalizacion, actualizarRecordatorioTitulo
        );
    }

    private RecordatorioResponse stubResponse(String id) {
        return new RecordatorioResponse(id, "user-1", "Título", true,
                LocalDateTime.now().plusDays(1), LocalDateTime.now());
    }

    @Nested
    @DisplayName("POST / — crear recordatorio")
    class Create {

        @Test
        @DisplayName("Debe retornar 201 CREATED con el body del use case")
        void shouldReturn201WithBody() {
            // Given
            CrearRecordatorioRequest request = new CrearRecordatorioRequest(
                    "user-1", "Título", LocalDateTime.now().plusDays(1));
            RecordatorioResponse expected = stubResponse("rec-1");
            when(crearRecordatorio.execute(request)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.create(request);

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expected, response.getBody());
            verify(crearRecordatorio).execute(request);
        }
    }

    @Nested
    @DisplayName("GET /{id} — obtener por id")
    class FindById {

        @Test
        @DisplayName("Debe retornar 200 OK con el body del use case")
        void shouldReturn200WithBody() {
            // Given
            RecordatorioResponse expected = stubResponse("rec-2");
            when(obtenerRecordatorioById.execute("rec-2")).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.findById("rec-2");

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
            List<RecordatorioResponse> expected = List.of(stubResponse("r1"), stubResponse("r2"));
            when(listarRecordatoriosByDesarrollador.execute("user-1")).thenReturn(expected);

            // When
            ResponseEntity<List<RecordatorioResponse>> response = controller.findByDesarrollador("user-1");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("PUT /{id} — actualizar recordatorio")
    class Update {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarRecordatorioRequest request = new ActualizarRecordatorioRequest(
                    "Nuevo", true, LocalDateTime.now().plusDays(2));
            RecordatorioResponse expected = stubResponse("rec-3");
            when(actualizarRecordatorio.execute("rec-3", request)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.update("rec-3", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/title — actualizar título")
    class UpdateTitulo {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarRecordatorioTituloRequest request = new ActualizarRecordatorioTituloRequest("Nuevo Título");
            RecordatorioResponse expected = stubResponse("rec-4");
            when(actualizarRecordatorioTitulo.execute("rec-4", request)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.updateTitulo("rec-4", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/date-end — actualizar fecha de finalización")
    class UpdateFechaFinalizacion {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con id y request correctos")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            ActualizarRecordatorioFechaFinalizacionRequest request =
                    new ActualizarRecordatorioFechaFinalizacionRequest(LocalDateTime.now().plusDays(5));
            RecordatorioResponse expected = stubResponse("rec-5");
            when(actualizarRecordatorioFechaFinalizacion.execute("rec-5", request)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.updateFechaFinalizacion("rec-5", request);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
        }
    }

    @Nested
    @DisplayName("DELETE /{id} — eliminar recordatorio")
    class Delete {

        @Test
        @DisplayName("Debe retornar 204 NO CONTENT y llamar al use case con el id correcto")
        void shouldReturn204AndCallUseCase() {
            // When
            ResponseEntity<Void> response = controller.delete("rec-6");

            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(eliminarRecordatorio).execute("rec-6");
        }
    }

    @Nested
    @DisplayName("PATCH /{id}/completar — completar recordatorio")
    class Completar {

        @Test
        @DisplayName("Debe retornar 200 OK y delegar al use case con el id correcto")
        void shouldReturn200AndDelegateToUseCase() {
            // Given
            RecordatorioResponse expected = stubResponse("rec-7");
            when(completarRecordatorio.execute("rec-7")).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.completar("rec-7");

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
            verify(completarRecordatorio).execute("rec-7");
        }
    }

    @Nested
    @DisplayName("POST /{id}/posponer — posponer recordatorio")
    class Posponer {

        @Test
        @DisplayName("Debe retornar 200 OK y pasar id y minutos al use case")
        void shouldReturn200AndPassIdAndMinutesToUseCase() {
            // Given
            RecordatorioResponse expected = stubResponse("rec-8");
            when(posponerRecordatorio.execute("rec-8", 15)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.posponer("rec-8", 15);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expected, response.getBody());
            verify(posponerRecordatorio).execute("rec-8", 15);
        }

        @Test
        @DisplayName("Debe usar 10 minutos como valor por defecto cuando no se especifica")
        void shouldUseDefaultOf10MinutesWhenNotSpecified() {
            // Given
            RecordatorioResponse expected = stubResponse("rec-9");
            when(posponerRecordatorio.execute("rec-9", 10)).thenReturn(expected);

            // When
            ResponseEntity<RecordatorioResponse> response = controller.posponer("rec-9", 10);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(posponerRecordatorio).execute("rec-9", 10);
        }
    }
}
