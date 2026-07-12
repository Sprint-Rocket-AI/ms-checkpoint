package cl.sprint_rocket_ai.ms_checkpoint.mcp_tools.application;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.CrearRecordatorio;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.ListarRecordatoriosByDesarrollador;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.CrearRecordatorioRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.dtos.RecordatorioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordatorioTools")
class RecordatorioToolsTest {

    @Mock private CrearRecordatorio crearRecordatorio;
    @Mock private ListarRecordatoriosByDesarrollador listarRecordatoriosByDesarrollador;

    @InjectMocks
    private RecordatorioTools recordatorioTools;

    private RecordatorioResponse stubResponse(String id) {
        return new RecordatorioResponse(id, "user-1", "Título", true,
                LocalDateTime.now().plusDays(1), LocalDateTime.now());
    }

    @Nested
    @DisplayName("crearRecordatorio")
    class CrearRecordatorioTests {

        @Test
        @DisplayName("Debe parsear la fechaExpiracion ISO y construir el request correctamente")
        void shouldParseIsoDateAndBuildRequest() {
            // Given
            RecordatorioResponse expected = stubResponse("rec-1");
            when(crearRecordatorio.execute(any(CrearRecordatorioRequest.class))).thenReturn(expected);
            ArgumentCaptor<CrearRecordatorioRequest> captor = ArgumentCaptor.forClass(CrearRecordatorioRequest.class);

            // When
            RecordatorioResponse result = recordatorioTools.crearRecordatorio(
                    "user-1",  "2026-12-31T23:59:59", null);

            // Then
            verify(crearRecordatorio).execute(captor.capture());
            assertEquals("user-1", captor.getValue().userId());
            assertEquals("Reunión", captor.getValue().titulo());
            assertNotNull(captor.getValue().fechaExpiracion());
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Debe usar fechaExpiracion nula cuando no se proporciona fecha")
        void shouldUseNullExpirationWhenNoDateProvided() {
            // Given
            when(crearRecordatorio.execute(any())).thenReturn(stubResponse("rec-2"));
            ArgumentCaptor<CrearRecordatorioRequest> captor = ArgumentCaptor.forClass(CrearRecordatorioRequest.class);

            // When
            recordatorioTools.crearRecordatorio("user-1", "Tarea", null);

            // Then
            verify(crearRecordatorio).execute(captor.capture());
            assertNull(captor.getValue().fechaExpiracion());
        }

        @Test
        @DisplayName("Debe usar fechaExpiracion nula cuando la cadena de fecha está en blanco")
        void shouldUseNullExpirationWhenDateIsBlank() {
            // Given
            when(crearRecordatorio.execute(any())).thenReturn(stubResponse("rec-3"));
            ArgumentCaptor<CrearRecordatorioRequest> captor = ArgumentCaptor.forClass(CrearRecordatorioRequest.class);

            // When
            recordatorioTools.crearRecordatorio("user-1", "Tarea", null);

            // Then
            verify(crearRecordatorio).execute(captor.capture());
            assertNull(captor.getValue().fechaExpiracion());
        }

        @Test
        @DisplayName("Debe usar now+1 día como fallback cuando la fecha no puede parsearse")
        void shouldUseTomorrowAsFallbackWhenDateCannotBeParsed() {
            // Given
            when(crearRecordatorio.execute(any())).thenReturn(stubResponse("rec-4"));
            ArgumentCaptor<CrearRecordatorioRequest> captor = ArgumentCaptor.forClass(CrearRecordatorioRequest.class);
            LocalDateTime antes = LocalDateTime.now().plusDays(1).minusSeconds(2);

            // When
            recordatorioTools.crearRecordatorio("user-1", "fecha-invalida", null);

            // Then
            verify(crearRecordatorio).execute(captor.capture());
            LocalDateTime expiracion = captor.getValue().fechaExpiracion();
            assertNotNull(expiracion);
            assertFalse(expiracion.isBefore(antes));
        }
    }

    @Nested
    @DisplayName("listarRecordatoriosByDesarrollador")
    class ListarTests {

        @Test
        @DisplayName("Debe delegar al use case con el userId correcto y retornar su resultado")
        void shouldDelegateToUseCaseAndReturnResult() {
            // Given
            List<RecordatorioResponse> expected = List.of(stubResponse("r1"), stubResponse("r2"));
            when(listarRecordatoriosByDesarrollador.execute("user-1")).thenReturn(expected);

            // When
            List<RecordatorioResponse> result = recordatorioTools.listarRecordatoriosByDesarrollador(null);

            // Then
            assertEquals(2, result.size());
            verify(listarRecordatoriosByDesarrollador).execute("user-1");
        }
    }
}
