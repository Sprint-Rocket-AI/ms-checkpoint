package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.observer;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.websocket.ReminderWebSocketHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReminderEventListener")
class ReminderEventListenerTest {

    @Mock
    private ReminderWebSocketHandler reminderWebSocketHandler;

    @InjectMocks
    private ReminderEventListener reminderEventListener;

    private ReminderTriggeredEvent buildEvent(String recordatorioId, String userId, String titulo, int count) {
        return new ReminderTriggeredEvent(this, recordatorioId, userId, titulo, count);
    }

    @Nested
    @DisplayName("Cuando se recibe un ReminderTriggeredEvent")
    class CuandoSeRecibeEvento {

        @Test
        @DisplayName("Debe llamar a sendToUser con el userId del evento")
        void shouldCallSendToUserWithEventUserId() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-1", "Reunión de equipo", 2);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(eq("user-1"), anyString());
        }

        @Test
        @DisplayName("Debe llamar a sendToUser exactamente una vez por evento")
        void shouldCallSendToUserExactlyOnce() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-2", "user-2", "Deploy a producción", 1);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler, times(1)).sendToUser(anyString(), anyString());
            verifyNoMoreInteractions(reminderWebSocketHandler);
        }

        @Test
        @DisplayName("Debe incluir type 'reminder.triggered' en el cuerpo del mensaje enviado")
        void shouldIncludeReminderTriggeredTypeInMessage() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-3", "user-3", "Code review", 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("\"type\": \"reminder.triggered\""));
        }

        @Test
        @DisplayName("Debe incluir el recordatorioId en el payload del mensaje")
        void shouldIncludeRecordatorioIdInPayload() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-99", "user-1", "Título", 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("\"reminderId\": \"rec-99\""));
        }

        @Test
        @DisplayName("Debe incluir el userId en el payload del mensaje")
        void shouldIncludeUserIdInPayload() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-abc", "Título", 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("\"userId\": \"user-abc\""));
        }

        @Test
        @DisplayName("Debe incluir el titulo en el payload del mensaje")
        void shouldIncludeTituloInPayload() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-1", "Reunión de planificación", 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("Reunión de planificación"));
        }

        @Test
        @DisplayName("Debe incluir el reminderCount en el payload del mensaje")
        void shouldIncludeReminderCountInPayload() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-1", "Título", 5);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("\"reminderCount\": 5"));
        }

        @Test
        @DisplayName("Debe escapar comillas dobles en el titulo para producir JSON válido")
        void shouldEscapeDoubleQuotesInTitulo() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-1", "Deploy \"producción\"", 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When
            reminderEventListener.onReminderTriggered(event);

            // Then
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("Deploy \\\"producción\\\""));
        }

        @Test
        @DisplayName("Debe manejar titulo nulo sin lanzar excepción y usar cadena vacía en el mensaje")
        void shouldHandleNullTituloWithoutException() {
            // Given
            ReminderTriggeredEvent event = buildEvent("rec-1", "user-1", null, 1);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // When / Then
            assertDoesNotThrow(() -> reminderEventListener.onReminderTriggered(event));
            verify(reminderWebSocketHandler).sendToUser(anyString(), messageCaptor.capture());
            assertTrue(messageCaptor.getValue().contains("\"titulo\": \"\""));
        }
    }
}
