package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.recordatorio.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReminderWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ReminderWebSocketHandler.class);

    /**
     * Mapa de sesiones activas por userId.
     * Un mismo usuario puede tener múltiples pestañas/clientes conectados.
     */
    private final ConcurrentHashMap<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    // ─── Lifecycle ──────────────────────────────────────────────────────────────

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId == null || userId.isBlank()) {
            log.warn("Conexión WebSocket rechazada — userId no proporcionado | sessionId='{}'",
                    session.getId());
            closeQuietly(session);
            return;
        }

        sessions.computeIfAbsent(userId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(session);

        log.info("Conexión WebSocket establecida | userId='{}' sessionId='{}' totalSessions={}",
                userId, session.getId(), sessions.get(userId).size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            Set<WebSocketSession> userSessions = sessions.get(userId);
            if (userSessions != null) {
                userSessions.remove(session);
                if (userSessions.isEmpty()) {
                    sessions.remove(userId);
                }
            }
        }
        log.info("Conexión WebSocket cerrada | userId='{}' sessionId='{}' status='{}'",
                userId, session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Error de transporte WebSocket | sessionId='{}': {}",
                session.getId(), exception.getMessage());
        closeQuietly(session);
    }

    public void sendToUser(String userId, String message) {
        Set<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions == null || userSessions.isEmpty()) {
            log.info("No hay sesiones WebSocket activas para userId='{}' — recordatorio no enviado", userId);
            return;
        }

        TextMessage textMessage = new TextMessage(message);
        userSessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                    log.info("Mensaje WebSocket enviado | userId='{}' sessionId='{}'",
                            userId, session.getId());
                } catch (IOException e) {
                    log.error("Error enviando mensaje WebSocket | userId='{}' sessionId='{}': {}",
                            userId, session.getId(), e.getMessage());
                }
            }
        });
    }


    public int getActiveUserCount() {
        return sessions.size();
    }


    private String extractUserId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && "userId".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private void closeQuietly(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (IOException e) {
            log.warn("Error cerrando sesión WebSocket | sessionId='{}': {}", session.getId(), e.getMessage());
        }
    }
}
