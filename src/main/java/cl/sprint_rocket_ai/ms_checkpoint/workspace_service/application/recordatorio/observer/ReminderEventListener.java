package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.recordatorio.observer;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.domain.events.ReminderTriggeredEvent;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.websocket.ReminderWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class ReminderEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReminderEventListener.class);

    private final ReminderWebSocketHandler reminderWebSocketHandler;

    public ReminderEventListener(ReminderWebSocketHandler reminderWebSocketHandler) {
        this.reminderWebSocketHandler = reminderWebSocketHandler;
    }

    @EventListener
    public void onReminderTriggered(ReminderTriggeredEvent event) {
        log.info("Evento recibido: ReminderTriggeredEvent | userId='{}' recordatorioId='{}' titulo='{}'",
                event.getUserId(), event.getRecordatorioId(), event.getTitulo());

        String message = buildWsMessage(event);
        reminderWebSocketHandler.sendToUser(event.getUserId(), message);

        log.info("Mensaje WebSocket despachado al usuario | userId='{}'", event.getUserId());
    }

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

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
