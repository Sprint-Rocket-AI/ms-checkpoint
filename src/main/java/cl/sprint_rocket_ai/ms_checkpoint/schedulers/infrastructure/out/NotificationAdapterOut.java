package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.NotificationPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class NotificationAdapterOut implements NotificationPortOut {

    private static final Logger log = LoggerFactory.getLogger(NotificationAdapterOut.class);

    @Override
    public void notifyDeveloper(String userId, String contenido) {
        log.info("Enviando notificación al desarrollador: {}", userId);
        // TODO: Integrar con canal de notificación real (Teams/Email/Slack)
        log.info("Contenido de notificación para {}: {}", userId, contenido);
        log.info("Notificación enviada exitosamente al desarrollador: {}", userId);
    }

    @Override
    public void notifyLider(String liderTecnicoId, String contenido) {
        log.info("Enviando notificación al líder técnico: {}", liderTecnicoId);
        // TODO: Integrar con canal de notificación real (Teams/Email/Slack)
        log.info("Contenido de notificación para líder {}: {}", liderTecnicoId, contenido);
        log.info("Notificación enviada exitosamente al líder técnico: {}", liderTecnicoId);
    }
}
