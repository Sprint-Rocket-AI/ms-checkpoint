package cl.sprint_rocket_ai.ms_checkpoint.gestionar_recordatorios.infrastructure.in.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuración WebSocket de ms-checkpoint.
 *
 * <p>Expone el endpoint: {@code ws://<host>/ws/reminders?userId=<userId>}
 *
 * <p>El cliente frontend debe conectarse pasando su {@code userId} como query param.
 * Se permiten todos los orígenes ({@code *}) para compatibilidad con entornos de desarrollo;
 * en producción restringir al dominio del frontend.
 *
 * <pre>
 * // Ejemplo de conexión desde el frontend:
 * const ws = new WebSocket('ws://localhost:8082/ws/reminders?userId=dev-001');
 * ws.onmessage = (event) => {
 *   const msg = JSON.parse(event.data);
 *   if (msg.type === 'reminder.triggered') showPopup(msg.payload);
 * };
 * </pre>
 */
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
