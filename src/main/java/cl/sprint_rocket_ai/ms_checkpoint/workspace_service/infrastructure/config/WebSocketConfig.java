package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.config;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.websocket.ReminderWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ReminderWebSocketHandler reminderWebSocketHandler;

    public WebSocketConfig(ReminderWebSocketHandler reminderWebSocketHandler) {
        this.reminderWebSocketHandler = reminderWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(reminderWebSocketHandler, "/ws/reminders")
                .setAllowedOrigins("*");
    }
}
