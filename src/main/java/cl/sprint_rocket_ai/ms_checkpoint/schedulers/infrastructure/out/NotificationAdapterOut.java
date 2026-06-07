package cl.sprint_rocket_ai.ms_checkpoint.schedulers.infrastructure.out;

import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.models.SugerenciaActividad;
import cl.sprint_rocket_ai.ms_checkpoint.schedulers.domain.ports.out.NotificationPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador de notificaciones.
 *
 * <p>Actualmente formatea el contenido y lo loguea simulando el envío de correo.
 * TODO: integrar con JavaMail/SMTP o un servicio de mensajería (Teams, Slack) para envío real.
 */
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

    @Override
    public void notifyDeveloperWithSummary(String userId, String resumen, List<SugerenciaActividad> sugerencias) {
        log.info("=== CORREO RESUMEN DIARIO ===");
        log.info("Para: {} <{}@empresa.com>", userId, userId);
        log.info("Asunto: [SpringRocket] Resumen ejecutivo de actividades del día");
        log.info("----------------------------------------------------------");
        log.info("Estimado desarrollador {},", userId);
        log.info("");
        log.info("A continuación encontrarás el resumen ejecutivo de tus actividades del día:");
        log.info("");
        log.info("📋 RESUMEN EJECUTIVO:");
        log.info("{}", resumen);
        log.info("");
        log.info("💡 ACTIVIDADES SUGERIDAS PARA MAÑANA:");
        if (sugerencias == null || sugerencias.isEmpty()) {
            log.info("  No se generaron sugerencias automáticas.");
        } else {
            for (int i = 0; i < sugerencias.size(); i++) {
                SugerenciaActividad s = sugerencias.get(i);
                log.info("  {}. [{}] {}", i + 1, s.prioridad(), s.titulo());
                log.info("     {}", s.descripcion());
                log.info("     Razón: {}", s.razon());
            }
        }
        log.info("");
        log.info("Este correo fue generado automáticamente por SpringRocket IA.");
        log.info("=== FIN CORREO RESUMEN DIARIO ===");
        // TODO: Reemplazar logs con envío real vía JavaMail o cliente SMTP
        // mailSender.send(buildMimeMessage(userId, resumen, sugerencias));
    }
}
