package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.application.observer;

import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.websocket.ReminderWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer del patrón Observer.
 *
 * <p>Escucha {@link ReminderTriggeredEvent} publicados por {@code ReminderPollingScheduler}
 * y los reenvía al frontend vía WebSocket usando {@link ReminderWebSocketHandler}.
 *
 * <p>El mensaje enviado al cliente tiene el formato:
 * <pre>
 * {
 *   "type": "reminder.triggered",
 *   "payload": {
 *     "reminderId": "...",
 *     "userId": "...",
 *     "titulo": "...",
 *     "reminderCount": 1
 *   }
 * }
 * </pre>
 */
@Component
public class ReminderEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReminderEventListener.class);

    private final ReminderWebSocketHandler reminderWebSocketHandler;

    public ReminderEventListener(ReminderWebSocketHandler reminderWebSocketHandler) {
        this.reminderWebSocketHandler = reminderWebSocketHandler;
    }

    /**
     * Recibe el evento y envía el mensaje WebSocket al usuario correspondiente.
     *
     * @param event evento publicado por el scheduler cuando un recordatorio vence
     */
    @EventListener
    public void onReminderTriggered(ReminderTriggeredEvent event) {
        log.info("Evento recibido: ReminderTriggeredEvent | userId='{}' recordatorioId='{}' titulo='{}'",
                event.getUserId(), event.getRecordatorioId(), event.getTitulo());

        String message = buildWsMessage(event);
        reminderWebSocketHandler.sendToUser(event.getUserId(), message);

        log.info("Mensaje WebSocket despachado al usuario | userId='{}'", event.getUserId());
    }

    /**
     * Serializa el evento como JSON para el mensaje WebSocket.
     * Se construye manualmente para evitar dependencia de ObjectMapper en el observer.
     */
    private String buildWsMessage(ReminderTriggeredEvent event) {
        return String.format("""
                {
                  "type": "reminder.triggered",
                  "payload": {
                    "reminderId": "%s",
                    "userId": "%s",
                    "titulo": "%s",
                    "reminderCount": %d
                  }
                }""",
                event.getRecordatorioId(),
                event.getUserId(),
                escapeJson(event.getTitulo()),
                event.getReminderCount()
        );
    }

    /** Escapa comillas dobles en el título para JSON válido. */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
