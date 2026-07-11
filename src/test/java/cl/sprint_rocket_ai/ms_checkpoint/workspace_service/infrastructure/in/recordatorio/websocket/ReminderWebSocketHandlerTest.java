package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReminderWebSocketHandler")
class ReminderWebSocketHandlerTest {

    @Mock
    private WebSocketSession session;

    // Handler bajo prueba — instanciado directamente (sin Spring)
    private ReminderWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ReminderWebSocketHandler();
    }

    private void mockSessionWithUserId(WebSocketSession s, String userId, String sessionId) throws Exception {
        when(s.getId()).thenReturn(sessionId);
        URI uri = new URI("ws://localhost:8080/ws?userId=" + userId);
        when(s.getUri()).thenReturn(uri);
    }

    private void mockSessionWithNoQuery(WebSocketSession s, String sessionId) throws Exception {
        when(s.getId()).thenReturn(sessionId);
        URI uri = new URI("ws://localhost:8080/ws");
        when(s.getUri()).thenReturn(uri);
    }

    // ─── afterConnectionEstablished ───────────────────────────────────────────

    @Nested
    @DisplayName("afterConnectionEstablished")
    class AfterConnectionEstablished {

        @Test
        @DisplayName("Debe registrar la sesión cuando el userId es válido")
        void shouldRegisterSessionWhenUserIdIsValid() throws Exception {
            // Given
            mockSessionWithUserId(session, "user-1", "sess-1");

            // When
            handler.afterConnectionEstablished(session);

            // Then
            assertEquals(1, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe permitir múltiples sesiones para un mismo userId")
        void shouldAllowMultipleSessionsForSameUser() throws Exception {
            // Given
            WebSocketSession session2 = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-1", "sess-1");
            mockSessionWithUserId(session2, "user-1", "sess-2");

            // When
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(session2);

            // Then — sigue siendo 1 usuario único, con 2 sesiones
            assertEquals(1, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe registrar usuarios distintos como entradas separadas")
        void shouldRegisterDistinctUsersAsSeparateEntries() throws Exception {
            // Given
            WebSocketSession session2 = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-1", "sess-1");
            mockSessionWithUserId(session2, "user-2", "sess-2");

            // When
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(session2);

            // Then
            assertEquals(2, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe cerrar la sesión cuando no se proporciona userId en la query")
        void shouldCloseSessionWhenNoUserIdInQuery() throws Exception {
            // Given
            mockSessionWithNoQuery(session, "sess-sin-userId");

            // When
            handler.afterConnectionEstablished(session);

            // Then
            verify(session).close(CloseStatus.POLICY_VIOLATION);
            assertEquals(0, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe cerrar la sesión cuando el URI es nulo")
        void shouldCloseSessionWhenUriIsNull() throws Exception {
            // Given
            when(session.getId()).thenReturn("sess-null-uri");
            when(session.getUri()).thenReturn(null);

            // When
            handler.afterConnectionEstablished(session);

            // Then
            verify(session).close(CloseStatus.POLICY_VIOLATION);
            assertEquals(0, handler.getActiveUserCount());
        }
    }

    // ─── afterConnectionClosed ────────────────────────────────────────────────

    @Nested
    @DisplayName("afterConnectionClosed")
    class AfterConnectionClosed {

        @Test
        @DisplayName("Debe eliminar la sesión del usuario cuando se cierra la conexión")
        void shouldRemoveSessionWhenConnectionClosed() throws Exception {
            // Given
            mockSessionWithUserId(session, "user-1", "sess-1");
            handler.afterConnectionEstablished(session);

            // When
            handler.afterConnectionClosed(session, CloseStatus.NORMAL);

            // Then
            assertEquals(0, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe eliminar la entrada del usuario cuando cierra su única sesión")
        void shouldRemoveUserEntryWhenLastSessionClosed() throws Exception {
            // Given
            mockSessionWithUserId(session, "user-solo", "sess-1");
            handler.afterConnectionEstablished(session);

            // When
            handler.afterConnectionClosed(session, CloseStatus.NORMAL);

            // Then
            assertEquals(0, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe mantener otras sesiones del usuario cuando solo cierra una")
        void shouldKeepOtherSessionsWhenOneCloses() throws Exception {
            // Given
            WebSocketSession session2 = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-1", "sess-1");
            mockSessionWithUserId(session2, "user-1", "sess-2");
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(session2);

            // When
            handler.afterConnectionClosed(session, CloseStatus.NORMAL);

            // Then — user-1 sigue activo con sess-2
            assertEquals(1, handler.getActiveUserCount());
        }
    }

    // ─── sendToUser ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("sendToUser")
    class SendToUser {

        @Test
        @DisplayName("Debe enviar el mensaje a la sesión abierta del usuario")
        void shouldSendMessageToOpenSession() throws Exception {
            // Given
            mockSessionWithUserId(session, "user-1", "sess-1");
            when(session.isOpen()).thenReturn(true);
            handler.afterConnectionEstablished(session);

            // When
            handler.sendToUser("user-1", "{\"type\":\"test\"}");

            // Then
            ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
            verify(session).sendMessage(captor.capture());
            assertEquals("{\"type\":\"test\"}", captor.getValue().getPayload());
        }

        @Test
        @DisplayName("No debe enviar mensaje cuando la sesión está cerrada")
        void shouldNotSendMessageWhenSessionIsClosed() throws Exception {
            // Given
            mockSessionWithUserId(session, "user-1", "sess-1");
            when(session.isOpen()).thenReturn(false);
            handler.afterConnectionEstablished(session);

            // When
            handler.sendToUser("user-1", "{\"type\":\"test\"}");

            // Then
            verify(session, never()).sendMessage(any());
        }

        @Test
        @DisplayName("No debe lanzar excepción cuando no existen sesiones para el userId")
        void shouldNotThrowWhenNoSessionsForUser() {
            // When / Then
            assertDoesNotThrow(() -> handler.sendToUser("user-sin-sesion", "{}"));
        }

        @Test
        @DisplayName("Debe enviar el mensaje a todas las sesiones abiertas del usuario")
        void shouldSendMessageToAllOpenSessionsOfUser() throws Exception {
            // Given
            WebSocketSession session2 = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-multi", "sess-1");
            mockSessionWithUserId(session2, "user-multi", "sess-2");
            when(session.isOpen()).thenReturn(true);
            when(session2.isOpen()).thenReturn(true);
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(session2);

            // When
            handler.sendToUser("user-multi", "{\"type\":\"ping\"}");

            // Then
            verify(session).sendMessage(any(TextMessage.class));
            verify(session2).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("No debe enviar a un userId distinto del destinatario")
        void shouldNotSendToOtherUsers() throws Exception {
            // Given
            WebSocketSession sessionOtro = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-destino", "sess-1");
            mockSessionWithUserId(sessionOtro, "user-otro", "sess-2");
            when(session.isOpen()).thenReturn(true);
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(sessionOtro);

            // When
            handler.sendToUser("user-destino", "{}");

            // Then
            verify(session).sendMessage(any(TextMessage.class));
            verify(sessionOtro, never()).sendMessage(any());
        }
    }

    // ─── handleTransportError ─────────────────────────────────────────────────

    @Nested
    @DisplayName("handleTransportError")
    class HandleTransportError {

        @Test
        @DisplayName("Debe cerrar la sesión cuando ocurre un error de transporte")
        void shouldCloseSessionOnTransportError() throws Exception {
            // Given
            when(session.getId()).thenReturn("sess-error");

            // When
            handler.handleTransportError(session, new RuntimeException("Error de red"));

            // Then
            verify(session).close(CloseStatus.POLICY_VIOLATION);
        }

        @Test
        @DisplayName("No debe propagar la excepción de transporte al llamador")
        void shouldNotPropagateTransportException() {
            // Given
            when(session.getId()).thenReturn("sess-error");

            // When / Then
            assertDoesNotThrow(() ->
                    handler.handleTransportError(session, new RuntimeException("Fallo de red")));
        }
    }

    // ─── getActiveUserCount ───────────────────────────────────────────────────

    @Nested
    @DisplayName("getActiveUserCount")
    class GetActiveUserCount {

        @Test
        @DisplayName("Debe retornar 0 cuando no hay sesiones activas")
        void shouldReturnZeroWhenNoActiveSessions() {
            // When / Then
            assertEquals(0, handler.getActiveUserCount());
        }

        @Test
        @DisplayName("Debe retornar el número de usuarios únicos con sesiones activas")
        void shouldReturnUniqueUserCount() throws Exception {
            // Given
            WebSocketSession session2 = mock(WebSocketSession.class);
            mockSessionWithUserId(session, "user-1", "sess-1");
            mockSessionWithUserId(session2, "user-2", "sess-2");
            handler.afterConnectionEstablished(session);
            handler.afterConnectionEstablished(session2);

            // When / Then
            assertEquals(2, handler.getActiveUserCount());
        }
    }
}
